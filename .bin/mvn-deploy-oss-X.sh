#!/usr/bin/env bash

goal=$*
if [[ -z $goal ]];then
	goal="clean deploy"
fi
echo "goal: ${goal}"
mvn -f ../pom.xml -DskipTests -X -P repos-oss ${goal}
