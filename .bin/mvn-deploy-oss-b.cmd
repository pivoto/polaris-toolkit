@echo off
set goal=%*
if "%goal%" == "" set goal=clean deploy
echo goal: %goal%

call mvn -f ../pom.xml -DskipTests -P repos-oss %goal% 2>&1 >mvn-deploy-oss.log

pause
