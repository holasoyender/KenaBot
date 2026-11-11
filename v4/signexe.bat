@echo off

echo Signing attempt 1...
signtool.exe sign /f %1 /p %2 /t http://timestamp.verisign.com/scripts/timstamp.dll /v %3
IF ERRORLEVEL 0 exit /b 0

echo Signing attempt 2...
signtool.exe sign /f %1 /p %2 /t http://timestamp.comodoca.com/authenticode /v %3
IF ERRORLEVEL 0 exit /b 0

echo Signing attempt 3...
signtool.exe sign /f %1 /p %2 /t http://timestamp.globalsign.com/scripts/timestamp.dll /v %3
IF ERRORLEVEL 0 exit /b 0

echo Signing failed.
exit /b 1