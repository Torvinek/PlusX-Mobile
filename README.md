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
  <img src="https://img.shields.io/badge/licencja-source--available-C62828?style=for-the-badge" alt="Source Available">
</p>

<p align="center">
  <a href="https://github.com/Torvinek/PlusX-Mobile/releases/latest"><strong>⬇ Pobierz najnowsze APK</strong></a>
  ·
  <a href="PRIVACY.md">Prywatność</a>
  ·
  <a href="SECURITY.md">Bezpieczeństwo</a>
  ·
  <a href="BUILDING.md">Samodzielny build</a>
</p>

> [!IMPORTANT]
> **PlusX Mobile jest projektem niezależnym i nieoficjalnym.** Nie jest własnością operatora panelu PlusX ani oficjalną aplikacją jego właściciela. Do działania wymagane jest własne, aktywne konto w obsługiwanym panelu.

> [!CAUTION]
> **Kod jest publicznie dostępny wyłącznie do weryfikacji bezpieczeństwa, prywatnego audytu i lokalnego testowania. To nie jest projekt open source.** Kopiowanie kodu do innych repozytoriów, tworzenie mirrorów, publikowanie forków jako osobnych projektów, redystrybucja APK oraz udostępnianie zmodyfikowanych wersji są zabronione bez wcześniejszej pisemnej zgody autora. Pełne warunki znajdują się w pliku [LICENSE](LICENSE).

## O projekcie

Oryginalny panel `new.plusx.tv` został przygotowany głównie z myślą o komputerach. PlusX Mobile porządkuje najważniejsze funkcje panelu i przedstawia je w interfejsie dopasowanym do telefonu.

Aplikacja nie posiada dostępu do bazy danych panelu i nie korzysta z nieoficjalnego obejścia logowania. Działa jako mobilna warstwa nad istniejącym serwisem:

1. logowanie odbywa się na oryginalnej stronie w `WebView`,
2. użytkownik sam wpisuje dane i przechodzi reCAPTCHA,
3. cookies sesji pozostają używane wyłącznie do komunikacji z panelem,
4. aplikacja pobiera strony panelu i zamienia ich zawartość na natywny interfejs Androida,
5. płatności oraz finalne potwierdzenie zakupu pozostają w oryginalnym portalu.

## Najważniejsze funkcje

| Obszar | Funkcje |
|---|---|
| **Panel główny** | saldo, odświeżanie balansu, szybka nawigacja i jasny/ciemny motyw |
| **Wiadomości** | informacje z panelu oraz backendu, czyszczenie treści i usuwanie duplikatów |
| **Programy na dziś** | dzisiejsze wydarzenia EPG, kanał, godziny rozpoczęcia i zakończenia, bitrate |
| **Pakiety** | lista pakietów, ceny miesięczne i roczne, wybór użytkownika oraz potwierdzenie zakupu |
| **Reseller Panel** | automatyczna paginacja, status konta, data wygaśnięcia, przejście do pakietów i M3U |
| **Linki M3U** | prawdziwy User Key, TiviMate, Smart IPTV, SS IPTV, CDN-y i EPG |
| **Ustawienia** | dane profilu, motyw, zmiana hasła i 2FA przez oryginalny portal |
| **Diagnostyka** | raport podstawowy lub zaawansowany wysyłany dopiero po świadomym zatwierdzeniu |

## Prywatność

PlusX Mobile został zaprojektowany tak, aby dane konta panelu nie trafiały do autora aplikacji ani do backendu projektu.

### Aplikacja nie wysyła do backendu Torvinek

- loginu i hasła do panelu,
- cookies sesji,
- User Key,
- pełnych linków M3U i parametrów `access_key`,
- danych płatniczych,
- zawartości kont resellerów,
- surowego HTML stron panelu.

### Połączenia sieciowe

```text
new.plusx.tv
  └─ logowanie, cookies sesji i dane panelu

backend.torvinek.pl
  └─ wiadomości, EPG i dobrowolnie wysyłana diagnostyka
```

Kod sprawdza dokładny host oraz połączenie HTTPS. Cookies panelu nie są dołączane do żądań backendu, a token backendu nie jest dołączany do żądań panelu. Szczegóły znajdują się w [NETWORK.md](NETWORK.md) i [PRIVACY.md](PRIVACY.md).

## Diagnostyka

Diagnostyka nie działa w tle i nie jest wysyłana automatycznie. Użytkownik sam otwiera formularz, wybiera zakres danych i zatwierdza wysłanie.

Raport podstawowy może zawierać:

- email kontaktowy podany świadomie przez użytkownika,
- opis problemu,
- datę i godzinę,
- wersję aplikacji,
- aktualny ekran i motyw,
- producenta i model urządzenia,
- wersję Androida,
- rozdzielczość, gęstość i orientację ekranu,
- język systemu,
- informację o aktualnie wybranej sekcji,
- liczbę zapisanych snapshotów diagnostycznych.

Raport zaawansowany może dodatkowo zawierać oczyszczony snapshot wybranej sekcji aplikacji. Przed wysłaniem sanitizator usuwa typowe sekrety, tokeny, hasła, adresy IP, parametry dostępowe i obce adresy email.

## Bezpieczeństwo sekretów

Publiczne repozytorium nie zawiera:

