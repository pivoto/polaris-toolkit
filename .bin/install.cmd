@echo off
call mvn -f ../pom.xml -DskipTests clean install
pause


