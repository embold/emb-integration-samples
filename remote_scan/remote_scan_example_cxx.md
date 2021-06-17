# Embold Scan in remote mode
Embold's Analyser component can be run on a separate machine than the Embold Server in order to support use-cases such as CI workflows, scanning with build, etc.
In this case, the scan happens on a machine such as the build machine, and the scan results are published to the Embold Server.
In this article, we will look at how to do this with a sample C++ project on an Ubuntu host and publish results to the Embold Server.
This approach also includes using the `embold-trace` tool along with your C/C++ build. This tool monitors the build process and uses the collected information to run the scan

The typical setup for this is as shown below:

```console
|---------------------------|                       |---------------------------|
| Analyser Host (Ubuntu 18) |                       |                           |
|                           |                       |                           |
|                           |            3000 (web) |                           |
|      Embold Analyser      |----------------> *----|       Embold Server       |
|      (a.k.a corona)       |                       |                           |
|                           |                       |                           |
|---------------------------|                       |---------------------------|
```


### Pre-requisites
- Access to the **Embold Corona** archive file (corona-archive.tar.gz version 1.9.0.2 as of this writing)
- Embold Server is setup in your environment and you have access to create a Project and Repository in Embold
- Embold Server ports (default 3000) are accessible from the host where the scan will run
- JRE 8 or later is installed on the Ubuntu host where we will run the scan
- You are able to build your C/C++ project on the Ubuntu host (which means you have all the necessary compilers, headers, libs available)
- strace is installed on the Analyser (Ubuntu) host (required for build monitoring)
    - Install using: `apt-get install strace -y`

### Optional
- If you want to run [cppcheck](http://cppcheck.sourceforge.net) along with the Embold scan and have its results published into Embold, install `cppcheck` ***version: 1.82*** on the analyser host (Ubuntu 18 in our example)

### Setup and Configuration
In this example, we will scan OpenCV (C++) cloned from here: <https://github.com/opencv/opencv.git>

1. Clone the source to some directory, e.g. `/home/user/opencv`
2. Now create a **Project** in Embold which will hold the repository we want to scan. More info here: <https://docs.embold.io/projects-repositories/#create-a-project>
3. Next, link a new **Remote Repository** to this Project and give it a name, e.g. `OpenCV`. More info here: <https://docs.embold.io/projects-repositories/#link-a-repository>
    
    Make sure the **Repository Type** is **Remote** and the Language is set to **C++**
4. Download the repository configuration (`repository-configuration.json`) of this newly-created repository on the host where we will run the scan. In our example, copy it to: `/home/user/opencv`

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
    - `repositories/sources/baseDir`: Set it to the source root where you cloned OpenCV sources. In our example, set it to: `/home/user/opencv`
    
    - `repositories/settings/additionalOptions`: Set the path to the directory where the `compile_commands.json` was generated from the build step above:
    
    ```json
    "settings": {
                "additionalOptions": ["--cdb=/home/user/opencv/build"],
                "includePaths": []
    }
    ```

6. Now download and unzip the corona-archive.tar.gz to a location (e.g. `/home/user/embold`):

    ```sh
    user@host:~# curl https://v1.embold.io/nfs/embold_1.9.0.2/Corona/linux/corona_1.9.0.2.tar.gz -o corona_1.9.0.2.tar.gz
    user@host:~# tar xvf corona_1.9.0.2.tar.gz
    ```

    and set the following environment variables:

    ```sh
    user@host:~# export CORONA_HOME=/home/user/embold/corona
    user@host:~# export CORONA_LOG=/home/user/embold/logs
    user@host:~# export EMB_USE_DATA_API=true
    ```

### Build your source together with embold-trace

1.  OpenCV uses `cmake` so will set it up with that:
    
    ```sh
    user@host:~/opencv# mkdir build
    user@host:~/opencv# cd build
    user@host:~/opencv/build# cmake ..
    ```
2.  Run the build
    - embold-trace is a build monitor tool to capture build steps and use the collected information to run the scan
    - Run your normal build together with embold-trace:

    ```sh
    user@host:~/opencv/build# /home/user/embold/corona/cxxparser/bin/embold-trace make
    ```
    The above step will build your sources and generate a `compile_commands.json` in the current directory, which will be used for the scan
    
    This works out-of-the-box for the following compilers: ***gcc variants, ccppc, clang, green-hills, tasking tricore***
    
    If you are using any other compiler, check out this link: https://docs.embold.io/installation-and-backup-guide/#embold-trace
    
    Or reach out to us for help with supporting your compiler

### Run the scan

With the build done, we can now launch the Embold scan:

```sh
user@host:~/opencv/build# /home/user/embold/corona/scanboxwrapper/bin/gammascanner -c /home/user/opencv/repository-configuration.json
```

- The above command runs the scan on the Ubuntu host and publishes results to the Embold Server
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

At this point, we have successfully run an Embold scan on a remote machine and published results to the Embold Server!

Check our more examples on how to run remote scans in other scenarios at: <https://github.com/embold/emb-integration-samples/tree/master/remote_scan_docker>
