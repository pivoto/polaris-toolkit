@echo off
rem call mvn versions:set -DnewVersion=3.1.1-SNAPSHOT
call mvn -f ../pom.xml versions:set -DnewVersion=3.1.1-SNAPSHOT
call mvn -f ../pom.xml versions:commit
pause
