<p align="center">
  <img src="assets/plusx-banner.png" width="820" alt="PlusX Mobile">
</p>

<h1 align="center">PlusX Mobile</h1>

<p align="center">
  <strong>Nieoficjalna aplikacja Android z natywnym interfejsem mobilnym dla panelu PlusX.</strong>
</p>

<p align="center">
  <a href="https://github.com/Torvinek/PlusX-Mobile/actions/workflows/android-ci.yml"><img src="https://img.shields.io/github/actions/workflow/status/Torvinek/PlusX-Mobile/android-ci.yml?branch=main&style=for-the-badge&label=Build" alt="Build"></a>
  <a href="https://github.com/Torvinek/PlusX-Mobile/releases/latest"><img src="https://img.shields.io/github/v/release/Torvinek/PlusX-Mobile?style=for-the-badge&label=Wersja" alt="Wersja"></a>
  <img src="https://img.shields.io/badge/Android-9%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android 9+">
  <img src="https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin">
</p>

> [!IMPORTANT]
> Projekt jest niezależny i nieoficjalny. Do działania wymaga własnego, aktywnego konta w obsługiwanym panelu.

## Jak działa

Logowanie odbywa się w oryginalnej stronie `new.plusx.tv` otwartej w WebView. Użytkownik sam wpisuje dane i przechodzi reCAPTCHA. Po zalogowaniu aplikacja używa cookies wyłącznie do pobierania stron panelu i przedstawia dane w natywnym interfejsie Androida.

Aplikacja nie posiada dostępu do bazy panelu, nie omija logowania i nie omija reCAPTCHA.

## Funkcje

- natywny dashboard z saldem,
- wiadomości z panelu i backendu,
- „Programy na dziś” / EPG,
- pakiety oraz wybór użytkownika,
- panel resellera z paginacją,
- linki M3U, EPG i prawdziwy User Key,
- TiviMate, Smart IPTV oraz SS IPTV,
- jasny i ciemny motyw,
- płatności i finalny zakup przez oryginalny portal,
- podstawowa i zaawansowana diagnostyka wysyłana po świadomym potwierdzeniu użytkownika.

## Diagnostyka

Diagnostyka działa tak samo jak w prywatnej wersji aplikacji.

**Podstawowa** może zawierać m.in.:

- email kontaktowy podany przez użytkownika,
- opis problemu,
- datę i godzinę,
- wersję aplikacji,
- aktualny ekran i motyw,
- producenta, model i nazwę urządzenia,
- wersję Androida,
- rozdzielczość, gęstość i orientację ekranu,
- język systemu,
- informację o wybranym użytkowniku M3U/Pakietów,
- liczbę zapisanych snapshotów.

**Zaawansowana** dodatkowo może dołączyć oczyszczony snapshot wybranej sekcji: Dashboard, Wiadomości, Programy na dziś, Pakiety, Reseller Panel lub Linki M3U.

Przed wysłaniem aplikacja usuwa z raportu typowe sekrety, tokeny, hasła, adresy IP i cudze adresy email. Diagnostyka jest wysyłana dopiero po wejściu użytkownika w odpowiednią funkcję i zatwierdzeniu formularza. Szczegóły: [PRIVACY.md](PRIVACY.md).

## Rozdzielenie ruchu sieciowego

Aplikacja ma dwie osobne strefy zaufania:

```text
new.plusx.tv
  └─ cookies sesji panelu

backend.torvinek.pl
  └─ token aplikacyjny tylko dla wiadomości, EPG i diagnostyki
```

Kod sprawdza dokładny host i HTTPS. Cookies panelu nie są dołączane do żądań backendu, a token backendu nie jest dołączany do żądań panelu. Szczegóły: [NETWORK.md](NETWORK.md).

## Sekrety i publiczne repozytorium

Repozytorium nie zawiera:

- tokenu backendu,
- haseł do keystore,
- prywatnego keystore,
- sesji Telegram,
- danych Cloudflare,
- lokalnych plików konfiguracyjnych.

Wartości dla oficjalnego builda są przekazywane przez zaszyfrowane GitHub Actions Secrets albo lokalny, ignorowany przez Git plik `local.properties`.

> [!WARNING]
> Długotrwałego sekretu używanego przez aplikację kliencką nie da się uczynić absolutnie niewydobywalnym z gotowego APK. Dlatego token aplikacyjny musi mieć wyłącznie minimalne uprawnienia potrzebne do odczytu wiadomości/EPG i wysyłania diagnostyki, bez dostępu administracyjnego, SSH, Telegrama ani panelu backendu. Prawdziwe sekrety pozostają wyłącznie po stronie serwera.

## Budowanie

Wymagania:

- JDK 17,
- Android SDK 35,
- Android Studio lub Gradle Wrapper.

```bash
./gradlew test assembleDebug
```

APK debug:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Aby wiadomości, EPG i diagnostyka działały z chronionym backendem, ustaw lokalnie `PLUSX_BACKEND_TOKEN` zgodnie z [BUILDING.md](BUILDING.md). Bez tokenu aplikacja nadal się kompiluje, a funkcje panelu PlusX pozostają dostępne.

## Weryfikacja

Każdy push i pull request uruchamia testy i buduje APK przez GitHub Actions. Wydanie z taga może zostać podpisane i opublikowane automatycznie wraz z sumą SHA-256.

```powershell
Get-FileHash .\PlusX-Mobile.apk -Algorithm SHA256
```

## Dokumentacja

- [PRIVACY.md](PRIVACY.md) — prywatność i diagnostyka,
- [NETWORK.md](NETWORK.md) — połączenia i separacja danych,
- [BUILDING.md](BUILDING.md) — lokalny build i sekrety,
- [SECURITY.md](SECURITY.md) — zgłaszanie problemów bezpieczeństwa,
- [BACKEND_SECURITY.md](BACKEND_SECURITY.md) — model tokenów i wariant bez sekretu w APK,
- [CONTRIBUTING.md](CONTRIBUTING.md) — zasady zmian,
- [UPLOAD_TO_GITHUB.md](UPLOAD_TO_GITHUB.md) — publikacja i sekrety CI,
- [VALIDATION.md](VALIDATION.md) — wykonane kontrole.

## Licencja

Kod aplikacji jest udostępniony na licencji [MIT](LICENSE). Nazwy, logotypy i usługi stron trzecich pozostają własnością ich właścicieli.

---

<p align="center">
  <strong>© Torvinek 2026</strong>
</p>
