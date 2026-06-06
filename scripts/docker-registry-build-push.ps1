[CmdletBinding(PositionalBinding = $false)]
param(
    [string]$Tag = (Get-Date -Format "yyyyMMdd-HHmmss"),
    [string]$Registry = "registry.megacorpservers.com/cust-019e9476bb747460b9aa0048",
    [string]$FrontendApiBaseUrl = "http://localhost:8080",
    [switch]$SkipBuild,
    [switch]$SkipPush
)

$ErrorActionPreference = "Stop"

$backendImage = "$Registry/golf-desk-backend:$Tag"
$frontendImage = "$Registry/golf-desk-frontend:$Tag"

Write-Host "Using TAG=$Tag"
Write-Host "Backend image:  $backendImage"
Write-Host "Frontend image: $frontendImage"

if (-not $SkipBuild) {
    docker build -t $backendImage -f Dockerfile .
    docker build -t $frontendImage --build-arg VITE_API_BASE_URL=$FrontendApiBaseUrl -f frontend/Dockerfile frontend
}

if (-not $SkipPush) {
    docker push $backendImage
    docker push $frontendImage
}

Write-Host "Registry compose TAG=$Tag"
