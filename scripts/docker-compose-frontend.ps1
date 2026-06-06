[CmdletBinding(PositionalBinding = $false)]
param(
    [string]$Tag = (Get-Date -Format "yyyyMMdd-HHmmss"),
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$ComposeArgs
)

if (-not $ComposeArgs -or $ComposeArgs.Count -eq 0) {
    $ComposeArgs = @("up", "--build")
}

$env:TAG = $Tag
$composeFile = Join-Path $PSScriptRoot "..\docker-compose.frontend.yml"

Write-Host "Using TAG=$Tag"
docker compose -f $composeFile @ComposeArgs
