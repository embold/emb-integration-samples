# Embold Local Scan
Embold Local Scan allows the developer to locally scan the code and get immediate feedback, before committing to the remote. It does not require any network connectivity during or after the scan (except for the initial authentication with the embold server). The results are published locally to a csv.

This article explains how to do it with a few commands and scripts, with an example C/C++ scan.


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
- Access to the **Embold Corona** server (v1.8.9.0 or later, as of this writing)
- Embold Server is setup in your environment and you have access to create a Project and Repository in Embold
- Embold Server port (default 3000 for web) are accessible from the developer's machine.
- Corona Package

### Setup and Configuration
In this example, we will scan Apache Kafka (Java) cloned from here: <https://github.com/apache/kafka.git>
1. Extract the corona package (to path for eg. /home/johndoe/corona)
1. Prepare a top-level directory for holding the source to be scanned and the scan scripts (e.g. `/home/johndoe/kafka_scan`)
2. Clone the source somewhere inside this directory, e.g. `/home/johndoe/kafka_scan/kafka`
3. Now create a **Project** in Embold which will hold the repository we want to scan. More info here: <https://docs.embold.io/projects-repositories/#create-a-project>
4. Next, link a new **Remote Repository** to this Project and give it a name, e.g. `Kafka`. More info here: <https://docs.embold.io/projects-repositories/#link-a-repository>
    Make sure the **Repository Type** is **Remote** and the Language is set to **Java**
5. Download the repository configuration (`repository-configuration.json`) of this newly-created repository on the host where we will run the scan. In our example, copy it to: `/home/johndoe/kafka_scan/scripts`

    **Note:** You can download the JSON by selecting the **"..."** option on the **Remote Repository** we just created, and then the "Download repository configuration" option
6. Modify the following elements of the repository-configuration.json:
    - `gammaAccess/url`: Your Embold Server Url (e.g. `http://<embold_host>:3000`)
    - `gammaAccess/userName`: Your Embold username
    - `gammaAccess/password`: Your Embold password. If you are using **Embold Access Token**, change the field name `password` to `token` and paste the token instead of password (More info on Embold Access Token here: <https://docs.embold.io/gamma-access-token-gat/#gamma-access-token-gat>)
    - `repositories/dataDir`:  `(/home/johndoe/kafka_scan/data)`
    - `repositories/sources/baseDir`: Set it to the source root where you cloned Kafka sources. (`/home/johndoe/kafka_scan/kafka`)

7. This script just launches the Embold Local scan and specifies the repository-configuration.json we just created.

    Create a file `embold-scan.sh` under `/home/johndoe/kafka_scan/scripts` and add this line:

    ```sh
    /home/johndoe/corona/scanboxwrapper/bin/gammascanner -la -od ./out.csv -c /home/johndoe/kafka_scan/scripts/repository-configuration.json
    
    -la           -  local scan
    -od <file>    -  local path for the output csv.
    
    ```
8. The issues will be published to the local output csv.

### Run the scan

With the setup done, we can now launch the Embold Corona container from host
- Add some necessary environment variables, and execute Embold scan with the docker run command:

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
1. Monitor your build with the Embold trace tool (`embold-trace`), to produce a compilation database (`compile_commands.json`).
 
    This process captures the compilation calls including header paths, switches, pre-processor definitions, etc. (as the compiler sees it), which we then use while running the actual scan. Integrating Embold trace tool with the build allows for faster scan times, as the developer would generally modify only a few files within the code-base, in a single commit. This results in a much smaller compilation database and hence faster local analysis.

2. Run the Embold scan by using the generated `compile_commands.json` and the source code.

As you can see, the first step is something specific for C/C++, and the second step is similar to the Java scan we saw earlier (except that we are now additionally specifiying the `compile_commands.json` to the scanner as well)

There are different ways to build your code, on various platforms, build systems, compilers, etc., so the steps to generate the `compile_commands.json` vary a bit depending on these combinations (platform, build system, compiler).


### Setup and Configuration
The setup and configuration is same as the above Java scan, except we will have to add an extra step, just before the scan.
So our above `embold-scan.sh` will look like the following:

    ```sh
    /home/johndoe/corona/cxxparser/bin/embold-trace <my build command> <my build command args>
    /home/johndoe/corona/scanboxwrapper/bin/gammascanner -la -od ./out.csv -c /home/johndoe/kafka_scan/scripts/repository-configuration.json
    
    -la           -  local scan
    -od <file>    -  local path for the output csv.
    
    ```

### Run the scan
Same as the above Java "Run the scan" section.





