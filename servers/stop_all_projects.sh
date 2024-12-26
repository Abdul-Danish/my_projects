#! /bin/bash


kill -9 `jps | grep "foundation" | cut -d " " -f 1`
echo "STOPPED FOUNDATION"
sleep 1s

kill -9 `jps | grep "solution-api" | cut -d " " -f 1`
echo "STOPPED SOLUTION"
sleep 1s

kill -9 `jps | grep "store-api" | cut -d " " -f 1`
echo "STOPPED STORE"
sleep 1s

kill -9 `jps | grep "scheduler" | cut -d " " -f 1`
echo "STOPPED SCHEDULER"
