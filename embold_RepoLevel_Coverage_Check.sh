#!/bin/bash
emboldUrl=$1
repositoryUid=$2
eat=$3
coveragePercentThreashold=$4 #The minimum coverage can be 60 (indicating 60% covergae)

if [ -z $emboldUrl ]
then
  echo "Please provide a valid Embold URL"
  exit 1
fi

if [ -z $repositoryUid ]
then
  echo "Please provide a valid Embold repository uid"
  exit 1
fi

if [ -z $eat ]
then
  echo "Please provide a valid Embold Access Token"
  exit 1
fi

if [ -z $coveragePercentThreashold ]
then
  echo "Considering default Coverage % Threashold value to be 60 %"
  coveragePercentThreashold=60;
fi


if ! type "jq" > /dev/null; then
  echo "jq does not exist"
  sudo apt install jq
fi

# Step 1: Get the latest snapshot
echo "Getting the latest snapshot for repository with repository uid $repositoryUid"
allSnapshots=$(curl -s -X GET -H "Authorization: bearer ${eat}" "$emboldUrl/api/v1/repositories/$repositoryUid/snapshots?sortBy=timestamp&orderBy=desc")
errorCode=$(echo $allSnapshots | jq -r '.error.code')

if [[ $errorCode > 0 ]]
then
  if [[ $errorCode -eq 1001 ]]
  then
    echo "Unauthorized Request. Please verify Embold Access Token."    
  fi
  
  if [[ $errorCode -eq 1007 ]]
  then
    echo "Forbidden. Please verify repository uid."    
  fi 
  exit 1;
fi

latestSnapshot=$(echo $allSnapshots | jq -r '.[0].id')

#Step 2: Get coverage information for entire Repo 
echo "Getting coverage information for the Repository with repository uid  $repositoryUid"
coverageInfo=$(curl -s -X GET -H "Authorization: bearer ${eat}" "$emboldUrl/api/v1/repositories/$repositoryUid/coverage")

if [ -z $coverageInfo ]
then
	echo "No coverage data found for repository with repository uid $repositoryUid. Please verify the repository uid provided."
	exit 1;
fi

declare -a actualCoveragePercentValue
numberOfMethods=$(echo $coverageInfo | jq -r '.numberOfMethods')
numberOfCoveredMethods=$(echo $coverageInfo | jq -r '.numberOfCoveredMethods')
totalLocOfMethods=$(echo $coverageInfo | jq -r '.totalLocOfMethods')
totalLocOfCoveredMethods=$(echo $coverageInfo | jq -r '.totalLocOfCoveredMethods')
percentageCoverage=0
  
if [[ -z $totalLocOfMethods || -z $totalLocOfCoveredMethods || $totalLocOfMethods -le 0 || $totalLocOfCoveredMethods -le 0 ]];
then
  percentageCoverage=0
else
  percentageCoverage=$(awk "BEGIN { pc=100*${totalLocOfCoveredMethods}/${totalLocOfMethods}; i=int(pc); print (pc-i<0.5)?i:i+1 }")    
fi

if [ "$percentageCoverage"  -le "$coveragePercentThreashold" ]
then
  echo "Coverage % check for repository with repository uid $repositoryUid was below configured threshold level of $coveragePercentThreashold. Failing the build."
  
  #Exit -1 from script would fail the jenkins build.
  exit 1       
fi
echo "Coverage % check for repository with repository uid $repositoryUid Passed with over $coveragePercentThreashold coverage."

exit 0;
