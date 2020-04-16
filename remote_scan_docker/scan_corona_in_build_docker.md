# Embold Scan with docker - Embold Analyser inside build container

You are building your C/C++ projects in a docker container via your custom image, which has the build tools, compilers, dependencies, etc.
This article describes how to add Embold analyser (a.k.a corona) to your docker-based build for C/C++ projects, and then run an Embold scan from within docker.

The typical setup for this is as shown below:

```console
|---------------------------|                       |---------------------------|
|  Docker Host              |                       |                           |
|                           |                       |                           |
|  |--------------------|   |            3000 (web) |                           |
|  |  Build Container   |--------------------> *----|       Embold Server       |
|  |  +Embold Scanner   |--------------------> *----|                           |
|  |--------------------|   |           5432 (data) |                           |
|---------------------------|                       |---------------------------|
```
This article assumes you already have a docker image which you use to build your software by launching a container of this image, and also you can modify this image by adding additional Embold-specific components.
The modified image will then be able to run an Embold scan after your usual build, from within the container.

In this example, we will build the curl repository: https://github.com/curl/curl.git

This project uses `cmake` and `make` to build from source, so we make sure those tools are in our docker image

## Build the custom Docker image (Your build tools + Embold components)

### Pre-requisites

- You have a docker image to build your software with all the necessary tools.
- You have downloaded the Embold Analyser (corona-archive.tar.gz) from your embold.io account (version 1.7.8.0 or later as of this writing)

In our example for curl, the Dockerfile looks like this (assuming an Ubuntu 18 image supported by Embold):

```Dockerfile
FROM ubuntu:18.04
  
# YOUR stuff needed for the build to work
RUN apt-get update -y \
    && apt-get install apt-utils -y \
    && apt-get upgrade -y \
    && apt-get install build-essential -y \
    && apt-get install libssl-dev -y \
    && apt-get install cmake -y
```
Your actual build image may be more complex depending on which compilers, tools, external dependencies you need to build your software.

You now need to add additional Embold-specific components to your Dockerfile and rebuild the image.
The new Dockerfile now looks like this:

```Dockerfile
FROM ubuntu:18.04
  
# YOUR stuff needed for the build to work
RUN apt-get update -y \
    && apt-get install apt-utils -y \
    && apt-get upgrade -y \
    && apt-get install build-essential -y \
    && apt-get install libssl-dev -y \
    && apt-get install cmake -y

########################################
# Additional Embold-specific components
########################################

# JRE - if your image already has JRE >= 8 <= 11, you don't need this step
RUN apt-get install default-jre -y

# Required for embold trace tool (gamma-trace) to monitor the build process
RUN apt-get install strace -y

# This is the embold analyser archive file (a.k.a corona)
ADD corona-archive.tar.gz /embold
```

**Note:** the the Embold corona component (corona-archive.tar.gz) should be available offline on the directory where you have the Dockerfile


Now you can build the image:

```sh
$ docker build .
```
And then tag it to any name (in our example, fabrikam/builder:1.0)

```sh
$ docker tag <image id> fabrikam/builder:1.0
```

With our custom image built (build system + Embold components), we can now configure and run the build and scan

## Build the sample C/C++ project and run a scan
### Setup and Configuration


1. Prepare a top-level directory (e.g. `/home/johndoe/docker_build/curl_scan`)

    **Note:** We will map the directory `/home/johndoe/docker_build` to `/docker_build` while running the docker container, with the `-v` option

2. Clone the source somewhere inside this directory, e.g. `/home/johndoe/docker_build/curl_scan/curl`

3. Now create a **Project** in Embold which will hold the repository we want to scan. More info here: <https://docs.embold.io/projects-repositories/#create-a-project>

4. Next, link a new **Remote Repository** to this Project and give it a name, e.g. `curl`. More info here: <https://docs.embold.io/projects-repositories/#link-a-repository>
    
    Make sure the **Repository Type** is **Remote** and the Language is set to **C/C++**

