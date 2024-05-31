#!/usr/bin/env bash

BETA=0
VER=3.1.39
if [[ ${BETA} -eq 1 ]];then
	VER=${VER}-SNAPSHOT
fi

mvn -f ../pom.xml -P withDemo,ide-idea versions:set -DnewVersion=${VER}
mvn -f ../pom.xml -P withDemo,ide-idea versions:commit
sed -i -r -e  "s/<project.polaris.revision>.+<\/project.polaris.revision>/<project.polaris.revision>${VER}<\/project.polaris.revision>/g" ../pom.xml
#echo "wait 5s...."
#sleep 5
