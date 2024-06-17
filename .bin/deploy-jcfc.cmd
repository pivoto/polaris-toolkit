@echo off
call mvn -f ../pom.xml -Dmaven.test.skip=true  -P withDoc,dist-jcfc  clean deploy
pause


