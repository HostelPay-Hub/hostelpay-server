@echo off
REM Maven Setup Script for Windows
REM Downloads Maven 3.9.6 if not already installed

setlocal enabledelayedexpansion

set MAVEN_HOME=C:\apache-maven-3.9.6
set MAVEN_URL=https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip
set MAVEN_ZIP=C:\apache-maven-3.9.6.zip

REM Check if Maven already exists
if exist "%MAVEN_HOME%" (
    echo Maven is already installed at %MAVEN_HOME%
    echo Running Maven...
    cd /d "%~dp0"
    "%MAVEN_HOME%\bin\mvn.cmd" clean install -DskipTests
    exit /b
)

echo Maven not found. Installing to %MAVEN_HOME%...

REM Download Maven
echo Downloading Maven from %MAVEN_URL%
powershell -Command "(New-Object Net.WebClient).DownloadFile('%MAVEN_URL%', '%MAVEN_ZIP%')"

REM Extract Maven
echo Extracting Maven...
powershell -Command "Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath 'C:\' -Force"

REM Remove zip file
del "%MAVEN_ZIP%"

REM Add to PATH
echo Adding Maven to PATH...
setx MAVEN_HOME "%MAVEN_HOME%"
setx PATH "%PATH%;%MAVEN_HOME%\bin"

echo Maven installation complete!
echo Please restart PowerShell/CMD and run: mvn --version
echo Then run: mvn clean install -DskipTests
