# Embold Scan with docker
Embold Analyser component is also shipped as a docker image, which means you can run a scan on a remote host (e.g. a Jenkins slave) and publish the results to an Embold Server. In this case, the scan runs in its own container, and the source code is available on a mapped volume to the container.
This article explains how to do it with a few commands and scripts, with an example Java scan.

The typical setup for this is as shown below:

```console
|---------------------------|                       |---------------------------|
|  Docker Host              |                       |                           |
|                           |                       |                           |
|  |--------------------|   |            3000 (web) |                           |
|  |  Embold Container  |--------------------> *----|       Embold Server       |
|  |  <Embold Scanner>  |--------------------> *----|                           |
|  |--------------------|   |           5432 (data) |                           |
|---------------------------|                       |---------------------------|
```

The Docker Host runs the Embold Corona Container, which internally runs the Embold Scanner and publishes results to Embold Server. The Docker Host could be, for example, a Jenkins slave, which builds the sources, and then launches an Embold scan on the same sources. 

## Run a Java scan
This section applies to running a Java scan or for that matter any other supported language other than C/C++ (see the **"Run a C/C++ scan"** section  for more details)

### Pre-requisites
- Docker engine on the host where you will run the scan
- Access to the **Embold Corona** docker image (embold/corona:1.7.8.0 or later, as of this writing)
- Embold Server is setup in your environment and you have access to create a Project and Repository in Embold
- Embold Server ports (default 3000 for web and 5432 for data) are accessible from the host where the docker scan will run

### Setup and Configuration
In this example, we will scan Apache Kafka (Java) cloned from here: <https://github.com/apache/kafka.git>
1. Prepare a top-level directory for holding the source to be scanned and the scan scripts (e.g. `/home/johndoe/docker_build/kafka_scan`)

    **Note:** We will map the directory `/home/johndoe/docker_build` to `/docker_build` while running the docker container, with the `-v` option
2. Clone the source somewhere inside this directory, e.g. `/home/johndoe/docker_build/kafka_scan/kafka`
3. Now create a **Project** in Embold which will hold the repository we want to scan. More info here: <https://docs.embold.io/projects-repositories/#create-a-project>
4. Next, link a new **Remote Repository** to this Project and give it a name, e.g. `Kafka`. More info here: <https://docs.embold.io/projects-repositories/#link-a-repository>
    
    Make sure the **Repository Type** is **Remote** and the Language is set to **Java**
5. Download the repository configuration (`repository-configuration.json`) of this newly-created repository on the host where we will run the scan. In our example, copy it to: `/home/johndoe/docker_build/kafka_scan/scripts`

    **Note:** You can download the JSON by selecting the **"..."** option on the **Remote Repository** we just created, and then the "Download repository configuration" option
