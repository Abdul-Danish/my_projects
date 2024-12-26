#! /bin/bash

echo -e "OKAY WHICH ONE ? : \c"
read server

if [[ $server == "vault" ]]
then
	sudo kill -9 `pidof vault`
	echo "STOPPED HASCHI-CORP-VAULT"

elif [[ $server == "minio" ]]
then
	sudo kill -9 `pidof minio`
	echo "STOPPED MINIO"

elif [[ $server == "kafka" ]]
then 
	cd ~/Documents/kafka_2.12-3.3.1
	./bin/kafka-server-stop.sh
	sleep 2s

	./bin/zookeeper-server-stop.sh
	sleep 2s

	echo "STOPPED KAFKA"
else
	echo "WHAT IS THAT!, GIVE VALID INPUT"
fi
