#!/bin/bash

WORK_DIR=${PWD}
SHELL_DIR=$(cd "$(dirname "$0")" && pwd -P)
cd "${SHELL_DIR}"/ || exit

BETA=1
VER=3.2.22
if [[ ${BETA} -eq 1 ]];then
	VER=${VER}-SNAPSHOT
fi


echo $PWD
mvn -f ../pom.xml -P withDemo,withIdea versions:set -DnewVersion=${VER}
mvn -f ../pom.xml -P withDemo,withIdea versions:commit
sed -i -r -e  "s/<project.polaris-toolkit.revision>.+<\/project.polaris-toolkit.revision>/<project.polaris-toolkit.revision>${VER}<\/project.polaris-toolkit.revision>/g" ../pom.xml
#echo "wait 5s...."
#sleep 5
