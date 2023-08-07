@echo off
mvn versions:set -DnewVersion=2.0.3-SNAPSHOT
mvn versions:commit
pause
