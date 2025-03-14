@echo off
REM if not "%GIT_BASH%"=="" echo %GIT_BASH%
if "%GIT_BASH%"=="" (
	set GIT_BASH=D:\devel\shell\Git\bin\bash
)
cd %~dp0
set MAVEN_OPTS=-Dfile.encoding=UTF-8
set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8
%GIT_BASH% -c "sh change-ossrh.sh"
call mvn -f ../pom.xml -DskipTests -P dist,dist-ossrh clean deploy
%GIT_BASH% -c "sh change-central.sh"
pause
