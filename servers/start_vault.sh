#! /bin/bash

echo "STARTING HASHI-CORP-VAULT"

vault server -dev -dev-root-token-id="root" > ~/Documents/DigitalDots/server_script_logs/vault.log 2>&1 &