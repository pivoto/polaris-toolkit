#!/bin/bash

goal=$*
if [[ -z $goal ]];then
	goal="clean deploy"
fi
echo "goal: ${goal}"
mvn -f ../pom.xml -DskipTests -X -P repos-oss ${goal} | tee -a mvn-deploy-oss-x.log
