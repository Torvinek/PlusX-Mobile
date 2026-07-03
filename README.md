<p align="center">
  <img src="assets/plusx-banner.png" alt="PlusX.tv" width="100%">
</p>

<h1 align="center">PlusX Mobile</h1>

<p align="center">
  <strong>Nieoficjalna aplikacja Android z natywnym, mobilnym interfejsem dla panelu PlusX.</strong>
</p>

<p align="center">
  Logowanie, saldo, wiadomości, programy, pakiety, reseller, M3U, historia zakupów i diagnostyka — w jednej aplikacji.
</p>

<p align="center">
  <a href="https://github.com/Torvinek/PlusX-Mobile/actions/workflows/android-ci.yml">
    <img alt="Android CI" src="https://img.shields.io/github/actions/workflow/status/Torvinek/PlusX-Mobile/android-ci.yml?branch=main&style=for-the-badge&label=Android%20CI">
  </a>
  <img alt="Wersja 1.5.3" src="https://img.shields.io/badge/wersja-1.5.3-169BD5?style=for-the-badge">
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
  <a href="#co-nowego">Co nowego</a>
  ·
  <a href="#instalacja">Instalacja</a>
  ·
  <a href="#prywatność-i-bezpieczeństwo">Prywatność</a>
  ·
  <a href="#budowanie-ze-źródeł">Budowanie</a>
</p>

---

