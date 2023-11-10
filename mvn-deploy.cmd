@echo off
set goal=%*
if "%goal%" == "" set goal=clean deploy
echo goal: %goal%
call mvn -P repos-oss  %goal% -DskipTests
pause
