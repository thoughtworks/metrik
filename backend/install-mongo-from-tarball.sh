#!/usr/bin/env bash

download_and_install() {
  echo "${PWD}/mongodb-linux-x86_64-debian10-4.4.4 directory not found. Start installation."
  sudo apt-get install libcurl4 openssl liblzma5
  wget https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-debian10-4.4.4.tgz
  tar -zxvf mongodb-linux-x86_64-debian10-4.4.4.tgz
}

[ -d "${PWD}/mongodb-linux-x86_64-debian10-4.4.4" ] \
  && echo "${PWD}/mongodb-linux-x86_64-debian10-4.4.4 directory already exists. Do nothing." \
  || download_and_install

echo "Mongo install done!"
