#!/usr/bin/env bash

set -e

DB_FOLDER=$(pwd)
DB_EXPOSE_PORT=""

DB_ADMIN_USERNAME="admin"
DB_ADMIN_PASSWORD=$(openssl rand -base64 21)

DB_INIT_DATABASE="4-Key-Metrics"

DB_4KM_USERNAME="4km"
DB_4KM_PASSWORD=""

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m'

createDBFolder() {
	if [ ! -d "$DB_FOLDER/mongo" ]; then
		echo "Creating MongoDB data folders..."
		mkdir -p $DB_FOLDER/mongo/mongo-init
		mkdir $DB_FOLDER/mongo/mongo-volume
	else
		echo -e "${RED}[ERROR]${NC} $DB_FOLDER/mongo folder existed."
		exit 1
	fi
}

saveAdminCredential () {
	CREDENTIAL_FILE="$DB_FOLDER/mongo/admin_credential.txt"
	echo "=============== MongoDB Admin Credential ===============" > $CREDENTIAL_FILE
	echo "" >> $CREDENTIAL_FILE
	echo "username: $DB_ADMIN_USERNAME" >> $CREDENTIAL_FILE
	echo "password: $DB_ADMIN_PASSWORD" >> $CREDENTIAL_FILE
	echo "" >> $CREDENTIAL_FILE
	echo "========================================================" >> $CREDENTIAL_FILE
	echo "" >> $CREDENTIAL_FILE
	echo "Please keep this credential file private and safe." >> $CREDENTIAL_FILE
}

generateInitDBScript () {
	echo -e "\nPlease enter the password for ${YELLOW}4-Key-Metrics${NC} database \n ${YELLOW}* At least 6 digits\n * Leave blank for a RANDOM password${NC}"
	read -p "> " DB_4KM_PASSWORD
	
	RE="[[:space:]]+"
	if [[ $DB_4KM_PASSWORD =~ $RE ]]; then
		echo -e "${RED}[ERROR]${NC} Invalid password, should not contain any spaces."
		exit 1
	fi
	
	if [ -z "$DB_4KM_PASSWORD" ]; then
		echo -e "${YELLOW}[WARN]${NC} Empty input, generating random password..."
		DB_4KM_PASSWORD=$(openssl rand -base64 21)
	fi
	
	if [ ${#DB_4KM_PASSWORD} -lt 6 ]; then
		echo -e "${RED}[ERROR]${NC} Database password is too short, at lease 6 digits."
		exit 1
	fi
	
	echo "Generating MongoDB initialization JS..."
	echo "db.createUser({ user: \"$DB_4KM_USERNAME\", pwd: \"$DB_4KM_PASSWORD\", roles: [{ role: \"readWrite\", db: \"$DB_INIT_DATABASE\" } ] } );" > $DB_FOLDER/mongo/mongo-init/init.js
}

generateDockerCompose () {
	echo -e "\nPlease specify the expose port of dockerized MongoDB \n ${YELLOW}* Leave blank for default [27017]${NC}"
	read -p "> " DB_PORT
	
	if [ -z "$DB_PORT" ]; then
		DB_PORT=27017
	fi
	
	if ! ([[ $DB_PORT ]] && [ $DB_PORT -eq $DB_PORT 2>/dev/null ]);then
		echo -e "${RED}[ERROR]${NC} Invalid port number, the port number should be an INTEGER ranges in 1025~65535"
		exit 1
	fi
	
	if [ $DB_PORT -lt 1025 ] || [ $DB_PORT -gt 65535 ];then
		echo -e "${RED}[ERROR]${NC} Invalid port number, the port number range should be 1025~65535"
		exit 1
	fi
	
	echo "Generating docker compose file..."
	cat << EOF > $DB_FOLDER/docker-compose-mongo.yaml
version: '3.9'
services:
    mongo:
      container_name: mongodb
      image: mongo:4.4.3-bionic
      restart: always
      environment:
        - MONGO_INITDB_ROOT_USERNAME=$DB_ADMIN_USERNAME
        - MONGO_INITDB_ROOT_PASSWORD=$DB_ADMIN_PASSWORD
        - MONGO_INITDB_DATABASE=$DB_INIT_DATABASE
      ports:
        - "$DB_PORT:27017"
      volumes:
        - $DB_FOLDER/mongo/mongo-init/:/docker-entrypoint-initdb.d/:ro
        - $DB_FOLDER/mongo/mongo-volume/:/data/db/
EOF
}

displayInstruction() {
	echo "Hooray! All done!"
	echo ""
	echo "=========== MongoDB Info ==========="
	echo ""
	echo -e "Port:${GREEN} $DB_PORT ${NC}"
	echo -e "Database:${GREEN} $DB_INIT_DATABASE ${NC}"
	echo -e "Username:${GREEN} $DB_4KM_USERNAME ${NC}"
	echo -e "Password:${GREEN} $DB_4KM_PASSWORD ${NC}" 
	echo -e "Auth Mechanism:${GREEN} SCRAM-SHA-256 ${NC}"
	echo ""
	echo "===================================="
	echo ""
	echo -e "Admin credential for MongoDB has been saved to ${GREEN}$DB_FOLDER/mongo/admin_credential.txt${NC}, if you need higher permission."
	echo ""
	echo "Now you can use the following command to run dockerized MongoDB"
	echo -e "${GREEN}docker-compose -f $DB_FOLDER/docker-compose-mongo.yaml up -d${NC}"
	echo ""
}

## START
echo -e "Are you sure you would like to save MongoDB files in ${YELLOW}$(pwd)${NC}? (y/N) "
read -p "> " DB_CONFIRMATION

case $DB_CONFIRMATION in
	"N")
		echo -e "\nPlease specify the folder to save MongoDB files"
		read -p "> "  DB_FOLDER
		if [ ! -d "$DB_FOLDER" ];then
			echo -e "${RED}[ERROR]${NC} $DB_FOLDER is not a valid folder."
			exit 1
		fi
		createDBFolder
		saveAdminCredential
		generateInitDBScript
		generateDockerCompose
		displayInstruction
	;;
	"y")
		createDBFolder
		saveAdminCredential
		generateInitDBScript
		generateDockerCompose
		displayInstruction
	;;
	*)
		echo -e "${RED}[ERROR]${NC} Invalid input, Aborted."
		exit 1
		esac
exit 0
