@echo off
REM HostelPay Hub - Automatic Backend Startup Script
REM This script attempts to start the backend using available Maven

setlocal enabledelayedexpansion

echo.
echo ========================================
echo HostelPay Hub - Backend Startup
echo ========================================
echo.

REM Check if running from correct directory
if not exist "pom.xml" (
    echo Error: pom.xml not found. Please run from hostelpay-server directory.
    pause
    exit /b 1
)

REM Try different Maven paths
set MAVEN_CMD=
set MAVEN_HOME=

REM Check if mvn is in PATH
where mvn >nul 2>&1
if %errorlevel% equ 0 (
    set MAVEN_CMD=mvn
    echo Found Maven in PATH
    goto BUILD
)

REM Check common installation paths
for %%P in (
    "C:\apache-maven-3.9.6"
    "C:\apache-maven-3.9.5"
    "C:\Program Files\maven"
    "C:\Program Files (x86)\maven"
) do (
    if exist "%%P\bin\mvn.cmd" (
        set MAVEN_HOME=%%P
        set MAVEN_CMD=%%P\bin\mvn.cmd
        echo Found Maven at: !MAVEN_HOME!
        goto BUILD
    )
)

REM Check JetBrains installations
for %%P in (
    "%USERPROFILE%\AppData\Local\JetBrains\*\plugins\maven\lib\maven3"
) do (
    if exist "%%P\bin\mvn.cmd" (
        set MAVEN_HOME=%%P
        set MAVEN_CMD=%%P\bin\mvn.cmd
        echo Found Maven in JetBrains: !MAVEN_HOME!
        goto BUILD
    )
)

REM Maven not found
echo.
echo ERROR: Maven not found in PATH or common locations
echo.
echo Please do ONE of the following:
echo 1. Use Spring Tool Suite (File ^> Open ^> hostelpay-server ^> Run As ^> Spring Boot App)
echo 2. Download Maven from: https://maven.apache.org/download.cgi
echo 3. Extract to: C:\apache-maven-3.9.6
echo 4. Add to PATH: setx PATH "%%PATH%%;C:\apache-maven-3.9.6\bin"
echo.
pause
exit /b 1

:BUILD
echo.
echo Building backend with Maven...
echo Command: !MAVEN_CMD! spring-boot:run
echo.

call "!MAVEN_CMD!" spring-boot:run

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo Backend started successfully!
    echo Available at: http://localhost:8080
    echo ========================================
) else (
    echo.
    echo ========================================
    echo Backend startup failed
    echo ========================================
)

pause
