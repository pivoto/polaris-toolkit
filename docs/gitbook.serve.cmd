::该脚本为启动gitbook本地服务器，当服务崩溃时会自动重启
@echo off
:runit
call gitbook serve
@goto runit
