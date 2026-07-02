<p align="center">
  <img src="https://raw.githubusercontent.com/Torvinek/PlusX-Mobile/main/assets/plusx-banner.png" width="820" alt="PlusX Mobile — nieoficjalna aplikacja Android dla panelu PlusX">
</p>

<h1 align="center">PlusX Mobile</h1>

<p align="center">
  <strong>Nieoficjalna aplikacja Android z natywnym interfejsem mobilnym dla panelu PlusX.</strong>
</p>

<p align="center">
  <a href="https://github.com/Torvinek/PlusX-Mobile/actions/workflows/android-ci.yml"><img src="https://img.shields.io/github/actions/workflow/status/Torvinek/PlusX-Mobile/android-ci.yml?branch=main&style=for-the-badge&label=Build" alt="Status builda"></a>
  <a href="https://github.com/Torvinek/PlusX-Mobile/releases/tag/v1.5.1"><img src="https://img.shields.io/badge/Wersja-1.5.1-1683D8?style=for-the-badge" alt="Wersja 1.5.1"></a>
  <img src="https://img.shields.io/badge/Android-9%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android 9 lub nowszy">
  <img src="https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin 2.0.21">
  <a href="LICENSE"><img src="https://img.shields.io/badge/Kod-source--available-C62828?style=for-the-badge" alt="Licencja Source-Available"></a>
</p>

<p align="center">
  <a href="https://github.com/Torvinek/PlusX-Mobile/releases/latest"><strong>⬇️ Pobierz najnowsze APK</strong></a>
  &nbsp;•&nbsp;
  <a href="PRIVACY.md">Prywatność</a>
  &nbsp;•&nbsp;
  <a href="SECURITY.md">Bezpieczeństwo</a>
  &nbsp;•&nbsp;
  <a href="BUILDING.md">Samodzielny build</a>
</p>

---

> [!IMPORTANT]
> **PlusX Mobile jest projektem niezależnym i nieoficjalnym.** Nie jest własnością operatora panelu PlusX ani oficjalną aplikacją jego właściciela. Do działania wymagane jest własne, aktywne konto w obsługiwanym panelu.

> [!CAUTION]
> **Kod jest publicznie widoczny w celu przejrzystości, audytu bezpieczeństwa i lokalnego testowania, ale projekt nie jest open source.** Bez wcześniejszej pisemnej zgody autora zabronione jest kopiowanie kodu do innych repozytoriów, tworzenie lub publikowanie mirrorów i niezależnych forków, redystrybucja kodu albo APK oraz wydawanie aplikacji pochodnych. Wiążące warunki znajdują się w [LICENSE-PlusXMobile](LICENSE-PlusXMobile).

## Spis treści

