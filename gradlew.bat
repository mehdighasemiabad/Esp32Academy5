@echo off
setlocal
set "APP_HOME=%~dp0"
set "VERSION=8.9"
where gradle >nul 2>nul
if %ERRORLEVEL% EQU 0 (gradle -p "%APP_HOME%" %* & exit /b %ERRORLEVEL%)
if "%GRADLE_USER_HOME%"=="" set "GRADLE_USER_HOME=%USERPROFILE%\.gradle"
set "DIST=%GRADLE_USER_HOME%\wrapper\dists\gradle-%VERSION%-bin\esp32academy"
set "BIN=%DIST%\gradle-%VERSION%\bin\gradle.bat"
if not exist "%BIN%" (
  if not exist "%DIST%" mkdir "%DIST%"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$ProgressPreference='SilentlyContinue'; Invoke-WebRequest 'https://services.gradle.org/distributions/gradle-%VERSION%-bin.zip' -OutFile '%DIST%\gradle.zip'; Expand-Archive -Force '%DIST%\gradle.zip' '%DIST%'; Remove-Item '%DIST%\gradle.zip'"
  if errorlevel 1 exit /b %ERRORLEVEL%
)
call "%BIN%" -p "%APP_HOME%" %*
exit /b %ERRORLEVEL%
