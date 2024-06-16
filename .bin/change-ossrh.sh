#!/bin/bash

WORK_DIR=${PWD}
SHELL_DIR=$(cd $(dirname $0) && pwd -P)
cd ${SHELL_DIR}/..
find . -name "pom.xml" | while read file;do
	sed -i -e "s|<groupId>cn.fossc.|<groupId>cn.pivoto.|g" ${file}
	sed -i -e "s|<deploymentName>cn.fossc.|<deploymentName>cn.pivoto.|g" ${file}
	sed -i -e "s|<description>fossc</description>|<description>pivoto</description>|g" ${file}
	sed -i -e "s|<name>fossc</name>|<name>pivoto</name>|g" ${file}
	sed -i -e "s|<include>cn.fossc.|<include>cn.pivoto.|g" ${file}
	sed -i -e "s|www.fossc.cn|www.pivoto.cn|g" ${file}
done;


