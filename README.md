<p align="center">
  <img src="assets/plusx-banner.png" alt="PlusX.tv" width="100%">
</p>

<h1 align="center">PlusX Mobile</h1>

<p align="center">
  <strong>Nieoficjalna aplikacja Android z wygodnym, mobilnym interfejsem dla panelu PlusX.</strong>
</p>

<p align="center">
  Logowanie, saldo, wiadomości, programy, pakiety, reseller, M3U, historia zakupów, aktualizacje i diagnostyka — w jednej aplikacji.
</p>

<p align="center">
  <a href="https://github.com/Torvinek/PlusX-Mobile/actions/workflows/android-ci.yml">
    <img alt="Android CI" src="https://img.shields.io/github/actions/workflow/status/Torvinek/PlusX-Mobile/android-ci.yml?branch=main&style=for-the-badge&label=Android%20CI">
  </a>
  <a href="https://github.com/Torvinek/PlusX-Mobile/releases/latest">
    <img alt="Najnowsza wersja" src="https://img.shields.io/github/v/release/Torvinek/PlusX-Mobile?style=for-the-badge&label=wersja">
  </a>
  <img alt="Android 9+" src="https://img.shields.io/badge/Android-9%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white">
  <img alt="Kotlin 2.0.21" src="https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white">
  <a href="LICENSE">
    <img alt="Licencja source-available" src="https://img.shields.io/badge/licencja-source--available-C74700?style=for-the-badge">
  </a>
</p>

<p align="center">
  <a href="https://github.com/Torvinek/PlusX-Mobile/releases/latest"><strong>⬇️ Pobierz najnowsze APK</strong></a>
  ·
  <a href="#najważniejsze-funkcje">Funkcje</a>
  ·
  <a href="#instalacja">Instalacja</a>
  ·
  <a href="#prywatność-i-bezpieczeństwo">Prywatność</a>
  ·
  <a href="#budowanie-ze-źródeł">Budowanie</a>
  ·
  <a href="https://tipply.pl/@torvinek">☕ Postaw kawę</a>
</p>

<p align="center">
  Jeśli PlusX Mobile Ci się spodobał albo po prostu okazał się przydatny, możesz postawić mi małą kawę.<br>
  <strong>Dzięki za każde wsparcie ☕</strong>
</p>

---

> [!IMPORTANT]
> **PlusX Mobile jest projektem niezależnym i nieoficjalnym.**
> Nie jest własnością operatora panelu PlusX ani oficjalną aplikacją właściciela usługi.
> Do działania wymagane jest własne, aktywne konto w obsługiwanym panelu.

## W skrócie

PlusX Mobile powstał dlatego, że oryginalny panel `new.plusx.tv` został przygotowany głównie z myślą o komputerach i dużych ekranach.

Aplikacja nie zastępuje panelu i nie tworzy własnego systemu kont. Korzysta z istniejącej strony PlusX, ale prezentuje jej najważniejsze funkcje w układzie dopasowanym do telefonu.

### Jak to działa po ludzku?

1. Uruchamiasz PlusX Mobile.
2. Logujesz się na **oryginalnej stronie PlusX** otwartej w WebView.
3. reCAPTCHA rozwiązujesz normalnie i ręcznie.
4. Aplikacja wykorzystuje aktywną sesję do pobierania potrzebnych stron panelu.
5. Dane są porządkowane lokalnie i wyświetlane w czytelnych kartach, listach oraz przyciskach.
6. Operacje wrażliwe, takie jak płatności, zmiana hasła, 2FA i końcowe potwierdzenie zakupu, pozostają na oryginalnej stronie.

