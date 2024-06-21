@echo off
cd %~dp0
call mvn -f ../pom.xml -DskipTests clean install
pause


