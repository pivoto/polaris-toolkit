#!/bin/bash

goal=$*
if [[ -z $goal ]];then
	goal="clean install"
fi
echo "goal: ${goal}"
mvn -f ../pom.xml -DskipTests -P repos-oss ${goal}
