@echo off
set GIT_BASH=D:\devel\shell\Git\bin\bash
REM set VER=3.1.11-SNAPSHOT
REM call mvn -f ../pom.xml versions:set -DnewVersion=%VER%
REM call mvn -f ../pom.xml versions:commit
REM %GIT_BASH% -c 'sed -i -r -e  "s/<project.polaris.revision>.+<\/project.polaris.revision>/<project.polaris.revision>%VER%<\/project.polaris.revision>/g" ../pom.xml'

%GIT_BASH% -c 'sh mvn-version.sh'
pause
