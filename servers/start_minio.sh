#! /bin/bash

sudo echo "STARTING MINIO"

sudo minio server ~/dev/digitaldots/minio/data --console-address ":9001" > ~/Documents/DigitalDots/server_script_logs/minio.log 2>&1 &
