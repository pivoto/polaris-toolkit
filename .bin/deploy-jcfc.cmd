@echo off
call mvn -f ../pom.xml -Dmaven.test.skip=true  -P dist,dist-jcfc  clean deploy
pause


