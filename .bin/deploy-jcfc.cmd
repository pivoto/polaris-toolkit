@echo off
cd %~dp0
call mvn -f ../pom.xml -Dmaven.test.skip=true  -P withDoc,dist-jcfc  clean deploy
pause


