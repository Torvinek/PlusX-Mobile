param(
    [Parameter(Mandatory=$true)]
    [string]$ApkPath
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$expectedFile = Join-Path $root "config\official-signing-cert.sha256"
$expected = (Get-Content -LiteralPath $expectedFile | Where-Object { $_ -and -not $_.Trim().StartsWith("#") } | Select-Object -First 1).Trim().ToLowerInvariant()

$candidates = @(
    "$env:ANDROID_HOME\build-tools\35.0.0\apksigner.bat",
    "$env:ANDROID_SDK_ROOT\build-tools\35.0.0\apksigner.bat",
    "D:\AndroidBuild\android-sdk\build-tools\35.0.0\apksigner.bat"
)
$apksigner = $candidates | Where-Object { $_ -and (Test-Path $_) } | Select-Object -First 1
if (-not $apksigner) { throw "apksigner not found." }

$output = & $apksigner verify --verbose --print-certs $ApkPath
$actualLine = $output | Select-String -Pattern "Signer #1 certificate SHA-256 digest:" | Select-Object -First 1
if (-not $actualLine) { throw "Cannot read APK signing certificate." }
$actual = ($actualLine.ToString().Split(":")[-1]).Trim().ToLowerInvariant()

if ($actual -ne $expected) {
    Write-Error "ERROR: APK uses a different signing certificate than official PlusX Mobile releases.`n`nPublishing and installation have been blocked."
    exit 1
}

Write-Host "SIGNATURE OK"
Write-Host "APK uses the official PlusX Mobile signing certificate."
