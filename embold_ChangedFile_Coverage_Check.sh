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

#Step 2: Get files diff for current Jenkins build. It should get a list of changed files path.
CURRENT_COMMIT_ID=$GIT_COMMIT
LAST_SUCESSFULL_COMMIT=$GIT_PREVIOUS_SUCCESSFUL_COMMIT

if [ -z $CURRENT_COMMIT_ID ]
then
	CURRENT_COMMIT_ID=$(git rev-parse --verify HEAD)
fi


if [ -z $LAST_SUCESSFULL_COMMIT ]
then
	LAST_SUCESSFULL_COMMIT=$GIT_PREVIOUS_COMMIT
fi

#Step 3: Get changed files path.
declare -a diffFilesPath=$(git diff --diff-filter=AM $LAST_SUCESSFULL_COMMIT...$CURRENT_COMMIT_ID --name-only)

if [ ${#diffFilesPath[@]} -eq 0 ]; then
	echo "Cannot find changed files between previous sucessful commit and current commit. Considering only the current commit changed files for "
	diffFilesPath=$(git show --name-only {CURRENT_COMMIT_ID})
fi

#Step 4: From repoid and file path (signature), get the list of NodeIds.
echo "Getting nodeids for changed files for repository uid $repositoryUid"
declare -a changedFilesNodeIds

for i in "${diffFilesPath[@]}"
do	
   nodeInfo=$(curl -s -X GET -H "Authorization: bearer ${eat}" "$emboldUrl/api/v1/repositories/$repositoryUid/nodes/details?signature=$i")
   nodeId=$(echo $nodeInfo | jq -r '.nodeid')
   
   if [-z "$nodeId" ]
	then
		echo "Node Id is  empty"
	else
	changedFilesNodeIds+=("$nodeId")
	fi
done

if [ ${#changedFilesNodeIds[@]} -eq 0 ]; then
	echo "NodeIds for changed files not not found on the embold instance configured at $emboldUrl for repository uid $repositoryUid. Please verify if the correct repository and/or embold url is configured."
	exit 1
fi


#Step 5: Get coverage information for each file
echo "Getting coverage information for all the changed files for repository $repositoryUid"
declare -a coverageForAllFiles

for j in "${changedFilesNodeIds[@]}"
do
  coverageInfo=$(curl -s -X GET -H "Authorization: bearer ${eat}" "$emboldUrl/api/v1/repositories/$repositoryUid/coverage?nodeId=$j")
  coverageForAllFiles+=("$coverageInfo")
done
 
declare -a actualCoveragePercentValue

#Step 6 : Get the coverage % for each file. 
for z in "${coverageForAllFiles[@]}"
do
  numberOfMethods=$(echo $z | jq -r '.numberOfMethods')
  numberOfCoveredMethods=$(echo $z | jq -r '.numberOfCoveredMethods')
  totalLocOfMethods=$(echo $z | jq -r '.totalLocOfMethods')
  totalLocOfCoveredMethods=$(echo $z | jq -r '.totalLocOfCoveredMethods')
  percentageCoverage=0
  
  if [[ -z $totalLocOfMethods || -z $totalLocOfCoveredMethods || $totalLocOfMethods -le 0 || $totalLocOfCoveredMethods -le 0 ]];
    then
      percentageCoverage=0
  else
      percentageCoverage=$(awk "BEGIN { pc=100*${totalLocOfCoveredMethods}/${totalLocOfMethods}; i=int(pc); print (pc-i<0.5)?i:i+1 }")    
  fi
  
  actualCoveragePercentValue+=("$percentageCoverage")
done


#Possibility of failing Jenkins Job based on total coverage %.
for x in "${actualCoveragePercentValue[@]}"
do
  if [ "$x"  -le "$coveragePercentThreashold" ]
    then
      echo "Coverage % check for changed files for repository with repository uid $repositoryUid was below configured threshold level of $coveragePercentThreashold. Failing build."
      
      #Exit -1 from script would fail the jenkins build.
	  exit 1       
  fi
done
echo "Coverage % check for changed files for repository with repository id $repositoryUid passes with over $coveragePercentThreashold coverage."

exit 0;
