#!/bin/bash
emboldUrl=$1
repositoryUid=$2
eat=$3
coverageDiffPercentThreashold=$4 #The minimum coverage can be 60 (indicating 60% covergae)

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

if [ -z $coverageDiffPercentThreashold ]
then
  echo "Considering default Coverage difference % Threashold value to be 60 %"
  coverageDiffPercentThreashold=20;
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

#Step 2: Getting file wise coverage information difference for the provided snapshots for repository
echo "Getting file wise coverage information difference between Embold scan number $previousSnapshot and  $latestSnapshot for repository with uid $repositoryUid"
declare -a componentDiffCoverage=$(curl -s -X GET -H "Authorization: bearer ${eat}" "$emboldUrl/api/v1/repositories/$repositoryUid/coverage/difference?snapshot1=$previousSnapshot&snapshot2=$latestSnapshot&orderBy=asc&nodeType=file")

declare -a componentsCoverageDifferenceBelowThreshold

for row in $(echo "${componentDiffCoverage}" | jq -r '.[] | @base64'); do
  _jq() {
   echo ${row} | base64 --decode | jq -r ${1}
  }
  
  coverageDifference=$(echo $(_jq '.coverageDifference'))
  signature=0
  
  if [ -z $coverageDifference ]
  then
    echo "Null Covergae %"
    signature=$(echo $(_jq '.name'))
    
  elif [[ $coverageDifference < $coverageDiffPercentThreashold ]]
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
	echo "File wise coverage % difference check between Embold scan number $previousSnapshot and  $latestSnapshot Passed sucessfully. Coverage is over $coverageDiffPercentThreashold coverage."
	
	exit 0;
fi

echo "File wise coverage % difference check between Embold scan number $previousSnapshot and  $latestSnapshot Failed for following files : "
for z in "${componentsCoverageBelowThreshold[@]}"	
do
	echo "$z"
done
exit 1;


