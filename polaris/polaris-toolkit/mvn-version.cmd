@echo off
rem mvn versions:set -DnewVersion=2.0.3-SNAPSHOT
mvn versions:set -DnewVersion=2.0.3
mvn versions:commit
pause
