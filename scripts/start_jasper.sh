#!/usr/bin/env bash

JDK7="/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home"
JASPER_DIR="/Applications/ireport.app/Contents/MacOS"

echo "Starting Jasper iReport with JDK 7 ..."
cd ${JASPER_DIR} && ./ireport --jdkhome ${JDK7} &