5. Download the repository configuration (`repository-configuration.json`) of this newly-created repository on the host where we will run the scan. In our example, copy it to: `/home/johndoe/docker_build/curl_scan`

    **Note:** You can download the JSON by selecting the **"..."** option on the **Remote Repository** we just created, and then the "Download repository configuration" option

6. Modify the following elements of the repository-configuration.json:
    - `gammaAccess/url`: Your Embold Server Url (e.g. `http://<embold_host>:3000`)
    - `gammaAccess/userName`: Your Embold username
    - `gammaAccess/password`: Your Embold password. If you are using **Embold Access Token**, change the field name `password` to `token` and paste the token instead of password (More info on Embold Access Token here: <https://docs.embold.io/gamma-access-token-gat/#gamma-access-token-gat>)
    - `repositories/dataDir`: Set it to a path visible to docker container. In this example, set it to: `/docker_build/curl_scan/embold_data`
    - `repositories/sources/baseDir`: Set it to the source root where you cloned `curl` sources (the path visible from container). In our example, set it to: 
`/docker_build/curl_scan/curl`

7. Prepare some scripts at `/home/johndoe/docker_build/curl_scan`

- **Clean script (`clean.sh`)**

    ```sh
    make -C /docker_build/curl_scan/curl clean
    # The file below is generated by the embold trace tool, so delete it as part of clean step
    rm -f /docker_build/curl_scan/curl/compile_commands.json
    ```

    The above script just does a `make clean` and removes the previous `compile_commands.json`

- **Build script (`build.sh`)**

    ```sh
    cd /docker_build/curl_scan/curl && cmake . -G"Unix Makefiles"
    /embold/corona/cxxparser/bin/gamma-trace -o /docker_build/curl_scan/curl make -j4 -C /docker_build/curl_scan/curl
    ```

    This script first runs cmake, which generates the Makefile

    **The next (and important) step** invokes `gamma-trace` followed by the actual build command (`make` in this case). `gamma-trace` monitors the build, and creates the `compile_commands.json` at `/docker_build/curl_scan/curl`

- **Scan script (`scan.sh`)**

    ```sh
    /embold/corona/scanboxwrapper/bin/gammascanner -c /docker_build/curl_scan/repository-configuration.json
    ```

    This script launches the Embold scan after the build is done. The Embold scanner will pickup the `compile_commands.json` from the source directory where we created it during the build step with `gamma-trace`

8. Putting it all together

    Now create a wrapper script which you can launch from the host. This script will launch the above 3 scripts in our custom `fabrikam/builder` container:

- **Wrapper script (`wrapper-build-scan.sh`)**

    ```sh
    #!/bin/sh

    # Runs clean in the build container
    docker run -v /home/johndoe/docker_build:/docker_build fabrikam/builder:1.0 sh /docker_build/curl_scan/clean.sh

    # Runs gamma-trace + build in the build container and generates compile_commands.json
    docker run --security-opt seccomp:unconfined -v /home/johndoe/docker_build:/docker_build fabrikam/builder:1.0 sh /docker_build/curl_scan/build.sh

    # Runs Embold scan in the Embold (corona) container, and consumes the generated compile_commands.json + source code
    docker run -e CORONA_HOME=/embold/corona -e CORONA_LOG=/docker_build/curl_scan/embold_logs -e ANALYSIS_MODE=remote -v /home/johndoe/docker_build:/docker_build fabrikam/builder:1.0 sh /docker_build/curl_scan/embold-scan.sh
    ```

    **Note:** The option `--security-opt seccomp:unconfined` is necessary for gamma-trace to work with the build while running `build.sh`

### Run the scan

With the setup done, we can launch the wrapper script, which cleans, builds and scans the system, and finally publishes the results to Embold Server

```sh
sh /home/johndoe/docker_build/curl_scan/wrapper-build-scan.sh
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
