#!/usr/bin/env bash

BETA=0
VER=3.1.19
if [[ ${BETA} -eq 1 ]];then
	VER=${VER}-SNAPSHOT
fi

mvn -f ../pom.xml versions:set -DnewVersion=${VER}
mvn -f ../pom.xml versions:commit
sed -i -r -e  "s/<project.polaris.revision>.+<\/project.polaris.revision>/<project.polaris.revision>${VER}<\/project.polaris.revision>/g" ../pom.xml
#echo "wait 5s...."
#sleep 5
