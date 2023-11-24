#!/usr/bin/env bash

#VER=3.1.1-SNAPSHOT
VER=3.1.2
mvn -f ../pom.xml versions:set -DnewVersion=${VER}
mvn -f ../pom.xml versions:commit
sed -i -r -e  "s/<project.polaris.revision>.+<\/project.polaris.revision>/<project.polaris.revision>${VER}<\/project.polaris.revision>/g" ../pom.xml
echo "wait 5s...."
sleep 5
