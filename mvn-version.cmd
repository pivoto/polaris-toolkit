@echo off
rem call mvn versions:set -DnewVersion=2.0.4-SNAPSHOT
call mvn versions:set -DnewVersion=2.0.4
call mvn versions:commit
pause
