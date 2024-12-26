#! /bin/bash

echo -e "Which one? : \c"
read project_name

if [[ $project_name == "foundation" ]]; 
	then
	echo "OK STOPPING..."
	kill -9 `jps | grep "foundation" | cut -d " " -f 1`
	echo "STOPPED FOUNDATION"

elif [[ $project_name == "solution" ]]; 
	then
	echo "OK STOPPING..."
	kill -9 `jps | grep "solution-api" | cut -d " " -f 1`
	echo "STOPPED SOLUTION"

elif [[ $project_name == "store" ]]; 
	then
	echo "OK STOPPING..."
	kill -9 `jps | grep "store-api" | cut -d " " -f 1`
	echo "STOPPED STORE"

elif [[ $project_name == "scheduler" ]]; 
	then
	echo "OK STOPPING..."
	kill -9 `jps | grep "scheduler" | cut -d " " -f 1`
	echo "STOPPED SCHEDULER"

elif [[ $project_name == "process-engine" ]]; 
	then
	echo "OK STOPPING"
	kill -9 `jps | grep "process-engine" | cut -d " " -f 1`
	echo "STOPPED PROCESS-ENGINE"

elif [[ $project_name == "process-api" ]]; 
	then
	echo "OK STOPPING"
	kill -9 `jps | grep "process-api" | cut -d " " -f 1`
	echo "STOPPED PROCESS-API"

else 
	echo "BRUH.... TRY AGAIN..."
fi
