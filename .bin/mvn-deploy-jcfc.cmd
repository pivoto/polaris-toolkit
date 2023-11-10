@echo off

set goal=%*
if "%goal%" == "" set goal=clean deploy
echo goal: %goal%

call mvn -f ../pom.xml -Dmaven.test.skip=true  -P repos-jcfc  %goal%
pause


