param(
    [Parameter(Mandatory=$true)]
    [string]$ApkPath
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
& (Join-Path $PSScriptRoot "verify-signing-certificate.ps1") -ApkPath $ApkPath

$adbCandidates = @("$env:ANDROID_HOME\platform-tools\adb.exe", "$env:ANDROID_SDK_ROOT\platform-tools\adb.exe", "D:\AndroidBuild\android-sdk\platform-tools\adb.exe")
$adb = $adbCandidates | Where-Object { $_ -and (Test-Path $_) } | Select-Object -First 1
if (-not $adb) { throw "adb not found." }

$aaptCandidates = @("$env:ANDROID_HOME\build-tools\35.0.0\aapt.exe", "$env:ANDROID_SDK_ROOT\build-tools\35.0.0\aapt.exe", "D:\AndroidBuild\android-sdk\build-tools\35.0.0\aapt.exe")
$aapt = $aaptCandidates | Where-Object { $_ -and (Test-Path $_) } | Select-Object -First 1
if (-not $aapt) { throw "aapt not found." }

$badging = & $aapt dump badging $ApkPath
$packageLine = $badging | Select-String -Pattern "^package:" | Select-Object -First 1
$pkg = [regex]::Match($packageLine, "name='([^']+)'").Groups[1].Value
$apkVersionCode = [int][regex]::Match($packageLine, "versionCode='([^']+)'").Groups[1].Value
$apkVersionName = [regex]::Match($packageLine, "versionName='([^']+)'").Groups[1].Value
if ($pkg -ne "pl.torvinek.plusxmobile") { throw "INSTALL BLOCKED: wrong package name $pkg." }

$installed = & $adb shell dumpsys package pl.torvinek.plusxmobile
$installedCode = 0
$installedName = "not installed"
if ($installed) {
    $codeLine = $installed | Select-String -Pattern "versionCode=" | Select-Object -First 1
    $nameLine = $installed | Select-String -Pattern "versionName=" | Select-Object -First 1
    if ($codeLine) { $installedCode = [int][regex]::Match($codeLine, "versionCode=(\d+)").Groups[1].Value }
    if ($nameLine) { $installedName = [regex]::Match($nameLine, "versionName=([^\s]+)").Groups[1].Value }
}

Write-Host "Installed version: $installedName ($installedCode)"
Write-Host "APK version: $apkVersionName ($apkVersionCode)"
Write-Host "Package: $pkg"
Write-Host "Signature: MATCH"

if ($installedCode -gt 0 -and $apkVersionCode -lt $installedCode) {
    Write-Error "INSTALL BLOCKED: downgrade is not allowed."
    exit 1
}

$isDebug = $ApkPath -match "(?i)debug"
if ($isDebug) {
    & $adb install -r -t $ApkPath
} else {
    & $adb install -r $ApkPath
}
