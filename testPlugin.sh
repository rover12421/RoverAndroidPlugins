#! /bin/bash

./gradlew :app:publishToLocal
./gradlew :app:clean
./gradlew :app:assembleRelease