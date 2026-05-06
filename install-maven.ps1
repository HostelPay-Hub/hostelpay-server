# Maven Installation Script for Windows PowerShell
# Downloads and installs Apache Maven 3.9.6

$MavenVersion = "3.9.6"
$MavenHome = "C:\apache-maven-$MavenVersion"
$MavenUrl = "https://archive.apache.org/dist/maven/maven-3/$MavenVersion/binaries/apache-maven-$MavenVersion-bin.zip"
$ZipFile = "C:\apache-maven-$MavenVersion.zip"

# Check if Maven already installed
if (Test-Path $MavenHome) {
    Write-Host "Maven already installed at $MavenHome" -ForegroundColor Green
    $env:MAVEN_HOME = $MavenHome
    $env:Path += ";$MavenHome\bin"
    & "$MavenHome\bin\mvn.cmd" --version
    exit
}

Write-Host "Installing Maven $MavenVersion..." -ForegroundColor Cyan

# Download Maven
Write-Host "Downloading Maven from $MavenUrl..." -ForegroundColor Yellow
try {
    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    (New-Object Net.WebClient).DownloadFile($MavenUrl, $ZipFile)
    Write-Host "Downloaded successfully" -ForegroundColor Green
} catch {
    Write-Host "Failed to download Maven: $_" -ForegroundColor Red
    exit 1
}

# Extract Maven
Write-Host "Extracting Maven..." -ForegroundColor Yellow
try {
    Expand-Archive -Path $ZipFile -DestinationPath "C:\" -Force
    Remove-Item $ZipFile -Force
    Write-Host "Extraction complete" -ForegroundColor Green
} catch {
    Write-Host "Failed to extract: $_" -ForegroundColor Red
    exit 1
}

# Set environment variables for current session
$env:MAVEN_HOME = $MavenHome
$env:Path += ";$MavenHome\bin"

# Set permanent environment variables
[Environment]::SetEnvironmentVariable("MAVEN_HOME", $MavenHome, [EnvironmentVariableTarget]::User)
$CurrentPath = [Environment]::GetEnvironmentVariable("Path", [EnvironmentVariableTarget]::User)
if ($CurrentPath -notlike "*$MavenHome\bin*") {
    [Environment]::SetEnvironmentVariable("Path", "$CurrentPath;$MavenHome\bin", [EnvironmentVariableTarget]::User)
}

Write-Host "Maven installed successfully!" -ForegroundColor Green
Write-Host "Testing Maven installation..." -ForegroundColor Yellow
& "$MavenHome\bin\mvn.cmd" --version
