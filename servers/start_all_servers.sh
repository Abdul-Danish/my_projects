#! /bin/bash


sudo echo "STARTING MINIO"
sudo minio server ~/dev/digitaldots/minio/data --console-address ":9001" > ~/Documents/digitaldots/server_logs/minio.log 2>&1 &
sleep 2s

vault server -dev -dev-root-token-id="root" > ~/Documents/digitaldots/server_logs/vault.log 2>&1 &
echo "HASHI-CORP-VAULT STARTED"
sleep 2s

cd ~/Documents/kafka_2.12-3.3.1

./bin/zookeeper-server-start.sh config/zookeeper.properties > ~/Documents/digitaldots/server_logs/zookeeper.log 2>&1 &
echo "ZOOKEEPER STARTED"
sleep 8s

./bin/kafka-server-start.sh config/server.properties > ~/Documents/digitaldots/server_logs/kafka.log 2>&1 &
echo "KAFKA STARTED"

echo "Switched Directory"
cd ~/Documents/digitaldots/servers
