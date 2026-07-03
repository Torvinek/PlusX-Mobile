# Wrzucenie projektu na GitHub

## 1. Wgraj zawartość ZIP-a

Pliki z archiwum powinny trafić bezpośrednio do katalogu głównego repozytorium, a nie do dodatkowego folderu.

Najwygodniej przez Git:

```bash
git clone https://github.com/Torvinek/PlusX-Mobile.git
cd PlusX-Mobile
# skopiuj tutaj zawartość paczki
git add .
git commit -m "Open source Android client"
git push
```

## 2. Dodaj sekrety GitHub Actions

Repozytorium → `Settings` → `Secrets and variables` → `Actions`.

Wymagane do podpisanego release:

- `PLUSX_BACKEND_TOKEN` — ograniczony token mobilny tylko do wiadomości, EPG i diagnostyki,
- `ANDROID_KEYSTORE_BASE64`,
- `ANDROID_KEYSTORE_PASSWORD`,
- `ANDROID_KEY_ALIAS`,
- `ANDROID_KEY_PASSWORD`.

Nie używaj jako `PLUSX_BACKEND_TOKEN` hasła panelu administracyjnego ani tokenu dającego dostęp do Telegrama/LXC.

## 3. Pierwsza kontrola

Po pushu otwórz zakładkę `Actions` i sprawdź workflow `Android CI`.

## 4. Wydanie

Po ustawieniu sekretów utwórz tag:

```bash
git tag v1.5.1
git push origin v1.5.1
```

Workflow zbuduje podpisany APK, wygeneruje SHA-256 i utworzy GitHub Release.
