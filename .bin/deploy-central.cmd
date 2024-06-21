@echo off
cd %~dp0
call mvn -f ../pom.xml -DskipTests -P dist,dist-central clean deploy
pause
