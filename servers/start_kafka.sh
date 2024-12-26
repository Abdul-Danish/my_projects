#! /bin/bash

cd ~/Documents/kafka_2.12-3.3.1

./bin/kafka-server-stop.sh
sleep 3s

./bin/zookeeper-server-stop.sh
sleep 3s

./bin/zookeeper-server-start.sh config/zookeeper.properties > ~/Documents/DigitalDots/server_script_logs/zookeeper.log 2>&1 &
echo "ZOOKEEPER STARTED"
sleep 6s

./bin/kafka-server-start.sh config/server.properties > ~/Documents/DigitalDots/server_script_logs/kafka.log 2>&1 &
echo "KAFKA STARTED"

echo "Switched Directory"
cd ~/Documents/DigitalDots/servers
