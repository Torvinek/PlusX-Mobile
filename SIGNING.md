# PlusX Mobile Signing

The official PlusX Mobile signing certificate was established by version 1.5.3.

All official debug and release builds use the same package name and the same signing certificate:

- package: `pl.torvinek.plusxmobile`
- certificate SHA-256: `96637c94668794e0ce73752eaa132b94e2cc9847f94e50fc2bb638a2498e3297`

Do not generate a new official keystore. Losing the official keystore means existing installations cannot be updated.

Local signing uses `signing.properties`; this file and all keystore files must stay out of Git.

Verify an APK:

```powershell
.\scripts\verify-signing-certificate.ps1 -ApkPath ".\app\build\outputs\apk\release\app-release.apk"
```

Install safely through ADB:

```powershell
.\scripts\install-official-apk.ps1 -ApkPath ".\app\build\outputs\apk\debug\app-debug.apk"
```
