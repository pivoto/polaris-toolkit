@echo off
setlocal enabledelayedexpansion

call %~dp0\setenv.cmd

cd %WORKDIR%
mkdir %WORKDIR%\logs

set JAVA_OPTIONS=-cp %CLASSPATH%
set JAVA_OPTIONS=%JAVA_OPTIONS% -DappName=%APP_NAME% -Dbase.dir=%WORKDIR%
set JAVA_OPTIONS=%JAVA_OPTIONS% -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector
set JAVA_OPTIONS=%JAVA_OPTIONS% -Dlog4j.configurationFile=%WORKDIR%/conf/log4j2.xml
set JAVA_OPTIONS=%JAVA_OPTIONS% -Dfile.encoding=UTF-8
set JAVA_OPTIONS=%JAVA_OPTIONS% -server -Xms512m -Xmx1024m -Xmn390m -Xss256k
set JAVA_OPTIONS=%JAVA_OPTIONS% -XX:MaxMetaspaceSize=64m -XX:MaxMetaspaceSize=256m -Xss256k -XX:SurvivorRatio=4
set JAVA_OPTIONS=%JAVA_OPTIONS% -XX:TargetSurvivorRatio=70 -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSClassUnloadingEnabled -XX:+DisableExplicitGC
set JAVA_OPTIONS=%JAVA_OPTIONS% -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:+CMSScavengeBeforeRemark
set JAVA_OPTIONS=%JAVA_OPTIONS% -XX:+PrintGC -XX:+PrintGCDetails

set APP_MAIN=io.polaris.builder.Main
set JAVA_ARGS=--jdbcCfg %WORKDIR%/conf/jdbc.xml --xmlData %WORKDIR%/conf/tables.xml --codegen %WORKDIR%/conf/code.xml

echo "java %JAVA_OPTIONS% %APP_MAIN% %JAVA_ARGS%"
java %JAVA_OPTIONS% %APP_MAIN% %JAVA_ARGS%

endlocal

pause
