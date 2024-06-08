#!/bin/bash

goal=$*
if [[ -z $goal ]];then
	goal="clean deploy"
fi
echo "goal: ${goal}"
mvn -f ../pom.xml -DskipTests -P repos-oss ${goal} 2>&1 | tee mvn-deploy-oss.log