> [!IMPORTANT]
> **PlusX Mobile jest projektem niezależnym i nieoficjalnym.**
> Nie jest własnością operatora panelu PlusX ani oficjalną aplikacją właściciela usługi.
> Do działania wymagane jest własne, aktywne konto w panelu [PlusX](https://new.plusx.tv/login.php).

## Co to właściwie jest?

PlusX Mobile to mobilny klient panelu `new.plusx.tv`.

Oryginalny panel został zaprojektowany głównie z myślą o komputerach i dużych ekranach. Na telefonie korzystanie z niego może być niewygodne, dlatego aplikacja pobiera dane z istniejącego panelu i pokazuje je w czytelnym, natywnym interfejsie Androida.

Aplikacja:

1. otwiera oryginalne logowanie PlusX w WebView,
2. pozwala normalnie wpisać dane i ręcznie przejść reCAPTCHA,
3. korzysta z aktywnej sesji panelu,
4. pobiera potrzebne strony przez HTTPS,
5. lokalnie porządkuje dane,
6. wyświetla je w mobilnych kartach, listach i przyciskach.

Wrażliwe operacje, takie jak płatności, zmiana hasła, 2FA i końcowe potwierdzenie zakupu, pozostają na oryginalnej stronie PlusX.

### Po ludzku

Nie dostajesz kolejnego desktopowego panelu wciśniętego na mały ekran. Dostajesz normalną aplikację na telefon, która korzysta z istniejącego konta PlusX i upraszcza obsługę najważniejszych funkcji.

---

## Najważniejsze funkcje

| Obszar | Co potrafi aplikacja |
|---|---|
| **Logowanie** | Oryginalne logowanie przez WebView, ręczna reCAPTCHA i obsługa wygaśnięcia sesji. |
| **Panel główny** | Saldo, szybkie odświeżanie i skróty do najważniejszych funkcji. |
| **Wiadomości** | Wiadomości z panelu PlusX oraz komunikaty pobierane z backendu Torvinek. |
| **Programy na dziś** | Wydarzenia EPG, sortowanie, odświeżanie, ukrywanie zakończonych pozycji i oznaczenie programu trwającego teraz. |
| **Historia zakupów** | Dane z `balance_history.php`, filtrowanie po użytkowniku, łączenie doładowań i rozpoznawanie prawdopodobnego pakietu. |
| **Pakiety** | Lista pakietów i cen, wybór użytkownika oraz potwierdzenie przed przejściem do zakupu. |
| **Reseller Panel** | Lista kont, statusy, daty wygaśnięcia, notatki, paginacja i szybkie przejście do pakietów lub M3U. |
| **Linki M3U** | User Key, TiviMate, Smart IPTV, SS IPTV, EPG, wybór użytkownika, kopiowanie i pobieranie. |
| **Doładowanie konta** | Oryginalny ekran płatności otwierany w WebView — bez własnego formularza kart płatniczych. |
| **Ustawienia** | Motyw jasny/ciemny, dane konta, 2FA, zmiana hasła, diagnostyka, informacje o aplikacji i aktualizacjach. |
| **Powiadomienia** | Informacje o nowych wiadomościach PlusX oraz dostępnej aktualizacji aplikacji. |
| **Diagnostyka** | Ręcznie uruchamiany raport podstawowy lub zaawansowany z automatycznym oczyszczaniem danych wrażliwych. |

---

<a id="co-nowego"></a>
## Co nowego

### Wersja 1.5.3

Wersja **1.5.3** skupia się na historii operacji, poprawnym rozpoznawaniu aktywnych pakietów oraz wygodniejszej obsłudze wielu kont.

#### Historia zakupów i doładowań

- Dodano zakładkę **Historia zakupów**, pobierającą dane z `balance_history.php`.
- Dodano filtrowanie historii po użytkowniku.
- Lista użytkowników jest tworzona na podstawie **Reseller Panelu** oraz konta głównego.
- Naprawiono wykrywanie losowych identyfikatorów operacji jako nazw użytkowników.
- Dodano rozpoznawanie doładowań konta.
- Kolejne doładowania są łączone w jedną, czytelną pozycję.
- Wpis historii może pokazywać:
  - datę,
  - kwotę,
  - użytkownika,
  - prawdopodobny pakiet.
- Kwoty są prezentowane w formacie `x.xx EUR`.
- Poprawiono rozpoznawanie pakietów po cenie z uwzględnieniem możliwych rabatów resellerów.
- Historia może uzupełniać pozycje informacjami z Reseller Panelu:
  - notatką,
  - statusem konta,
  - aktywnym pakietem,
  - terminem wygaśnięcia — jeżeli dane są dostępne.

#### Pakiety i status konta

- Naprawiono problem, przez który nieaktywne konto mogło wyglądać jak aktywne w zakładce **Kup pakiet**.
- Dodano blokadę zakupu innego pakietu, gdy użytkownik ma już aktywny pakiet.

#### Ustawienia i wygląd

- Całe kafelki ustawień są teraz klikalne.
- Dodano czytelne oznaczenie, że kafelek można otworzyć.
- Usunięto informację o blokadzie po IP z ustawień.
- Zmieniono komunikat przy adresie e-mail — jego zmiana wymaga kontaktu z supportem PlusX.
- Poprawiono przełączanie motywu oraz wykrywanie jasnego i ciemnego trybu systemowego.
- Zachowano updater aplikacji i zakładkę **O aplikacji**.

### Wersja 1.5.2

#### Dodano

- powiadomienia o nowych wiadomościach PlusX,
- powiadomienia o dostępnej nowej wersji aplikacji,
- zakładkę **O aplikacji** w ustawieniach,
- ręczne sprawdzanie aktualizacji z poziomu aplikacji,
- pobieranie opisu zmian bezpośrednio z GitHuba,
- plik SHA-256 do weryfikacji APK przy wydaniu.

#### Poprawiono

- sortowanie w zakładce **Programy na dziś**,
- ukrywanie wydarzeń, które już się zakończyły,
- oznaczenie programu trwającego w danym momencie,
- odświeżanie programów z backendu,
- wykrywanie timeoutu sesji, gdy saldo nie zostanie znalezione,
- automatyczny powrót do logowania po wygaśnięciu sesji,
- komunikat `Zostałeś wylogowany (timeout)`,
- kodowanie UTF-8 i wyświetlanie polskich znaków.

### Wersja 1.5.1

- Poprawiono sortowanie w zakładce **Programy na dziś**.
- Najnowsze i najpóźniejsze wydarzenia są wyświetlane wyżej, a starsze niżej.
- Poprawiono przycisk **Odśwież balans**.
- Saldo jest pobierane na świeżo, bez używania starego cache.
- Dodano wykrywanie wygaśniętej sesji.
- Jeśli saldo nie zostanie znalezione, aplikacja traktuje to jako timeout sesji.
- Dodano komunikat `Zostałeś wylogowany (timeout)`.
- Po timeout aplikacja czyści sesję i cookies.
- Użytkownik jest automatycznie przenoszony do ekranu logowania.

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
                             wiadomości, EPG i ręcznie
                             wysłana diagnostyka
```

### Najważniejsze zasady sieciowe

- Cookies panelu są używane wyłącznie wobec dokładnego hosta `new.plusx.tv`.
- Cookies PlusX nie są wysyłane do backendu Torvinek.
- Aplikacja akceptuje wyłącznie połączenia HTTPS.
- Adresy podszywające się pod właściwą domenę, np. `new.plusx.tv.example.org`, są odrzucane.
- Token backendu jest przekazywany podczas budowania i nie jest publikowany w repozytorium.
- Token aplikacyjny backendu powinien mieć wyłącznie minimalne, nieadministracyjne uprawnienia.

Szczegóły znajdują się w [NETWORK.md](NETWORK.md) i [BACKEND_SECURITY.md](BACKEND_SECURITY.md).

---

## Prywatność i bezpieczeństwo

### Czego aplikacja nie wysyła do backendu Torvinek?

- loginu do PlusX,
- hasła,
- cookies sesji,
- danych reCAPTCHA,
- danych płatniczych,
- zawartości formularza karty,
- pełnej sesji panelu.

### Logowanie

Login, hasło oraz reCAPTCHA są obsługiwane na oryginalnej stronie `new.plusx.tv`, otwartej w WebView. Aplikacja nie tworzy osobnego formularza udającego logowanie PlusX.

### Płatności

Płatności pozostają na stronie PlusX. Aplikacja nie implementuje własnego systemu płatności i nie przechwytuje danych karty.

### Uprawnienia Androida

| Uprawnienie | Zastosowanie |
|---|---|
| `INTERNET` | Logowanie, pobieranie danych z `new.plusx.tv`, wiadomości, EPG, sprawdzanie aktualizacji i ręcznie wysyłana diagnostyka. |
| `POST_NOTIFICATIONS` | Wyświetlanie własnych powiadomień o nowych wiadomościach PlusX i dostępnej aktualizacji. |

Na Androidzie 13 i nowszym system prosi użytkownika o zgodę na wyświetlanie powiadomień.

Brak zgody **nie blokuje działania aplikacji**. Wyłączone będą tylko komunikaty wyświetlane poza aplikacją.

`POST_NOTIFICATIONS` nie daje PlusX Mobile dostępu do cudzych powiadomień ani ich treści. Pozwala jedynie wyświetlać własne komunikaty systemowe.

Aplikacja nie żąda dostępu do:

- kontaktów,
- SMS-ów,
- mikrofonu,
- aparatu,
- lokalizacji,
- historii połączeń.

Więcej: [PRIVACY.md](PRIVACY.md) i [SECURITY.md](SECURITY.md).

---

## Diagnostyka

Diagnostyka nie jest wysyłana automatycznie przy każdym błędzie. Użytkownik uruchamia ją ręcznie w ustawieniach aplikacji.

### Diagnostyka podstawowa może zawierać

- e-mail kontaktowy wpisany przez użytkownika,
- opis problemu,
- datę i godzinę,
- wersję aplikacji,
- model urządzenia,
- wersję Androida i SDK,
- rozdzielczość, orientację i gęstość ekranu,
- język systemu,
- nazwę aktualnego ekranu,
- wybrany motyw,
- podstawowe informacje pomocnicze związane z wybraną sekcją.

### Diagnostyka zaawansowana

Może dołączyć lokalny snapshot danych potrzebnych do zdiagnozowania wybranej sekcji.

Przed wysłaniem aplikacja próbuje usunąć między innymi:

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

Aktualizację można sprawdzić ręcznie w zakładce **O aplikacji**.

Aby zainstalować aktualizację bezpośrednio z aplikacji, Android może poprosić o zezwolenie PlusX Mobile na instalowanie aplikacji z tego źródła. Jest to osobne ustawienie systemowe i można je później wyłączyć.

Aktualizację można także pobrać ręcznie z sekcji [Releases](https://github.com/Torvinek/PlusX-Mobile/releases).

---

<a id="instalacja"></a>
## Instalacja

### Wymagania

- Android 9 lub nowszy,
- własne konto w panelu [PlusX](https://new.plusx.tv/login.php),
- połączenie z internetem.

### Krok po kroku

1. Otwórz [najnowsze wydanie](https://github.com/Torvinek/PlusX-Mobile/releases/latest).
2. Rozwiń sekcję **Assets**.
3. Pobierz plik APK.
4. Otwórz pobrany plik na telefonie.
5. Jeśli Android poprosi o zgodę, zezwól przeglądarce lub menedżerowi plików na instalowanie aplikacji z tego źródła.
6. Zainstaluj PlusX Mobile.
7. Uruchom aplikację i zaloguj się na oryginalnej stronie panelu.

> [!TIP]
> Ostrzeżenie o aplikacji instalowanej spoza Google Play nie oznacza automatycznie wykrycia wirusa. Android może pokazywać taki komunikat dla nowych lub mało znanych aplikacji APK instalowanych ręcznie.

---

## Weryfikacja pobranego APK

Każde oficjalne wydanie może zawierać plik z sumą SHA-256. Pozwala on sprawdzić, czy APK nie zostało uszkodzone lub podmienione po pobraniu.

### Windows PowerShell

```powershell
Get-FileHash -Algorithm SHA256 .\PlusX-Mobile-v1.5.3.apk
```

### Linux

```bash
sha256sum PlusX-Mobile-v1.5.3.apk
```

Porównaj wynik z plikiem `.sha256` dodanym do tego samego wydania.

---

<a id="budowanie-ze-źródeł"></a>
## Budowanie ze źródeł

Kod jest publicznie widoczny w celu przejrzystości, audytu, nauki i lokalnego testowania. Projekt jest **source-available**, a nie open source w znaczeniu licencji OSI.

### Wymagania techniczne

- JDK 17,
- Android SDK 35,
- Gradle Wrapper dołączony do repozytorium,
- internet podczas pierwszego pobierania zależności.

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

- PlusX nie udostępnia oficjalnego API używanego przez ten projekt.
- Część danych jest pobierana przez parsowanie HTML.
- Zmiana struktury strony może czasowo zepsuć parser konkretnej zakładki.
- Logowanie, reCAPTCHA, płatności, 2FA i zmiana hasła pozostają w WebView.
- Niektóre funkcje zależą od dostępności `new.plusx.tv` lub backendu Torvinek.
- Aplikacja nie gwarantuje działania z każdym typem konta ani z każdą przyszłą wersją panelu.

---

## Zgłaszanie problemów

Najlepiej zgłosić problem bezpośrednio z aplikacji:

1. Wejdź w **Ustawienia → Diagnostyka**.
2. Wybierz diagnostykę podstawową albo zaawansowaną.
3. Opisz krótko, co nie działa i na jakim ekranie wystąpił problem.
4. Wyślij raport.

Możesz też utworzyć zgłoszenie w zakładce [Issues](https://github.com/Torvinek/PlusX-Mobile/issues).

Nie publikuj w zgłoszeniach:

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

Kod można przeglądać, analizować i lokalnie budować wyłącznie w granicach określonych w licencji.

Bez pisemnej zgody autora zabronione jest między innymi:

- kopiowanie projektu do innych repozytoriów,
- tworzenie i publikowanie mirrorów,
- redystrybucja kodu lub APK,
- publikowanie własnych albo zmodyfikowanych wersji aplikacji,
- używanie projektu komercyjnie,
- usuwanie informacji o autorze,
- podszywanie się pod oficjalne wydanie,
- dodawanie złośliwego kodu i rozpowszechnianie go jako PlusX Mobile.

Plik [LICENSE-MIT](LICENSE-MIT) dotyczy wyłącznie elementów wyraźnie oznaczonych jako objęte licencją MIT. Nie zastępuje głównej licencji projektu.

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
