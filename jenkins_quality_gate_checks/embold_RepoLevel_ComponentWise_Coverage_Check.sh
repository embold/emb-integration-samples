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
previousSnapshot=$(echo $allSnapshots | jq -r '.[1].id')

echo "Latest Snapshot : $latestSnapshot"
echo "Previous Snapshot : $previousSnapshot"

#Step 2: Get coverage information for each file
echo "Getting component wise coverage information for the latest scan of Embold for repository with uid $repositoryUid"
declare -a componentCoverageList=$(curl -s -X GET -H "Authorization: bearer ${eat}" "$emboldUrl/api/v1/repositories/$repositoryUid/coverage/list")

#if [[ -z $componentCoverageList  || $componentCoverageList -eq 0 ]]
#then
#	echo "No coverage data found for any component for the latest scan of Embold for repository with uid $repositoryUid"
#fi  

declare -a componentsCoverageBelowThreshold

for row in $(echo "${componentCoverageList}" | jq -r '.[] | @base64'); do
  _jq() {
   echo ${row} | base64 --decode | jq -r ${1}
  }
  
  coveragePercentage=$(echo $(_jq '.coveragePercentage'))
  signature=0
  
  if [ -z $coveragePercentage ]
  then
    echo "Null Covergae %"
    signature=$(echo $(_jq '.name'))
    
  elif [[ $coveragePercentage < $coveragePercentThreashold ]]
    then
      signature=$(echo $(_jq '.name'))
  fi
  
   if [ $signature != 0 ]
   then
     componentsCoverageBelowThreshold+=("$signature")
   fi
done

if [[ -z $componentsCoverageBelowThreshold  || $componentsCoverageBelowThreshold == 0 ]]
then
	echo "Coverage % check Passed for all components for repository with repository uid $repositoryUid. Coverage is over $coveragePercentThreashold coverage."
	
	exit 0;
fi

echo "Coverage % check Failed for following components for repository with repository id $repositoryUid : "
for z in "${componentsCoverageBelowThreshold[@]}"	
do
	echo "$z"
done
exit 1