- tokenu backendu,
- haseł do keystore,
- prywatnego keystore,
- sesji Telegram,
- danych Cloudflare Tunnel,
- plików `.env`, `local.properties` ani `signing.properties`,
- danych logowania użytkowników.

Wartości używane podczas oficjalnego builda są przekazywane przez GitHub Actions Secrets. Lokalne dane konfiguracyjne są ignorowane przez Git.

> [!WARNING]
> Sekret potrzebny aplikacji klienckiej nie może być jednocześnie używany przez APK i absolutnie niewydobywalny z tego APK. Dlatego token mobilny ma mieć wyłącznie minimalne uprawnienia do wiadomości, EPG i diagnostyki. Nie może zapewniać dostępu administracyjnego, dostępu do Telegrama, SSH, Cloudflare ani infrastruktury serwera.

## Instalacja

1. Otwórz stronę [najnowszego wydania](https://github.com/Torvinek/PlusX-Mobile/releases/latest).
2. Pobierz plik APK.
3. Porównaj jego sumę SHA-256 z plikiem dołączonym do wydania.
4. Zezwól Androidowi na instalowanie aplikacji z wybranego źródła.
5. Zainstaluj APK i zaloguj się przez oryginalny formularz panelu.

Minimalna wersja systemu: **Android 9 (API 28)**.

## Samodzielna weryfikacja i build

Kod jest dostępny publicznie po to, aby można było sprawdzić sposób obsługi sesji, połączeń sieciowych, diagnostyki i danych użytkownika.

Wymagania:

- JDK 17,
- Android SDK 35,
- Android Studio lub Gradle Wrapper.

```bash
./gradlew test assembleDebug
```

Gotowy debug APK:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Funkcje panelu można zbudować bez prywatnego tokenu. Wiadomości, EPG i wysyłka diagnostyki do chronionego backendu wymagają lokalnej konfiguracji opisanej w [BUILDING.md](BUILDING.md).

## Weryfikacja oficjalnego wydania

Każdy push i pull request uruchamia testy oraz budowanie APK w GitHub Actions. Wydania tworzone z tagów mogą być podpisywane automatycznie i publikowane razem z sumą SHA-256.

Windows PowerShell:

```powershell
Get-FileHash .\PlusX-Mobile-v1.5.1.apk -Algorithm SHA256
```

Linux:

```bash
sha256sum PlusX-Mobile-v1.5.1.apk
```

## Zgłaszanie błędów i problemów bezpieczeństwa

- Zwykłe błędy zgłaszaj przez zakładkę **Issues**.
- Nie publikuj w zgłoszeniu cookies, tokenów, User Key, pełnych linków M3U, danych kont ani surowego HTML panelu.
- Problemy bezpieczeństwa zgłaszaj zgodnie z [SECURITY.md](SECURITY.md).

## Dokumentacja

- [PRIVACY.md](PRIVACY.md) — prywatność i zakres diagnostyki,
- [NETWORK.md](NETWORK.md) — domeny i separacja ruchu,
- [BUILDING.md](BUILDING.md) — lokalne budowanie i konfiguracja,
- [SECURITY.md](SECURITY.md) — bezpieczne zgłaszanie podatności,
- [BACKEND_SECURITY.md](BACKEND_SECURITY.md) — model tokenów backendu,
- [CONTRIBUTING.md](CONTRIBUTING.md) — zasady proponowania zmian,
- [UPLOAD_TO_GITHUB.md](UPLOAD_TO_GITHUB.md) — publikacja oraz konfiguracja CI,
- [VALIDATION.md](VALIDATION.md) — wykonane kontrole projektu.

## Licencja i zakaz redystrybucji

Copyright © 2026 Torvinek. Wszelkie prawa zastrzeżone.

Kod źródłowy jest udostępniony na niestandardowej licencji **source-available**, wyłącznie w celu przejrzystości, prywatnego audytu i lokalnego testowania.

Bez wcześniejszej pisemnej zgody autora zabronione jest między innymi:

- kopiowanie lub importowanie całości albo istotnych fragmentów kodu do innego repozytorium,
- tworzenie i utrzymywanie publicznych mirrorów projektu,
- publikowanie forka jako oddzielnego projektu lub alternatywnej dystrybucji,
- rozpowszechnianie kodu źródłowego w formie zmienionej lub niezmienionej,
- rozpowszechnianie oficjalnego albo zmodyfikowanego APK poza oficjalnymi wydaniami,
- publikowanie aplikacji pochodnej opartej na tym kodzie,
- wykorzystanie komercyjne,
- usuwanie informacji o autorze i prawach autorskich,
- używanie nazwy, logo lub identyfikacji projektu do podszywania się pod oficjalne wydanie.

Dozwolone jest przeglądanie kodu, prywatny audyt, lokalne sklonowanie w celu weryfikacji oraz lokalny build do własnych testów. Szczegółowe i wiążące warunki znajdują się w pliku [LICENSE](LICENSE).

Nazwy, logotypy i usługi stron trzecich pozostają własnością ich odpowiednich właścicieli.

---

<p align="center">
  <strong>© Torvinek 2026 · PlusX Mobile 1.5.1</strong><br>
  <sub>Source available for transparency — redistribution prohibited.</sub>
</p>
