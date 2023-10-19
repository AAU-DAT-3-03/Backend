@echo off
cd %~dp0
call mvn clean
call mvn package
call mvn dependency:copy-dependencies