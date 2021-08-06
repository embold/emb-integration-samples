# CI/CD Wrapper integration in Jenkins pipeline

This integration will do the following,
1. Find only changed files
2. Run Embold Scan for only those files
3. Check Embold quality gate status after a scan
4. Show the quality gate profile data on Jenkins console 
5. Fail Jenkins Job if quality gate failed

### Pre-requisites

1. Download standalone corona from Embold release section
2. Set following Global environment variable in Jenkins
a. CORONA_HOME : It should be the path of extracted corona folder
b. CORONA_LOG : It should be any path where Embold logs get created
3. Download CI/CD wrapper package from Embold release section
4. Extract the wrapper package in Jenkins machine where Job will execute

### Setup in pipeline script
Need following commands to execute in a pipeline build step

First, we need to find the changed files from GIT_PREVIOUS_SUCCESSFUL_COMMIT to GIT_COMMIT
**This requires at least one successful build before this command**
> sh "git diff --name-only --oneline $GIT_PREVIOUS_SUCCESSFUL_COMMIT $GIT_COMMIT > result.txt"

Run embold-ci-cd-wrapper with following options
> sh '../../../embold_ci_cd_wrapper-1.0-SNAPSHOT/bin/embold-ci-cd-wrapper -c repository-configuration.json -lf result.txt'
