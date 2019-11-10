@echo off

rem construct classpath
setlocal EnableDelayedExpansion 

set CP=

for %%L in (.\lib\*.jar ) do set CP=!CP!;%%L

rem echo %CP%
set CLASSPATH=%CLASSPATH%;%CP%

rem start application
java com.bivgroup.flextera.insurance.bivfront.db.DatabaseManager %* 
