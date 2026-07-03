param(
    [string]$Tag = ""
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$props = @{}
Get-Content -LiteralPath (Join-Path $root "version.properties") | ForEach-Object {
    if ($_ -match "^\s*([^#][^=]+)=(.*)$") { $props[$matches[1].Trim()] = $matches[2].Trim() }
}

$name = $props["VERSION_NAME"]
$code = [int]$props["VERSION_CODE"]
$previous = switch ($name) {
    "1.5.4" { 9 }
    default { $code - 1 }
}

if ($code -le $previous) {
    Write-Error "ERROR: VERSION_CODE $code is not greater than previous VERSION_CODE $previous."
    exit 1
}

if ($Tag -and $Tag.TrimStart("v") -ne $name) {
    Write-Error "ERROR: Git tag $Tag does not match VERSION_NAME $name."
    exit 1
}

Write-Host "VERSION OK: $name ($code)"
