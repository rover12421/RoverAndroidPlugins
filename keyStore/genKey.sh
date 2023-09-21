#! /bin/bash

name=RoverAndroidPlugins
country=US
pass="${name}"

echo y | keytool -genkey -keystore keyStore/$name.jks -dname "cn=$name, ou=$name, o=$name, c=$country" -alias $name -keypass $pass -storepass $pass -keyalg RSA -keysize 2048 -validity 20000

keytool -list -v -alias $name -keystore keyStore/$name.jks -storepass $pass

