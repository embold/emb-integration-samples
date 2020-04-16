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
This project uses cmake to build from source.

## Build the custom Docker image (Your build tools + Embold components)

### Pre-requisites

- You have a docker image to build your software with all the necessary tools.
- You have downloaded the Embold Analyser (corona-archive.tar.gz) from your embold.io account

In our example for curl, the Dockerfile may look like this (assuming an Ubuntu 18 image supported by Embold):

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
Your actual build image would be more complex depending on which compilers, tools, external dependencies you need to build your software.

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

# Required for embold trace tool (gamm-trace) to monitor the build process
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

curl is built with cmake and make.

1. Prepare a top-level directory (e.g. `/home/johndoe/docker_build/curl_scan`)

TODO

