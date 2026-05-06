@echo off
REM Simple Maven setup for Windows using curl
REM This script downloads and extracts Maven

echo Downloading Maven 3.9.6...
curl -L -o maven.zip "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip"

if %errorlevel% equ 0 (
    echo Extracting Maven...
    powershell -Command "Expand-Archive -Path maven.zip -DestinationPath C:\ -Force"
    del maven.zip
    echo Maven installed to C:\apache-maven-3.9.6
    echo Please add to PATH: C:\apache-maven-3.9.6\bin
    echo Then run: mvn clean install -DskipTests
) else (
    echo Download failed. Please:
    echo 1. Download from: https://maven.apache.org/download.cgi
    echo 2. Extract to C:\apache-maven-3.9.6
    echo 3. Add C:\apache-maven-3.9.6\bin to your PATH
)