Pełna historia zmian poszczególnych wersji znajduje się w sekcji [Releases](https://github.com/Torvinek/PlusX-Mobile/releases).

---

<a id="najważniejsze-funkcje"></a>
## Najważniejsze funkcje

| Obszar | Co potrafi aplikacja |
|---|---|
| **Logowanie** | Oryginalne logowanie przez WebView, ręczna reCAPTCHA oraz obsługa wygaśnięcia sesji. |
| **Panel główny** | Saldo, odświeżanie danych i szybki dostęp do najważniejszych zakładek. |
| **Wiadomości** | Wiadomości z panelu PlusX oraz komunikaty pobierane z backendu Torvinek. |
| **Programy na dziś** | Aktualne wydarzenia EPG, sortowanie, odświeżanie, ukrywanie zakończonych pozycji i oznaczenie programu trwającego teraz. |
| **Historia zakupów** | Historia z `balance_history.php`, filtrowanie po użytkowniku, rozpoznawanie doładowań i prawdopodobnych pakietów. |
| **Pakiety** | Lista dostępnych pakietów i cen, wybór użytkownika oraz potwierdzenie przed przejściem do zakupu. |
| **Reseller Panel** | Konta, statusy, daty wygaśnięcia, notatki, paginacja i szybkie przejście do pakietów lub M3U. |
| **Linki M3U** | User Key, TiviMate, Smart IPTV, SS IPTV, EPG, wybór użytkownika, kopiowanie i pobieranie. |
| **Doładowanie konta** | Oryginalny ekran płatności w WebView — bez własnego formularza kart płatniczych. |
| **Ustawienia** | Motyw jasny/ciemny, dane konta, 2FA, zmiana hasła, diagnostyka i zakładka „O aplikacji”. |
| **Aktualizacje** | Sprawdzanie nowej wersji, opis wydania z GitHuba, pobieranie APK i uruchamianie systemowego instalatora. |
| **Powiadomienia** | Informacje o nowych wiadomościach oraz dostępnej aktualizacji. |
| **Diagnostyka** | Ręcznie uruchamiany raport podstawowy lub zaawansowany z automatycznym oczyszczaniem danych wrażliwych. |

---

## Jak działa aplikacja od strony technicznej?

```text
Użytkownik
│
├── WebView ───────────────► new.plusx.tv
│                            logowanie, reCAPTCHA, płatności,
│                            zmiana hasła i 2FA
│
├── CookieManager
│
├── HTTPS ─────────────────► new.plusx.tv
│                            saldo, pakiety, reseller,
│                            M3U, profil i historia
│
└── HTTPS ─────────────────► backend.torvinek.pl
                             wiadomości, EPG oraz ręcznie
                             wysłana diagnostyka
```

### Najważniejsze zasady sieciowe

- Cookies panelu są używane wyłącznie podczas komunikacji z `new.plusx.tv`.
- Cookies PlusX nie są wysyłane do backendu Torvinek.
- Komunikacja aplikacji odbywa się przez HTTPS.
- Adresy podszywające się pod właściwą domenę, np. `new.plusx.tv.example.org`, są odrzucane.
- Token backendu jest przekazywany podczas budowania i nie jest publikowany w repozytorium.
- Token aplikacyjny backendu powinien mieć wyłącznie minimalne, nieadministracyjne uprawnienia.

Więcej informacji: [NETWORK.md](NETWORK.md) oraz [BACKEND_SECURITY.md](BACKEND_SECURITY.md).

---

<a id="prywatność-i-bezpieczeństwo"></a>
## Prywatność i bezpieczeństwo

### Logowanie

Login, hasło i reCAPTCHA są obsługiwane na oryginalnej stronie `new.plusx.tv` otwartej w WebView.

PlusX Mobile nie tworzy osobnego formularza udającego stronę logowania i nie wysyła danych logowania do backendu Torvinek.

### Czego aplikacja nie wysyła do backendu Torvinek?

- loginu do PlusX,
- hasła,
- cookies sesji,
- danych reCAPTCHA,
- danych płatniczych,
- zawartości formularza karty,
- pełnej sesji panelu.

### Płatności

Płatności pozostają na stronie PlusX. Aplikacja nie implementuje własnego systemu płatności i nie przechwytuje danych karty.

### Uprawnienia Androida

| Uprawnienie | Zastosowanie |
|---|---|
| `INTERNET` | Logowanie, pobieranie danych z panelu, wiadomości, EPG, aktualizacje i ręcznie wysyłana diagnostyka. |
| `POST_NOTIFICATIONS` | Wyświetlanie własnych powiadomień o nowych wiadomościach i dostępnej aktualizacji. |

Na Androidzie 13 i nowszym system może poprosić użytkownika o zgodę na wyświetlanie powiadomień.

Brak zgody **nie blokuje działania aplikacji**. Wyłączone będą jedynie komunikaty pojawiające się poza aplikacją.

`POST_NOTIFICATIONS` nie daje aplikacji dostępu do cudzych powiadomień ani ich treści. Pozwala wyłącznie wyświetlać własne komunikaty systemowe.

PlusX Mobile nie żąda dostępu do:

- kontaktów,
- SMS-ów,
- mikrofonu,
- aparatu,
- lokalizacji,
- historii połączeń.

Szczegóły: [PRIVACY.md](PRIVACY.md) oraz [SECURITY.md](SECURITY.md).

---

## Diagnostyka

Diagnostyka nie jest wysyłana automatycznie przy każdym błędzie. Raport jest przesyłany dopiero po wejściu do odpowiedniej zakładki, wypełnieniu formularza i zatwierdzeniu wysyłki.

### Diagnostyka podstawowa może zawierać

- adres e-mail kontaktowy podany przez użytkownika,
- opis problemu,
- datę i godzinę,
- wersję aplikacji,
- model urządzenia,
- producenta telefonu,
- wersję Androida i SDK,
- rozdzielczość, orientację oraz gęstość ekranu,
- język systemu,
- nazwę aktualnego ekranu,
- wybrany motyw,
- podstawowe informacje pomocnicze związane z wybraną sekcją.

### Diagnostyka zaawansowana

Może dołączyć lokalny snapshot danych potrzebnych do sprawdzenia wybranej funkcji.

Przed wysłaniem aplikacja próbuje automatycznie usunąć między innymi:

- parametry sesji,
- nagłówki Bearer,
- wartości wyglądające jak hasła, tokeny lub sekrety,
- adresy IP,
- obce adresy e-mail.

Diagnostyki zaawansowanej nie można uruchomić dla ekranów doładowania ani ustawień konta.

> [!NOTE]
> Automatyczne oczyszczanie ogranicza ryzyko ujawnienia danych wrażliwych, ale w opisie problemu nadal nie należy wpisywać haseł, tokenów, cookies, User Key ani pełnych linków M3U.

---

## Powiadomienia i aktualizacje

Aplikacja może informować o:

- nowych wiadomościach PlusX,
- dostępnej nowej wersji,
- opisie zmian pobranym z GitHuba.

W zakładce **O aplikacji** można ręcznie sprawdzić dostępność aktualizacji.

Aby zainstalować pobrane APK bezpośrednio z aplikacji, Android może poprosić o zezwolenie PlusX Mobile na instalowanie aplikacji z tego źródła. Zgodę można później wyłączyć w ustawieniach systemowych.

Aktualizację można również pobrać ręcznie z sekcji [Releases](https://github.com/Torvinek/PlusX-Mobile/releases).

---

<a id="instalacja"></a>
## Instalacja

### Wymagania

- Android 9 lub nowszy,
- własne konto w panelu PlusX,
- połączenie z internetem.

### Najprostsza instalacja

1. Otwórz [najnowsze wydanie](https://github.com/Torvinek/PlusX-Mobile/releases/latest).
2. Rozwiń sekcję **Assets**.
3. Pobierz plik `PlusXMobile-Release-...apk`.
4. Otwórz pobrany plik na telefonie.
5. Jeżeli Android poprosi o zgodę, zezwól przeglądarce lub menedżerowi plików na instalowanie aplikacji z tego źródła.
6. Zainstaluj PlusX Mobile.
7. Uruchom aplikację i zaloguj się na oryginalnej stronie panelu.

> [!TIP]
> Ostrzeżenie o aplikacji instalowanej spoza Google Play nie oznacza automatycznie wykrycia wirusa. Android może wyświetlać takie ostrzeżenie w przypadku nowych lub mało znanych aplikacji APK instalowanych ręcznie.

---

## Weryfikacja pobranego APK

Oficjalne wydanie może zawierać plik `.sha256`. Pozwala on sprawdzić, czy APK nie zostało uszkodzone albo podmienione po pobraniu.

### Windows PowerShell

```powershell
Get-FileHash -Algorithm SHA256 .\PlusXMobile-Release-v1.5.4.apk
```

### Linux

```bash
sha256sum PlusXMobile-Release-v1.5.4.apk
```

Porównaj otrzymany wynik z zawartością pliku `.sha256` dodanego do tego samego wydania.

---

<a id="budowanie-ze-źródeł"></a>
## Budowanie ze źródeł

Kod projektu jest publicznie widoczny w celu przejrzystości, audytu, nauki oraz lokalnego testowania.

Projekt jest **source-available**, a nie open source w znaczeniu licencji OSI.

### Wymagania techniczne

- JDK 17,
- Android SDK 35,
- Gradle Wrapper dołączony do repozytorium,
- połączenie z internetem podczas pierwszego pobierania zależności.

### Build debug

Linux/macOS:

```bash
./gradlew test assembleDebug
```

Windows:

```powershell
.\gradlew.bat test assembleDebug
```

Wynik:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Funkcje korzystające z prywatnego backendu wymagają lokalnej konfiguracji tokenu aplikacyjnego. Szczegóły znajdują się w [BUILDING.md](BUILDING.md).

> [!WARNING]
> Nie publikuj plików `local.properties`, `signing.properties`, keystore, haseł ani tokenów backendu.

---

## Technologie

| Element | Wartość |
|---|---|
| Język | Kotlin |
| Minimalny Android | Android 9 / API 28 |
| Target / Compile SDK | 35 |
| JVM | 17 |
| Namespace | `pl.torvinek.plusxmobile` |
| Interfejs | Natywny Android + WebView w miejscach wymagających oryginalnego portalu |
| Źródło danych panelu | HTML z `new.plusx.tv`, przetwarzany lokalnie |
| Backend pomocniczy | `backend.torvinek.pl` — wiadomości, EPG i diagnostyka |
| CI/CD | GitHub Actions |
| Dystrybucja | Podpisane APK w GitHub Releases |

---

## Struktura projektu

```text
app/
├── src/main/java/pl/torvinek/plusxmobile/
│   ├── MainActivity.kt
│   ├── M3uHtmlParser.kt
│   ├── ResellerHtmlParser.kt
│   ├── ResellerPaginator.kt
│   ├── ServerMessagesParser.kt
│   ├── DiagnosticSanitizer.kt
│   ├── NetworkPolicy.kt
│   └── AppConfig.kt
├── src/main/res/
└── src/test/

.github/workflows/
├── android-ci.yml
└── release.yml
```

---

## Ograniczenia

- PlusX nie udostępnia oficjalnego API wykorzystywanego przez ten projekt.
- Część danych jest pobierana przez parsowanie HTML.
- Zmiana struktury strony może tymczasowo zepsuć parser konkretnej zakładki.
- Logowanie, reCAPTCHA, płatności, 2FA i zmiana hasła pozostają w WebView.
- Niektóre funkcje zależą od dostępności `new.plusx.tv` albo backendu Torvinek.
- Aplikacja może wymagać aktualizacji po większych zmianach w panelu.
- Projekt nie gwarantuje działania z każdym typem konta ani z każdą przyszłą wersją strony.

---

## Zgłaszanie problemów

Najwygodniej wysłać zgłoszenie bezpośrednio z aplikacji:

1. Wejdź w **Ustawienia → Diagnostyka**.
2. Wybierz diagnostykę podstawową albo zaawansowaną.
3. Opisz krótko problem i ekran, na którym wystąpił.
4. Wyślij raport.

Możesz również utworzyć zgłoszenie w zakładce [Issues](https://github.com/Torvinek/PlusX-Mobile/issues).

Nie publikuj w zgłoszeniu:

- loginu,
- hasła,
- cookies,
- User Key,
- pełnych linków M3U,
- tokenów,
- danych płatniczych.

---

## Dokumentacja

- [PRIVACY.md](PRIVACY.md) — prywatność i diagnostyka,
- [SECURITY.md](SECURITY.md) — zgłaszanie problemów bezpieczeństwa,
- [NETWORK.md](NETWORK.md) — połączenia sieciowe,
- [BACKEND_SECURITY.md](BACKEND_SECURITY.md) — zabezpieczenia backendu,
- [BUILDING.md](BUILDING.md) — lokalna konfiguracja i budowanie,
- [VALIDATION.md](VALIDATION.md) — walidacja repozytorium i buildów,
- [CONTRIBUTING.md](CONTRIBUTING.md) — zasady przesyłania zmian.

---

## Licencja

Główna część projektu jest udostępniana na warunkach [PlusX Mobile Source-Available License](LICENSE).

Kod można przeglądać, analizować oraz lokalnie budować wyłącznie w granicach określonych w licencji.

Bez pisemnej zgody autora zabronione jest między innymi:

- kopiowanie projektu do innych repozytoriów,
- tworzenie i publikowanie mirrorów,
- redystrybucja kodu lub APK,
- publikowanie własnych albo zmodyfikowanych wersji aplikacji,
- wykorzystywanie projektu komercyjnie,
- usuwanie informacji o autorze,
- podszywanie się pod oficjalne wydanie,
- dodawanie złośliwego kodu i rozpowszechnianie go jako PlusX Mobile.

Plik [LICENSE-MIT](LICENSE-MIT) dotyczy wyłącznie elementów wyraźnie oznaczonych jako objęte licencją MIT. Nie zastępuje głównej licencji projektu.

---

## Wesprzyj projekt

Jeśli PlusX Mobile Ci się spodobał albo ułatwił korzystanie z panelu, możesz postawić mi małą kawę.

<p align="center">
  <a href="https://tipply.pl/@torvinek"><strong>☕ Postaw kawę na Tipply</strong></a>
</p>

<p align="center">
  Każde wsparcie pomaga mi dalej rozwijać i testować aplikację. Dzięki!
</p>

---

## Autor

**Torvinek**

- GitHub: [@Torvinek](https://github.com/Torvinek)
- Telegram: [@marclxz](https://t.me/marclxz)
- E-mail: [prywatny.marcelo@gmail.com](mailto:prywatny.marcelo@gmail.com)

<p align="center">
  <strong>© Torvinek 2026</strong><br>
  PlusX Mobile — prywatny, niezależny projekt mobilnego klienta panelu PlusX.
</p>
