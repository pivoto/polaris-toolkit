#!/bin/bash

WORK_DIR=${PWD}
SHELL_DIR=$(cd $(dirname $0) && pwd -P)
cd ${SHELL_DIR}/..
find . -name "pom.xml" | while read file;do
	sed -i -e "s|<groupId>cn.pivoto.|<groupId>cn.fossc.|g" ${file}
	sed -i -e "s|<deploymentName>cn.pivoto.|<deploymentName>cn.fossc.|g" ${file}
	sed -i -e "s|<description>pivoto</description>|<description>fossc</description>|g" ${file}
	sed -i -e "s|<name>pivoto</name>|<name>fossc</name>|g" ${file}
	sed -i -e "s|www.pivoto.cn|www.fossc.cn|g" ${file}
done;


