# Laby dev client for one Minecraft version (default 1.16.5).
# Example: .\scripts\run-all-clients.ps1
# Example: .\scripts\run-all-clients.ps1 -Version 1.21.11
param([string]$Version = "1.16.5")
$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
if (-not (Test-Path (Join-Path $root "gradlew.bat"))) {
  $root = (Get-Location).Path
}

$java21 = "C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot"
if (Test-Path $java21) {
  $env:JAVA_HOME = $java21
}

$task = "client_v$Version"
Write-Host ":game-runner:$task"
Write-Host "JAVA_HOME=$($env:JAVA_HOME)"
& (Join-Path $root "gradlew.bat") ":game-runner:$task" --no-daemon
exit $LASTEXITCODE
