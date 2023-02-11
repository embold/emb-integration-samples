# Gcov results integration with Embold
Embold's Analyser component can be run on a separate machine than the Embold Server in order to support use-cases such as CI workflows, scanning with build, etc.
In this case, the scan happens on a machine such as the build machine, and the scan results are published to the Embold Server.
In this article, we will look at how to do this with a sample C# project on a Windows 10 host and publish results to the Embold Server.

The typical setup for this is as shown below:

```console
|---------------------------|                       |---------------------------|
| Analyser Host (Windows 10)|                       |                           |
|                           |                       |                           |
|                           |            3000 (web) |                           |
|      Embold Analyser      |----------------> *----|       Embold Server       |
|      (a.k.a corona)       |----------------> *----|                           |
|                           |           5432 (data) |                           |
|---------------------------|                       |---------------------------|
```


### Pre-requisites
- Access to the **Embold Corona** archive file (corona-archive.tar.gz version 1.9.13.1 as of this writing)
- Embold Server is setup in your environment and you have access to create a Project and Repository in Embold
- Embold Server ports (default 3000) are accessible from the host where the scan will run
- JRE 8 or later is installed on the Ubuntu host where we will run the scan
- You are able to build your C/C++ project on the Ubuntu host (which means you have all the necessary compilers, headers, libs available)
- gcovr version 5.0 to 5.2

### Setup and Configuration
In this example, we will scan C++ Test Project cloned from here: <https://github.com/NileshVirkar/GtestGcovExample.git>

1. Clone the source to some directory, e.g. `/home/user/GtestGcovExample`
2. Now create a **Project** in Embold which will hold the repository we want to scan. More info here: <https://docs.embold.io/projects-repositories/#create-a-project>
3. Next, link a new **Remote Repository** to this Project and give it a name, e.g. `GtestGcovExample`. More info here: <https://docs.embold.io/projects-repositories/#link-a-repository>

   Make sure the **Repository Type** is **Remote** and the Language is set to **C++**
4. Download the repository configuration (`repository-configuration.json`) of this newly-created repository on the host where we will run the scan. In our example, copy it to: `/home/user/GtestGcovExample`

   **Note:** You can download the JSON by selecting the **"..."** option on the **Remote Repository** we just created, and then the "Download repository configuration" option
5. Modify the following elements of the repository-configuration.json:
    - `gammaAccess/url`: Your Embold Server Url (e.g. `http://<embold_host>:3000`)
    - `gammaAccess/token`: Your **Embold Access Token** (More info on Embold Access Token here: <https://docs.embold.io/gamma-access-token-gat/#gamma-access-token-gat>)


    ```json
    "gammaAccess": {
        "url": "http://<embold_host>:3000",
        "token": "<Your Embold Access Token>"
    }
    ```

    - `repositories/dataDir`: In this example, set it to: `/home/user/embold_data`
    - `repositories/sources/baseDir`: Set it to the source root where you cloned GtestGcovExample sources. In our example, set it to: `/home/user/GtestGcovExample`
    ```json
    
    ```
6. Generate unit test result for source directory.

   1. create executable for test file.
       - g++ `<FilePath>` -lgtest --coverage -lpthread -o `<executableName>`
       - (e.g. `g++ test.cpp -lgtest --coverage -lpthread -o test`)
   2. Now the executable is generated for test file. Run the executable .
       - ./`<executable>` (for windows `<executable>`)
       - (e.g. ./test)
   3. Generate the result using gcovr.
       - gcovr --exclude='gtest.*' -x -o `<path-to-xml>`
       - (e.g. gcovr --exclude='gtest.*' -x -o  /home/user/github/Demo/resultDir/result.xml)
      
7. Add the generated result.xml's parent directory path in gcov section in repository-configuration.json:
    - ```json
        "coverage": [
                    {
                        "name": "gcov",
                        "enabled": true,
                        "config": {
                            "rules": [],
                            "options": [
                                {
                                    "name": "reportDir",
                                    "type": "upload",
                                    "value": "/home/user/github/Demo/resultDir",
                                    "required": true
                                }
                            ]
                        }
                    }
                ]
        ```
8. Now download and unzip the latest corona-archive.tar.gz to a location (e.g. `/home/user/embold`):

    ```sh
    user@host:~# curl https://v1.embold.io/nfs/embold_1.9.13.1/Corona/linux/corona_1.9.13.1.tar.gz -o corona_1.9.13.1.tar.gz
    user@host:~# tar xvf corona_1.9.13.1.tar.gz
    ```

   and set the following environment variables:

    ```sh
    user@host:~# export CORONA_HOME=/home/user/embold/corona
    user@host:~# export CORONA_LOG=/home/user/embold/logs
    user@host:~# export EMB_USE_DATA_API=true
    ```
### Run the scan

With the setup done, we can now launch the Embold scan:

    C:\embold\corona\scanboxwrapper\bin\gammascanner -c C:\Users\embold\remote_scan\repository-configuration.json

- The above command runs the scan on the Windows host and publishes results to the Embold Server
- The log files are placed at `%CORONA_LOG%`
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

At this point, we have successfully run an Embold scan on a remote machine and published results to the Embold Server!

Check our more examples on how to run remote scans in other scenarios at: <https://github.com/embold/emb-integration-samples/tree/master/remote_scan_docker>



