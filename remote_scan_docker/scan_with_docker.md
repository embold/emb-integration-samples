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
This section applies to running a Java scan or for that matter any other supported language other than C/C++ (see the "Run a C/C++ scan" section  for more details)

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

```shell
/opt/gamma/corona/scanboxwrapper/bin/gammascanner -c /docker_build/kafka_scan/scripts/repository-configuration.json
```
### Run the scan
With the setup done, we can now launch the Embold Corona container from host
- Add some necessary environment variables, and execute Embold scan with the docker run command:

```shell
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
[EMBOLD] VERIFYING REPOSITORY AND DB CONNECTION            	[DONE]
[EMBOLD] PREPARING TO SCAN                                 	[DONE]
[EMBOLD] VERIFYING EMBOLD LICENSE                          	[DONE]
[EMBOLD] LAUNCHING SCAN                                    	[DONE]
[EMBOLD] STARTED REVIEW PROCESS                            	[DONE]
[EMBOLD] SCANNING FILES                                    	[DONE]
[EMBOLD] PREPROCESSING DATA                                	[DONE]
[EMBOLD] COLLECTING METRICS                                	[DONE]
[EMBOLD] INTEGRATING UNIT TEST RESULTS : JUNIT             	[DONE]
[EMBOLD] DETECTING CODE ISSUES : PMD                       	[DONE]
[EMBOLD] INTEGRATING CODE COVERAGE RESULTS : JACOCO        	[DONE]
[EMBOLD] INTEGRATING CODE COVERAGE RESULTS : CLOVER        	[DONE]
[EMBOLD] DETECTING CODE ISSUES : GAMMA_JAVA                	[DONE]
[EMBOLD] IDENTIFYING HIGH RISK COMPONENTS                  	[DONE]
[EMBOLD] IDENTIFYING ANTI-PATTERNS                         	[DONE]
[EMBOLD] CALCULATING RATINGS                               	[DONE]
[EMBOLD] PUBLISHING RESULTS                                	[DONE]
[EMBOLD] --------------------------------------------------------
[EMBOLD] ANALYSIS SUCCESS                                  
[EMBOLD] --------------------------------------------------------
[EMBOLD] Total Time : 280 sec                              
[EMBOLD] Finished at : 2020-03-01 06:57:34.525             
[EMBOLD] --------------------------------------------------------
```

At this point, we have successfully run an Embold scan in a docker container and published results to the Embold Server!

### Next steps

Now that we got the scan running, the next step could be to commit the `repository-configuration.json` to the source repository (rather than keeping it in the scripts directory), so it is always available to the container.

This can then be setup in a CI pipeline which is running a build, followed by the Embold scan within its own docker container.

## Run a C/C++ scan
TODO




