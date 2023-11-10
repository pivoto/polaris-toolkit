@echo off
rem call mvn versions:set -DnewVersion=3.0.1-SNAPSHOT
call mvn versions:set -DnewVersion=3.1.0
call mvn versions:commit
pause
