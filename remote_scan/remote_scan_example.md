# Embold Scan in remote mode
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
- Access to the **Embold Corona** archive file (corona-archive.tar.gz version 1.8.1.0 or later, as of this writing)
- Embold Server is setup in your environment and you have access to create a Project and Repository in Embold
- Embold Server ports (default 3000 for web and 5432 for data) are accessible from the host where the scan will run
- JRE 8 or later is installed on the Windows 10 host where we will run the scan
- For C# scans: .Net Core 3.1 is needed
    - You can check if its installed with this command:
    - `dotnet --version`
    - The above command should return 3.1 or later
    - If it is not installed, follow steps here to install it: <https://dotnet.microsoft.com/download/dotnet-core/thank-you/sdk-3.1.201-windows-x64-installer>

### Setup and Configuration
In this example, we will scan MSBuild (C#) cloned from here: <https://github.com/microsoft/msbuild.git>

1. Prepare a top-level directory for holding the source to be scanned, e.g. `C:\Users\embold\remote_scan`
2. Clone the source somewhere inside this directory, e.g. `C:\Users\embold\remote_scan\msbuild`
3. Now create a **Project** in Embold which will hold the repository we want to scan. More info here: <https://docs.embold.io/projects-repositories/#create-a-project>
4. Next, link a new **Remote Repository** to this Project and give it a name, e.g. `MSBuild`. More info here: <https://docs.embold.io/projects-repositories/#link-a-repository>
    
    Make sure the **Repository Type** is **Remote** and the Language is set to **C#**
5. Download the repository configuration (`repository-configuration.json`) of this newly-created repository on the host where we will run the scan. In our example, copy it to: `C:\Users\embold\remote_scan`

    **Note:** You can download the JSON by selecting the **"..."** option on the **Remote Repository** we just created, and then the "Download repository configuration" option
6. Modify the following elements of the repository-configuration.json:
    - `gammaAccess/url`: Your Embold Server Url (e.g. `http://<embold_host>:3000`)
    - `gammaAccess/userName`: Your Embold username
    - `gammaAccess/password`: Your Embold password. If you are using **Embold Access Token**, change the field name `password` to `token` and paste the token instead of password (More info on Embold Access Token here: <https://docs.embold.io/gamma-access-token-gat/#gamma-access-token-gat>)
    - `repositories/dataDir`: In this example, set it to: `C:\\Users\\embold\\remote_scan\\embold_data` (**make sure to use `\\`, otherwise you will see a parsing error**)
    - `repositories/sources/baseDir`: Set it to the source root where you cloned MSBuild sources. In our example, set it to: 
`C:\\Users\\embold\\remote_scan\\msbuild` (**again, make sure to use `\\`**)
7. Now unzip the downloaded corona-archive.tar.gz to a location (e.g. `C:\embold`) and set the following environment variables

    ```bat
    set CORONA_HOME=C:\embold\corona
    set CORONA_LOG=C:\embold\logs
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



