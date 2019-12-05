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
HTTP_RESPONSE=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" -X GET -H "Authorization: bearer ${eat}" "$emboldUrl/api/v1/repositories/$repositoryUid/snapshots?sortBy=timestamp&orderBy=desc")
# extract the body
HTTP_BODY=$(echo $HTTP_RESPONSE | sed -e 's/HTTPSTATUS\:.*//g')

# extract the status
HTTP_STATUS=$(echo $HTTP_RESPONSE | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

if [ $HTTP_STATUS != 200 ] && [ $HTTP_STATUS != 204 ]
then
  echo $HTTP_BODY | jq -r '.error.message'
  exit 1;
fi

latestSnapshot=$(echo $HTTP_BODY | jq -r '.[0].id')
previousSnapshot=$(echo $HTTP_BODY | jq -r '.[1].id')

echo "Latest Snapshot : $latestSnapshot"
echo "Previous Snapshot : $previousSnapshot"

#Step 2: Get coverage information for each file
echo "Getting component wise coverage information for the latest scan of Embold for repository with uid $repositoryUid"
declare -a componentCoverageList=$(curl -s -X GET -H "Authorization: bearer ${eat}" "$emboldUrl/api/v1/repositories/$repositoryUid/coverage/list")

declare -a componentsCoverageBelowThreshold

for row in $(echo "${componentCoverageList}" | jq -r '.[] | @base64'); do
  _jq() {
   echo ${row} | base64 --decode | jq -r ${1}
  }
  
  coveragePercentage=$(echo $(_jq '.coveragePercentage'))
  signature=0
  
  if [ -z $coveragePercentage ]
  then
    echo "Null coverage %"
    signature=$(echo $(_jq '.name'))
    
  elif [[ "`echo "${coveragePercentage} < ${coveragePercentThreshold}" | bc`" -eq 1 ]]
    then
      signature=$(echo $(_jq '.name'))
	    coveragePercent=$(echo $(_jq '.coveragePercentage'))
  fi
  
   if [ $signature != 0 ]
   then
     componentsCoverageBelowThreshold+=("$signature  $coveragePercent%")
     componentCount=$(expr $componentCount + 1)
   fi
done

if [[ -z $componentsCoverageBelowThreshold  || $componentsCoverageBelowThreshold == 0 ]]
then
	echo "Coverage Quality Gate Passed: Coverage % check Passed for all components for repository with repository uid $repositoryUid. Coverage is over $coveragePercentThreshold ."
	
	exit 0;
fi

echo "Coverage Quality Gate Failed: Coverage % check Failed for following $componentCount components for repository with repository id $repositoryUid : "
for z in "${componentsCoverageBelowThreshold[@]}"	
do
	index=$(expr $index + 1)
	echo "$index. $z"
done
exit 1
