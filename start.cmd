@echo off
cd %~dp0
call java -cp target/Backend-1.0-SNAPSHOT.jar;target/dependencies/*  dat3.app.App