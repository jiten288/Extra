@echo off
rem This batch is to reverse the cancel transactions

set method=%1
set filepath=%2
SET "JAVA_HOME=C:\Program Files\Java\jdk1.7.0_80"
echo JAVA HOME: %JAVA_HOME%
"%JAVA_HOME%\bin\java" -jar  %CD%\TaqseetReconciliationBatch.jar "%method%" "%filepath%"
pause