- [O projekcie](#o-projekcie)
- [Najważniejsze funkcje](#najważniejsze-funkcje)
- [Jak działa aplikacja](#jak-działa-aplikacja)
- [Prywatność i diagnostyka](#prywatność-i-diagnostyka)
- [Bezpieczeństwo sekretów](#bezpieczeństwo-sekretów)
- [Instalacja](#instalacja)
- [Samodzielny build](#samodzielny-build)
- [Weryfikacja wydania](#weryfikacja-wydania)
- [Dokumentacja](#dokumentacja)
- [Licencje](#licencje)

## O projekcie

Oryginalny panel `new.plusx.tv` został przygotowany przede wszystkim z myślą o komputerach. PlusX Mobile porządkuje jego najważniejsze funkcje i przedstawia je w interfejsie dopasowanym do telefonu.

Aplikacja nie ma dostępu do bazy danych panelu i nie omija zabezpieczeń logowania. Działa jako mobilna warstwa nad istniejącym serwisem:

1. logowanie odbywa się na oryginalnej stronie w `WebView`,
2. użytkownik sam wpisuje dane i przechodzi reCAPTCHA,
3. aplikacja przejmuje lokalną sesję po poprawnym zalogowaniu,
4. strony panelu są pobierane bezpośrednio przez urządzenie użytkownika,
5. parsery zamieniają potrzebne fragmenty HTML na natywny interfejs Androida,
6. płatności, zmiana hasła, 2FA i finalne potwierdzenie zakupu pozostają w oryginalnym portalu.

## Najważniejsze funkcje

| Obszar | Funkcje |
|---|---|
| **Panel główny** | saldo, odświeżanie balansu, szybka nawigacja, animacje oraz jasny i ciemny motyw |
| **Wiadomości** | informacje z panelu i backendu, oczyszczanie treści oraz usuwanie duplikatów |
| **Programy na dziś** | wydarzenia EPG, kanał, godziny rozpoczęcia i zakończenia oraz bitrate |
| **Pakiety** | ceny miesięczne i roczne, wybór użytkownika oraz potwierdzenie przed przejściem do zakupu |
| **Reseller Panel** | automatyczna paginacja, status konta, data wygaśnięcia i przejście do pakietów lub M3U |
| **Linki M3U** | prawdziwy User Key, TiviMate, Smart IPTV, SS IPTV, CDN-y oraz EPG |
| **Ustawienia** | profil, motyw, zmiana hasła i 2FA przez oryginalny portal |
| **Diagnostyka** | raport podstawowy albo zaawansowany wysyłany dopiero po zatwierdzeniu przez użytkownika |

## Jak działa aplikacja

### Panel PlusX

Połączenia do panelu są wykonywane bezpośrednio pomiędzy telefonem użytkownika a:

```text
https://new.plusx.tv
```

Cookies sesji są używane wyłącznie dla właściwego hosta panelu. Nie są przekazywane do backendu projektu.

### Backend projektu

Backend Torvinek służy do pobierania wiadomości, programu EPG oraz — po zgodzie użytkownika — odbierania diagnostyki:

```text
https://backend.torvinek.pl
```

Klient panelu i klient backendu są rozdzielone. Token backendu nie jest wysyłany do panelu PlusX, a cookies panelu nie są wysyłane do backendu Torvinek.

## Prywatność i diagnostyka

### Aplikacja nie wysyła do backendu Torvinek

- loginu ani hasła do panelu,
- cookies sesji,
- User Key,
- pełnych linków M3U i parametrów `access_key`,
- danych płatniczych,
- zawartości kont resellerów,
- surowego HTML stron panelu.

### Diagnostyka

Diagnostyka nie jest wysyłana automatycznie w tle. Użytkownik sam otwiera formularz, wybiera zakres danych i zatwierdza wysłanie.

Raport podstawowy może zawierać:

- adres email kontaktowy wpisany świadomie przez użytkownika,
- opis problemu,
- datę i godzinę,
- wersję aplikacji,
- aktualny ekran i motyw,
- producenta i model urządzenia,
- wersję Androida,
- rozdzielczość, gęstość i orientację ekranu,
- język systemu,
- informacje techniczne potrzebne do odtworzenia błędu.

Raport zaawansowany może dodatkowo zawierać oczyszczony snapshot wybranej sekcji aplikacji. Sanitizator usuwa typowe sekrety, tokeny, hasła, adresy IP, parametry dostępowe i obce adresy email przed wysłaniem.

Pełny opis znajduje się w [PRIVACY.md](PRIVACY.md) i [NETWORK.md](NETWORK.md).

## Bezpieczeństwo sekretów

Publiczne repozytorium nie zawiera:

- tokenu backendu,
- haseł do keystore,
- prywatnego keystore,
- sesji Telegram,
- danych Cloudflare Tunnel,
- plików `.env`, `local.properties` ani `signing.properties`,
- danych logowania użytkowników.

Sekrety oficjalnego builda są przekazywane przez GitHub Actions Secrets. Lokalne pliki konfiguracyjne są ignorowane przez Git.

> [!WARNING]
> Sekret używany przez aplikację kliencką nie może być jednocześnie wbudowany w APK i absolutnie niewydobywalny z tego APK. Dlatego token mobilny powinien mieć wyłącznie minimalne uprawnienia do wiadomości, EPG i diagnostyki. Nie powinien zapewniać dostępu administracyjnego, dostępu do Telegrama, SSH, Cloudflare ani infrastruktury serwera.

Szczegółowy model zabezpieczeń backendu opisuje [BACKEND_SECURITY.md](BACKEND_SECURITY.md).

## Instalacja

1. Otwórz stronę [najnowszego wydania](https://github.com/Torvinek/PlusX-Mobile/releases/latest).
2. Pobierz plik APK.
3. Porównaj jego sumę SHA-256 z plikiem dołączonym do wydania.
4. Zezwól Androidowi na instalowanie aplikacji z wybranego źródła.
5. Zainstaluj APK i zaloguj się przez oryginalny formularz panelu.

**Wymagany system:** Android 9 lub nowszy, czyli API 28+.

## Samodzielny build

Kod można pobrać lokalnie w celu prywatnego audytu i testowania zgodnie z [LICENSE](LICENSE).

Wymagania:

- JDK 17,
- Android SDK 35,
- Android Studio lub Gradle Wrapper.

### Linux / macOS

```bash
./gradlew test assembleDebug
```

### Windows

```powershell
.\gradlew.bat test assembleDebug
```

Gotowy debug APK:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Podstawowe funkcje panelu można zbudować bez prywatnego tokenu. Wiadomości, EPG i wysyłka diagnostyki do chronionego backendu wymagają lokalnej konfiguracji opisanej w [BUILDING.md](BUILDING.md).

## Weryfikacja wydania

Każdy push i pull request uruchamia testy oraz budowanie debug APK w GitHub Actions. Oficjalne wydania utworzone z tagów mogą być podpisywane automatycznie i publikowane razem z sumą SHA-256.

### Windows PowerShell

```powershell
Get-FileHash .\PlusX-Mobile-v1.5.1.apk -Algorithm SHA256
```

### Linux

```bash
sha256sum PlusX-Mobile-v1.5.1.apk
```

Wynik musi być identyczny z wartością w pliku `.sha256` dołączonym do wydania.

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
- [VALIDATION.md](VALIDATION.md) — wykonane kontrole projektu.

## Licencje

### Kod PlusX Mobile

Kod należący do autora projektu jest udostępniony na niestandardowej licencji **PlusX Mobile Source-Available License v1.0**:

- [LICENSE](LICENSE)

To nie jest licencja open source. Pozwala na przeglądanie kodu, prywatny audyt, lokalne sklonowanie i prywatny build testowy, ale bez pisemnej zgody autora zabrania między innymi:

- kopiowania lub importowania całości albo istotnych fragmentów kodu do innego repozytorium,
- tworzenia i utrzymywania publicznych mirrorów,
- publikowania forka jako osobnego projektu lub alternatywnej dystrybucji,
- redystrybucji kodu źródłowego,
- redystrybucji oficjalnego albo zmodyfikowanego APK,
- tworzenia i publikowania aplikacji pochodnych,
- wykorzystania komercyjnego,
- usuwania informacji o autorze i prawach autorskich,
- podszywania się pod oficjalne wydanie PlusX Mobile.

### Licencja MIT

Repozytorium zawiera również:

- [LICENSE-MIT](LICENSE-MIT)

Plik ten dotyczy wyłącznie elementów, komponentów albo fragmentów kodu, które są wyraźnie oznaczone jako objęte licencją MIT. **Nie zmienia on licencji całego projektu i nie daje prawa do kopiowania lub redystrybucji kodu PlusX Mobile objętego `LICENSE-PlusXMobile`.**

Biblioteki i zasoby stron trzecich zachowują własne licencje, znaki towarowe i prawa ich właścicieli.

---

<p align="center">
  <strong>© Torvinek 2026 · PlusX Mobile 1.5.1</strong><br>
  <sub>Source available for transparency — redistribution prohibited.</sub>
</p>
