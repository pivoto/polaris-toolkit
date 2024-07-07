@echo off
cd %~dp0
set MAVEN_OPTS=-Dfile.encoding=UTF-8
set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8
call mvn -f ../pom.xml -DskipTests -P dist,dist-central clean deploy
pause
