#!/bin/bash

source ~/.bash_profile

CURRDIR=${PWD}
SHELL_DIR=$(dirname $0)
if [[ '/' == "${SHELL_DIR:0:1}" ]];then
	cd ${SHELL_DIR}/..
	export WORKDIR=${PWD}
else
	cd ${PWD}/${SHELL_DIR}/..
	export WORKDIR=${PWD}
fi

echo "WORKDIR: ${WORKDIR}"

PATH_SEP=":"
if [[ "${OS}" == "Windows_NT" ]];then
	PATH_SEP=";"
fi

LIB_CLASSPATH=
for file in ${WORKDIR}/*.jar
do
	LIB_CLASSPATH="${LIB_CLASSPATH}"${PATH_SEP}"$file"
done

for file in ${WORKDIR}/lib/*.jar
do
	LIB_CLASSPATH="${LIB_CLASSPATH}"${PATH_SEP}"$file"
done

export CLASSPATH=.${PATH_SEP}${WORKDIR}/conf${PATH_SEP}${LIB_CLASSPATH}${PATH_SEP}${CLASSPATH}
echo "CLASSPATH: ${CLASSPATH}"
echo "JAVA_HOME: ${JAVA_HOME}"
