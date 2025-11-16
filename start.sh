#!/bin/bash
# Start Spring Boot with Angular frontend and AWS RDS

# Setup Node.js 22 for Angular builds
source ~/.nvm/nvm.sh
nvm use 22.14.0

# Setup Java 21
export JAVA_HOME=/usr/local/Cellar/openjdk@21/21.0.8/libexec/openjdk.jdk/Contents/Home

# Start with local profile (uses AWS RDS)
mvn spring-boot:run -Dspring-boot.run.profiles=local