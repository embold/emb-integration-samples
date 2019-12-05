#!/bin/bash
emboldUrl=$1
repositoryUid=$2
eat=$3
coverageDiffPercentThreshold=$4 #The minimum coverage can be 60 (indicating 60% coverage)

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

if [ -z $coverageDiffPercentThreshold ]
then
  echo "Considering default Coverage difference % Threshold value to be 20 %"
  coverageDiffPercentThreshold=20;
fi

# Step 1: Get the latest snapshot
echo "Getting the latest snapshot for repository with repository uid $repositoryUid"
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

latestSnapshot=$(echo $HTTP_BODY | jq -r '.[0].id')
previousSnapshot=$(echo $HTTP_BODY | jq -r '.[1].id')

#Step 2: Getting file wise coverage information difference for the provided snapshots for repository
echo "Getting file wise coverage information difference between Embold scan number $previousSnapshot and  $latestSnapshot for repository with uid $repositoryUid"
declare -a componentDiffCoverage=$(curl -s -X GET -H "Authorization: bearer ${eat}" "$emboldUrl/api/v1/repositories/$repositoryUid/coverage/difference?snapshot1=$previousSnapshot&snapshot2=$latestSnapshot&orderBy=desc&nodeType=file")

declare -a componentsCoverageDifferenceBelowThreshold

for row in $(echo "${componentDiffCoverage}" | jq -r '.[] | @base64'); do
  _jq() {
   echo ${row} | base64 --decode | jq -r ${1}
  }
  
  coverageDifference=$(echo $(_jq '.coverageDifference'))
  signature=0
  
  if [ -z $coverageDifference ]
  then
    echo "Null coverage %"
    signature=$(echo $(_jq '.name'))
    
  elif [[ "`echo "${coverageDifference} < ${coverageDiffPercentThreshold}" | bc`" -eq 1 ]]
    then
        signature=$(echo $(_jq '.signature'))
	      coverageDiff=$(echo $(_jq '.coverageDifference'))
  fi
  
   if [ $signature != 0 ]
   then
     componentsCoverageBelowThreshold+=("$signature  $coverageDiff%")
     fileCount=$(expr $fileCount + 1)
   fi
done

if [[ -z $componentsCoverageBelowThreshold  || $componentsCoverageBelowThreshold == 0 ]]
then
	echo "Coverage Quality Gate Passed: Coverage is over $coverageDiffPercentThreshold."
	
	exit 0;
fi

echo "Coverage Quality Gate Failed: Coverage is under $coverageDiffPercentThreshold. Failed for following $fileCount files : "
for z in "${componentsCoverageBelowThreshold[@]}"	
do
	index=$(expr $index + 1)
	echo "$index. $z"
done
exit 1;


