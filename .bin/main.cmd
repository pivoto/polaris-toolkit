@echo off

REM if not "%GIT_BASH%"=="" echo %GIT_BASH%
if "%GIT_BASH%"=="" (
	set GIT_BASH=D:\devel\shell\Git\bin\bash
)
cd %~dp0
%GIT_BASH% -c "sh main.sh %*"
pause
