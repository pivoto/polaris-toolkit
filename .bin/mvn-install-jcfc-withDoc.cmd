@echo off

set goal=%*
if "%goal%" == "" set goal=clean install
echo goal: %goal%

call mvn -f ../pom.xml -Dmaven.test.skip=true -P repos-jcfc,withDoc  %goal%

pause
