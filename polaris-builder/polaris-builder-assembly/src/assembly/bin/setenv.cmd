@echo off
rem setlocal enabledelayedexpansion

if not "%WORKDIR%" == "" goto okHome
pushd %~dp0\..
set WORKDIR=%cd%
popd
:okHome

set LIB_CLASSPATH=
for %%J in (%WORKDIR%\*.jar) do (
	REM echo %%~fJ
	SET LIB_CLASSPATH=!LIB_CLASSPATH!;%%~fJ
)

for %%J in (%WORKDIR%\lib\*.jar) do (
	REM echo %%~fJ
	SET LIB_CLASSPATH=!LIB_CLASSPATH!;%%~fJ
)

set CLASSPATH=.;%WORKDIR%\conf;%LIB_CLASSPATH%;%CLASSPATH%
set CLASSPATH=%CLASSPATH%


echo "WORKDIR: %WORKDIR%"
echo "CLASSPATH: %CLASSPATH%"
echo "JAVA_HOME: %JAVA_HOME%"

