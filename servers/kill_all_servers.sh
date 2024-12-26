#! /bin/bash

sudo kill -9 `pidof vault`
echo "STOPPED HASCHI-CORP-VAULT"
sleep 2s

sudo kill -9 `pidof minio`
echo "STOPPED MINIO"
sleep 2s

cd ~/Documents/kafka_2.12-3.3.1
./bin/kafka-server-stop.sh
sleep 2s

./bin/zookeeper-server-stop.sh
sleep 2s

echo "STOPPED KAFKA"

echo "Switched Directory"
cd ~/Documents/DigitalDots/servers
