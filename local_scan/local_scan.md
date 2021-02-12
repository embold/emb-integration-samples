# Embold Local Scan
Embold Local Scan allows the developer to locally scan the code and get immediate feedback, before committing to the remote. The results are published locally to a csv.

This article explains how to do it with a few commands and scripts, with an example Java scan.


```console
|---------------------------|                       |---------------------------|  auth   |-----------|
|       Develop             |---------------------->|           Scan            |<------->|  Server   |
|---------------------------|                       |---------------------------|         |-----------|
                        ʌ                              /
                         \                            /
                          \                          /   
                           \                        /
                            \                      V 
                          |---------------------------|
                          |        Fix Issues         |
                          |---------------------------|
```


## Run a Java scan
This section applies to running a Java scan or for that matter any other supported language other than C/C++ (see the **"Run a C/C++ scan"** section  for more details)

### Pre-requisites
- Access to the **Embold** server (v1.8.9.0 or later, as of this writing)
- Embold Server is setup in your environment and you have access to create a Project and Repository in Embold
- Embold Server port (default 3000 for web) are accessible from the developer's machine.
- Corona Archive (Log into your account at [embold.io](https://embold.io/) and download the package from `Releases/embold_<Version>/Corona`).

### Setup and Configuration
In this example, we will scan Apache Kafka (Java) cloned from here: <https://github.com/apache/kafka.git>
1. Extract the corona package (to path for eg. /home/johndoe/corona)
1. Prepare a top-level directory for holding the source to be scanned and the scan scripts (e.g. `/home/johndoe/kafka_scan`)
2. Clone the source somewhere inside this directory, e.g. `/home/johndoe/kafka_scan/kafka`
3. Now create a **Project** in Embold which will hold the repository we want to scan. More info here: <https://docs.embold.io/projects-repositories/#create-a-project>
4. Next, link a new **Remote Repository** to this Project and give it a name, e.g. `Kafka`. More info here: <https://docs.embold.io/projects-repositories/#link-a-repository>https://embold.io/
 and downloadArchive Make sure the **Repository Type** is **Remote** and the Language is set to **Java**
5. Download the repository configuration (`repository-configuration.json`) of this newly-created repository on the host where we will run the scan. In our example, copy it to: `/home/johndoe/kafka_scan/scripts`

    **Note:** You can download the JSON by selecting the **"..."** option on the **Remote Repository** we just created, and then the "Download repository configuration" option
    
6. Modify the following elements of the repository-configuration.json:
    - `gammaAccess/url`: Your Embold Server Url (e.g. `http://<embold_host>:3000`)
    -  Replace the fields `userName` and `password` with `token` and paste the token (More info on Embold Access Token here: <https://docs.embold.io/gamma-access-token-gat/#gamma-access-token-gat>)
    - `repositories/dataDir`:  `(/home/johndoe/kafka_scan/data)`
    - `repositories/sources/baseDir`: Set it to the source root where you cloned Kafka sources. (`/home/johndoe/kafka_scan/kafka`)
    
      **Note:** You will notice, we still need to configure/modify a few details pertaining to remote scan (as stated in the above steps).This is set to be removed in our upcoming releases.
      
7. This script just launches the Embold Local scan and specifies the repository-configuration.json we just created.

    Create a file `embold-scan.sh` under `/home/johndoe/kafka_scan/scripts` and add this line:

    ```sh
    /home/johndoe/corona/scanboxwrapper/bin/gammascanner -la -od ./out.csv -c /home/johndoe/kafka_scan/scripts/repository-configuration.json
    ```
    ```
    -la           -  local scan
    -od <file>    -  local path for the output csv.
    ```
8. The issues will be published to the local output csv.

### Run the scan

- Add some necessary environment variables, and execute Embold scan command:

    ```sh
    CORONA_HOME=/home/johndoe/corona CORONA_LOG=/home/johndoe/corona/log  /home/johndoe/kafka_scan/scripts/embold-scan.sh
    ```

- The above command runs the scan and publishes the results to the local csv
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

At this point, we have successfully run an Embold scan and published results to the local csv.


## Run a C/C++ scan
This section adds some specifics if you want to run a C/C++ local scan. We recommend running any C/C++ scan with the following 2 steps:
1. Monitor your build with the Embold trace tool (`embold-trace`), to produce a compilation database (`compile_commands.json`).This process captures the compilation calls including header paths, switches, pre-processor definitions, etc. (as the compiler sees it), which we then use while running the actual scan
 
### Local Scan Advantages
   Integrating Embold trace tool with the build allows for faster scan times, as the developer would generally modify only a few files within the code-base, in a single commit. Incrementally making changes and building (eg.`make`) will result in incremental compilation, as a result it will create a smaller compilation database and hence faster local analysis, with the output containing issues only from the files that were last modified. 

2. Run the Embold scan by using the generated `compile_commands.json` and the source code.

As you can see, the first step is something specific for C/C++, and the second step is similar to the Java scan we saw earlier (except that we are now additionally specifiying the `compile_commands.json` to the scanner as well)

There are different ways to build your code, on various platforms, build systems, compilers, etc., so the steps to generate the `compile_commands.json` vary a bit depending on these combinations (platform, build system, compiler).


### Setup and Configuration
The setup and configuration is same as the [above Java scan](https://github.com/embold/emb-integration-samples/blob/local_scan/local_scan/local_scan.md#setup-and-configuration), except we will have to add an extra step in the beginning, which is to generate the compilation database at in the `baseDir`.
So our above `embold-scan.sh` will look like the following (assuming the codebase (`baseDir`) is `/home/johndoe/c-project/`)

    /home/johndoe/corona/cxxparser/bin/embold-trace -o /home/johndoe/c-project/ <build command> <build command args>
    /home/johndoe/corona/scanboxwrapper/bin/gammascanner -la -od ./out -c /home/johndoe/c-project/scripts/repository-configuration.json

### Run the scan
Same as the above [Java's Run the scan](https://github.com/embold/emb-integration-samples/blob/local_scan/local_scan/local_scan.md#run-the-scan) section.

### Note
Embold trace tool should be fed the actual build compilation command (make, ninja, msbuild, gcc, etc) and not the build generators such cmake, meson, autotools. The build generators will have to be run separately first.




