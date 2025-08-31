@echo off
REM Run the Spring Boot application
cd %~dp0
java -jar target\handle_files-0.0.1-SNAPSHOT.jar
pause