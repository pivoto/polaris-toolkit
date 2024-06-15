@echo off
call mvn -f ../pom.xml -DskipTests -P dist,dist-central clean deploy
pause
