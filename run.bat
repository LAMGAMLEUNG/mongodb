@echo off
title mongodb_demo
setlocal enabledelayedexpansion
@REM class文件所在的目录
set classPath=-cp "target/classes;lib/*"
@REM java.exe所在的目录
set javaexe="%JAVA_HOME%/bin/java"
%javaexe% -Dfile.encoding=UTF-8 %classPath% com.demo.mongodb.MongodbApplication