6. Modify the following elements of the repository-configuration.json:
    - `gammaAccess/url`: Your Embold Server Url (e.g. `http://<embold_host>:3000`)
    - `gammaAccess/userName`: Your Embold username
    - `gammaAccess/password`: Your Embold password. If you are using **Embold Access Token**, change the field name `password` to `token` and paste the token instead of password (More info on Embold Access Token here: <https://docs.embold.io/gamma-access-token-gat/#gamma-access-token-gat>)
    - `repositories/dataDir`: Set it to a path visible to docker container. In this example, set it to: `/docker_build/kafka_scan/embold_data`
    - `repositories/sources/baseDir`: Set it to the source root where you cloned Kafka sources (the path visible from container). In our example, set it to: 
`/docker_build/kafka_scan/kafka`
7. Now we need to create a scan launcher script which we will launch with the `docker run` command. This script just launches the Embold scan and specifies the repository-configuration.json we just created.

    Create a file `embold-scan.sh` under `/home/johndoe/docker_build/kafka_scan/scripts` and add this line:

    ```sh
    /opt/gamma/corona/scanboxwrapper/bin/gammascanner -c /docker_build/kafka_scan/scripts/repository-configuration.json
    ```
### Run the scan

With the setup done, we can now launch the Embold Corona container from host
- Add some necessary environment variables, and execute Embold scan with the docker run command:

    ```sh
    docker run -e CORONA_LOG=/docker_build/kafka_scan/embold_logs -e ANALYSIS_MODE=remote -v /home/johndoe/docker_build:/docker_build embold/corona:1.7.8.0 sh /docker_build/kafka_scan/scripts/embold-scan.sh
    ```

- The above command runs the scan inside the container and publishes results to the Embold Server
- The log files are placed at `$CORONA_LOG`
- You should see a console log like this if the scan results are successfully published:

    ```console
    [EMBOLD] --------------------------------------------------------
    [EMBOLD] EMBOLD ANALYSIS SUMMARY                           
    [EMBOLD] --------------------------------------------------------
    [EMBOLD] INITIALIZING GAMMASCANNER                         	[DONE]
    [EMBOLD] CHECKING EMBOLD ENVIRONMENT                       	[DONE]
    [EMBOLD] AUTHENTICATING USER                               	[DONE]

    ...

    [EMBOLD] CALCULATING RATINGS                               	[DONE]
    [EMBOLD] PUBLISHING RESULTS                                	[DONE]
    [EMBOLD] --------------------------------------------------------
    [EMBOLD] ANALYSIS SUCCESS                                  
    [EMBOLD] --------------------------------------------------------
    ```

At this point, we have successfully run an Embold scan in a docker container and published results to the Embold Server!

### Next steps

Now that we got the scan running, the next step could be to commit the `repository-configuration.json` to the source repository (rather than keeping it in the scripts directory), so it is always available to the container.

This can then be setup in a CI pipeline which is running a build, followed by the Embold scan within its own docker container.

## Run a C/C++ scan
This section adds some specifics if you want to run a C/C++ scan in remote mode with the Embold Corona docker container. We recommend running any C/C++ scan with the following 2 steps:
1. Monitor your build with the Embold trace tool (`gamma-trace`), to produce a compilation database (`compile_commands.json`).
 
    This process captures the compilation calls including header paths, switches, pre-processor definitions, etc. (as the compiler sees it), which we then use while running the actual scan.

2. Run the Embold scan by using the generated `compile_commands.json` and the source code

As you can see, the first step is something specific for C/C++, and the second step is similar to the Java scan we saw earlier (except that we are now additionally specifiying the `compile_commands.json` to the scanner as well)

There are different ways to build your code, on various platforms, build systems, compilers, etc., so the steps to generate the `compile_commands.json` vary a bit depending on these combinations (platform, build system, compiler).

### Pre-requisites
In this example, we assume the following setup:
1. You are already running your build in its own docker container (that means you run `docker run <your image> make` or something similar to build your code). We assume a Linux container, cmake and make as the build system, and gcc as the compiler
2. You are allowed to modify your image a bit by adding the [strace](https://strace.io) tool (**required for gamma-trace to work**)

Other pre-requisites are as mentioned in the section **"Run a Java scan"**

### Setup and Configuration

In this example, we will scan the Eclipse MRAA library (<https://github.com/eclipse/mraa.git>)
MRAA is built with cmake and make.

1. Prepare a top-level directory like before (e.g. `/home/johndoe/docker_build/mraa_scan`)
2. Place a copy of the Embold Analyser (`corona-archive.tar.gz`) at `/home/johndoe/docker_build` and untar it to `/home/johndoe/docker_build/corona`. You can download this file from your Embold account under the Releases section. It contains the `gamma-trace` tool which we will launch with the build in next steps
3. Prepare some scripts at `/home/johndoe/docker_build/mraa_scan/scripts`

    - **Clean script (`clean.sh`)**

    ```sh
    make -C /docker_build/mraa_scan/mraa clean
    rm -f /docker_build/mraa_scan/mraa/compile_commands.json
    ```

    The above script just does a `make clean` and removes the previous `compile_commands.json`

    This script will run inside the docker container which runs the build (and not the Embold Analyser image). In our example, this container is: `embold/ninja-build-strace-experimental:latest`. This image contains cmake, make, ninja and other build tools (used for examples only). This image also includes the `strace` tool

    **Note:** When building your code, you would use your own image instead of this one, with the additional strace tool packaged in the container

    - **Build script (`build.sh`)**

    ```sh
    cd /docker_build/mraa_scan/mraa && cmake . -G"Unix Makefiles"
    /docker_build/corona/cxxparser/bin/gamma-trace -o /docker_build/mraa_scan/mraa make -C /docker_build/mraa_scan/mraa
    ```

    This script runs cmake, which generates the Makefile

    **The next (and important) step** invokes `gamma-trace` followed by the actual build command (`make` in this case). `gamma-trace` monitors the build, and creates the `compile_commands.json` at `/docker_build/mraa_scan/mraa`

    This script will also run inside the docker container which runs the build (`embold/ninja-build-strace-experimental:latest`)

    - **Scan script (`embold-scan.sh`)**

    ```sh
    /opt/gamma/corona/scanboxwrapper/bin/gammascanner -c /docker_build/mraa_scan/scripts/repository-configuration.json
    ```

    This script launches the Embold scan after the build is done. The Embold scanner will pickup the `compile_commands.json` from the source directory where we created it during the build step with `gamma-trace`
    
    This script will run inside the Embold Analyser docker container (corona). In our example, its: `embold/corona:1.7.8.0`)

    **Note:** Before running the scan, make sure to create a **Project** and **Remote Repository** for this system in Embold Server (make sure to specify language as C/C++). Download and update the `repository-configuration.json` which we will use with the gammascanner (see steps in the **"Run a Java scan"** section)


4. Putting it all together

    Now create a wrapper script which you can launch from the host. This script will launch the above 3 scripts in their respective containers:

    - **Wrapper script (`wrapper-build-scan.sh`)**

    ```sh
    #!/bin/sh

    # Runs clean in the build container
    docker run -v /home/johndoe/docker_build:/docker_build embold/ninja-build-strace-experimental:latest sh /docker_build/mraa_scan/scripts/clean.sh

    # Runs gamma-trace + build in the build container and generates compile_commands.json
    docker run --security-opt seccomp:unconfined -v /home/johndoe/docker_build:/docker_build embold/ninja-build-strace-experimental:latest sh /docker_build/mraa_scan/scripts/build.sh

    # Runs Embold scan in the Embold (corona) container, and consumes the generated compile_commands.json + source code
    docker run -e CORONA_LOG=/docker_build/mraa_scan/embold_logs -e ANALYSIS_MODE=remote -v /home/johndoe/docker_build:/docker_build embold/corona:1.7.8.0 sh /docker_build/mraa_scan/scripts/embold-scan.sh
    ```

    **Note:** The option `--security-opt seccomp:unconfined` is necessary for gamma-trace to work with the build while running `build.sh`

### Run the scan

With the setup done, we can launch the wrapper script, which cleans, builds and scans the system, and finally publishes the results to Embold Server

```sh
sh /home/johndoe/docker_build/mraa_scan/scripts/wrapper-build-scan.sh
```

If all goes well, you would see successful scan status on console:

```console
    [EMBOLD] --------------------------------------------------------
    [EMBOLD] EMBOLD ANALYSIS SUMMARY                           
    [EMBOLD] --------------------------------------------------------
    [EMBOLD] INITIALIZING GAMMASCANNER                         	[DONE]
    [EMBOLD] CHECKING EMBOLD ENVIRONMENT                       	[DONE]
    [EMBOLD] AUTHENTICATING USER                               	[DONE]

    ...

    [EMBOLD] CALCULATING RATINGS                               	[DONE]
    [EMBOLD] PUBLISHING RESULTS                                	[DONE]
    [EMBOLD] --------------------------------------------------------
    [EMBOLD] ANALYSIS SUCCESS                                  
    [EMBOLD] --------------------------------------------------------
```









