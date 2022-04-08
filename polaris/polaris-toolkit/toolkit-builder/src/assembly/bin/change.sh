#!/bin/bash

SHELL_DIR=$(dirname $0)
if [[ '/' == "${SHELL_DIR:0:1}" ]];then
	source ${SHELL_DIR}/setenv.sh
else
	source ${PWD}/${SHELL_DIR}/setenv.sh
fi

cd ${WORKDIR}
mkdir -p ${WORKDIR}/logs

JAVA_OPTIONS="-Dapp.name=${APP_NAME} -Dwork.dir=${WORKDIR}"
JAVA_OPTIONS="-Dlog.appName=${APP_NAME} -Dlog.basedir=${WORKDIR}/logs"
JAVA_OPTIONS="${JAVA_OPTIONS} -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector "
JAVA_OPTIONS="${JAVA_OPTIONS} -Dlog4j.configurationFile=${WORKDIR}/conf/log4j2.xml "
JAVA_OPTIONS="${JAVA_OPTIONS} -Dfile.encoding=UTF-8 "
JAVA_OPTIONS="${JAVA_OPTIONS} -server -Xms512m -Xmx1024m -Xmn390m -Xss256k "
JAVA_OPTIONS="${JAVA_OPTIONS} -XX:MaxMetaspaceSize=64m -XX:MaxMetaspaceSize=256m -Xss256k -XX:SurvivorRatio=4"
JAVA_OPTIONS="${JAVA_OPTIONS} -XX:TargetSurvivorRatio=70 -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSClassUnloadingEnabled -XX:+DisableExplicitGC"
JAVA_OPTIONS="${JAVA_OPTIONS} -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:+CMSScavengeBeforeRemark"
JAVA_OPTIONS="${JAVA_OPTIONS} -XX:+PrintGC -XX:+PrintGCDetails"

APP_MAIN=io.polaris.change.PackageChangerRunner
JAVA_ARGS="$WORKDIR/conf/change.xml"

echo "java ${JAVA_OPTIONS} ${APP_MAIN} $JAVA_ARGS"
java ${JAVA_OPTIONS} ${APP_MAIN} $JAVA_ARGS
