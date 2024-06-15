@echo off

REM if not "%GIT_BASH%"=="" echo %GIT_BASH%
if "%GIT_BASH%"=="" (
	set GIT_BASH=D:\devel\shell\Git\bin\bash
)
%GIT_BASH% -c 'sh version.sh'
pause
