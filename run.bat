@echo off
title mongodb_demo
setlocal enabledelayedexpansion
@REM class�ļ����ڵ�Ŀ¼
set classPath=-cp "target/classes;lib/*"
@REM java.exe���ڵ�Ŀ¼
set javaexe="%JAVA_HOME%/bin/java"
%javaexe% -Dfile.encoding=UTF-8 %classPath% com.demo.mongodb.MongodbApplication