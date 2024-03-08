#!/usr/bin/env bash

goal=$*
if [[ -z $goal ]];then
	goal="clean deploy"
fi
echo "goal: ${goal}"
mvn -f ../pom.xml -Dmaven.test.skip=true  -P repos-jcfc,withDoc ${goal}
