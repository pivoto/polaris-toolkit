@echo off
REM set VER=3.1.1
set VER=3.1.1
set GIT_BASH=D:\devel\shell\Git\bin\bash

call mvn -f ../pom.xml versions:set -DnewVersion=%VER%
call mvn -f ../pom.xml versions:commit
%GIT_BASH% -c 'sed -i -r -e  "s/<project.polaris.revision>.+<\/project.polaris.revision>/<project.polaris.revision>%VER%<\/project.polaris.revision>/g" ../pom.xml'
pause
