#!/usr/bin/env bash

echo "./gradlew clean test testUi"

./gradlew clean test testUi

if [ $? -ne 0 ]
then
    say "Test build failed"
fi
