#!/bin/bash
emboldUrl=$1
repositoryUid=$2
eat=$3
coveragePercentThreshold=$4 #The minimum coverage can be 60 (indicating 60% coverage)

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

if [ -z $coveragePercentThreshold ]
then
  echo "Considering default Coverage % Threshold value to be 60 %"
  coveragePercentThreshold=60;
fi

# Step 1: Get the latest snapshot
# echo "Getting the latest snapshot for repository with repository uid $repositoryUid"
HTTP_RESPONSE=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" -X GET -H "Authorization: bearer ${eat}" "$emboldUrl/api/v1/repositories/$repositoryUid/snapshots?sortBy=timestamp&orderBy=desc")
# extract the body
HTTP_BODY=$(echo $HTTP_RESPONSE | sed -e 's/HTTPSTATUS\:.*//g')

# extract the status
HTTP_STATUS=$(echo $HTTP_RESPONSE | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

#echo "$HTTP_BODY"
#echo "$HTTP_STATUS"

if [ $HTTP_STATUS != 200 ] && [ $HTTP_STATUS != 204 ]
then
  echo $HTTP_BODY | jq -r '.error.message'
  exit 1;
fi

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

if [ "$percentageCoverage"  -lt "$coveragePercentThreshold" ]
then
  echo "Coverage Quality Gate Failed: Coverage % check for repository with repository uid $repositoryUid is $percentageCoverage% which is below configured threshold level of $coveragePercentThreshold. Failing the build."
  exit 1       
fi
echo "Coverage Quality Gate Passed: Coverage % check for repository with repository uid $repositoryUid Passed with $percentageCoverage % coverage."
exit 0;
