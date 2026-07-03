<p align="center">
  <img
    src="assets/plusx-banner.png"
    alt="PlusX.tv"
    width="820"
  >
</p>

<h1 align="center">PlusX Mobile</h1>

<p align="center">
  <strong>Nieoficjalna aplikacja Android z natywnym, mobilnym interfejsem dla panelu PlusX.</strong><br>
  Logowanie, saldo, wiadomości, programy, pakiety, reseller, M3U, historia zakupów i diagnostyka — w jednej aplikacji.
</p>

<p align="center">
  <a href="https://github.com/Torvinek/PlusX-Mobile/actions/workflows/android-ci.yml">
    <img alt="Android CI" src="https://img.shields.io/github/actions/workflow/status/Torvinek/PlusX-Mobile/android-ci.yml?branch=main&style=for-the-badge&logo=githubactions&logoColor=white&label=Android%20CI">
  </a>
  <a href="https://github.com/Torvinek/PlusX-Mobile/releases/latest">
    <img alt="Wersja 1.5.3" src="https://img.shields.io/badge/WERSJA-1.5.3-0891D1?style=for-the-badge">
  </a>
  <img alt="Android 9+" src="https://img.shields.io/badge/ANDROID-9%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white">
  <img alt="Kotlin 2.0.21" src="https://img.shields.io/badge/KOTLIN-2.0.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white">
  <a href="LICENSE">
    <img alt="Licencja Source Available" src="https://img.shields.io/badge/LICENCJA-SOURCE--AVAILABLE-C2410C?style=for-the-badge">
  </a>
</p>

<p align="center">
  <a href="https://github.com/Torvinek/PlusX-Mobile/releases/latest"><strong>⬇️ Pobierz najnowsze APK</strong></a>
  &nbsp;•&nbsp;
  <a href="#najważniejsze-funkcje">Funkcje</a>
  &nbsp;•&nbsp;
  <a href="#co-nowego-w-wersji-153">Co nowego</a>
  &nbsp;•&nbsp;
  <a href="#instalacja">Instalacja</a>
  &nbsp;•&nbsp;
  <a href="#prywatność-i-bezpieczeństwo">Prywatność</a>
  &nbsp;•&nbsp;
  <a href="#budowanie-ze-źródeł">Budowanie</a>
</p>

---

> [!IMPORTANT]
> **PlusX Mobile jest projektem niezależnym i nieoficjalnym.** Nie jest własnością operatora panelu PlusX ani oficjalną aplikacją właściciela usługi. Do działania wymagane jest własne, aktywne konto w panelu [PlusX](https://new.plusx.tv/login.php).

## Co to właściwie jest?

PlusX Mobile porządkuje desktopowy panel `new.plusx.tv` i pokazuje jego najważniejsze funkcje w wygodnym układzie na telefon.

Aplikacja **nie zastępuje serwisu PlusX** i nie tworzy własnego systemu logowania. Oryginalne logowanie oraz reCAPTCHA otwierają się bezpośrednio w WebView. Po zalogowaniu aplikacja wykorzystuje aktywną sesję do pobrania danych z panelu i prezentuje je w natywnych ekranach Androida.

### Po ludzku

1. Otwierasz aplikację.
2. Logujesz się na oryginalnej stronie PlusX.
3. reCAPTCHA rozwiązujesz normalnie, ręcznie.
4. Aplikacja pobiera strony panelu w tle.
5. Dane są układane w czytelne karty, listy i przyciski dopasowane do telefonu.
6. Operacje wrażliwe — płatności, zmiana hasła, 2FA i finalne potwierdzenie zakupu — pozostają na oryginalnej stronie.

## Dlaczego powstała ta aplikacja?

Ze wzgledu na to ze oryginalna strona była tworzona z myślą na komputery stacjonarne/duże ekrany więc powstała aplikacja ktora poprostu bazuje na ich stronie tylko ze jest przyjemniejsza w obsłudze oraz kompaktowa.

## Najważniejsze funkcje

| Obszar | Co potrafi aplikacja |
|---|---|
| **Logowanie** | Oryginalne logowanie przez WebView, ręczna reCAPTCHA i obsługa wygaśnięcia sesji. |
| **Panel główny** | Saldo, szybkie odświeżanie, najważniejsze skróty i responsywny interfejs. |
| **Wiadomości** | Wiadomości z panelu PlusX oraz komunikaty pobierane z backendu Torvinek. |
| **Programy na dziś** | Aktualne wydarzenia EPG, sortowanie, ukrywanie zakończonych pozycji i oznaczenie programu trwającego teraz. |
| **Historia zakupów** | Pobieranie historii z `balance_history.php`, filtrowanie po użytkowniku, łączenie doładowań i rozpoznawanie prawdopodobnego pakietu. |
| **Pakiety** | Lista pakietów, ceny miesięczne i roczne, wybór użytkownika oraz potwierdzenie przed przejściem do zakupu. |
| **Reseller Panel** | Lista kont, statusy, daty wygaśnięcia, notatki, paginacja oraz szybkie przejście do pakietu lub M3U. |
| **Linki M3U** | Prawdziwy User Key, TiviMate, Smart IPTV, SS IPTV, EPG, wybór użytkownika, kopiowanie i pobieranie. |
| **Doładowanie konta** | Oryginalny ekran płatności otwierany w WebView — bez własnego formularza kart płatniczych. |
| **Ustawienia** | Motyw jasny/ciemny, dane konta, 2FA, zmiana hasła, informacje o aplikacji i aktualizacjach. |
| **Powiadomienia** | Informacje o nowych wiadomościach PlusX oraz dostępnej nowej wersji aplikacji. |
| **Diagnostyka** | Ręcznie uruchamiany raport podstawowy lub zaawansowany z automatycznym oczyszczaniem danych wrażliwych. |

## Co nowego w wersji 1.5.3

Wersja **1.5.3** skupia się na historii operacji, poprawnym rozpoznawaniu aktywnych pakietów oraz wygodniejszej obsłudze wielu kont.

### Historia zakupów i doładowań

- Dodano zakładkę **Historia zakupów** pobierającą dane z `balance_history.php`.
- Dodano filtrowanie historii po użytkowniku.
- Lista użytkowników jest tworzona na podstawie **Reseller Panelu** oraz konta głównego.
- Naprawiono błędne wykrywanie losowych identyfikatorów operacji jako nazw użytkowników.
- Dodano rozpoznawanie doładowań konta.
- Kolejne doładowania są łączone w jedną, czytelną pozycję.
- Wpis historii może pokazywać:
  - datę,
  - kwotę,
  - użytkownika,
  - prawdopodobny pakiet.
- Kwoty są zaokrąglane do formatu `x.xx EUR`.
- Poprawiono rozpoznawanie pakietów po cenie z uwzględnieniem możliwych rabatów resellerów.
- Historia może uzupełnić pozycję informacjami z Reseller Panelu:
  - notatką,
  - statusem konta,
  - aktywnym pakietem,
  - terminem wygaśnięcia — jeżeli te dane są dostępne.

### Pakiety i status konta

- Naprawiono problem, przez który nieaktywne konto mogło wyglądać jak aktywne w zakładce **Kup pakiet**.
- Dodano blokadę zakupu innego pakietu, kiedy użytkownik ma już aktywny pakiet.

### Ustawienia i wygląd

- Całe kafelki ustawień są teraz klikalne.
- Dodano czytelne oznaczenie, że dany kafelek można otworzyć.
- Usunięto informację o blokadzie po IP z ustawień.
- Zmieniono komunikat przy adresie e-mail — zmiana e-maila wymaga kontaktu z supportem PlusX.
- Poprawiono przełączanie motywu oraz wykrywanie jasnego i ciemnego trybu systemowego.
- Zachowano updater aplikacji oraz zakładkę **O aplikacji**.

<details>
<summary><strong>Co dodano i poprawiono w wersji 1.5.2</strong></summary>

### Dodano

- powiadomienia o nowych wiadomościach PlusX,
- powiadomienia o dostępnej nowej wersji aplikacji,
- zakładkę **O aplikacji** w ustawieniach,
- ręczne sprawdzanie aktualizacji z poziomu aplikacji,
- pobieranie opisu zmian bezpośrednio z GitHuba,
- plik SHA-256 do weryfikacji APK przy wydaniu.

### Poprawiono

- sortowanie w zakładce **Programy na dziś**,
- ukrywanie programów, które już się zakończyły,
- oznaczenie wydarzenia trwającego w danym momencie,
- odświeżanie danych programów z backendu,
- wykrywanie timeoutu sesji, kiedy saldo nie zostanie znalezione,
- automatyczny powrót do logowania po wygaśnięciu sesji,
- komunikat `Zostałeś wylogowany (timeout)`,
- kodowanie UTF-8 i wyświetlanie polskich znaków w README.

</details>

<details>
<summary><strong>Co poprawiono w wersji 1.5.1</strong></summary>

- Poprawiono sortowanie w zakładce **Programy na dziś**.
- Najnowsze i najpóźniejsze wydarzenia są wyświetlane wyżej, a starsze niżej.
- Poprawiono przycisk **Odśwież balans**.
- Saldo jest pobierane na świeżo, bez używania starego cache.
- Dodano wykrywanie wygaśniętej sesji.
- Jeżeli saldo nie zostanie znalezione, aplikacja traktuje to jako timeout sesji.
- Dodano komunikat `Zostałeś wylogowany (timeout)`.
- Po timeout aplikacja czyści sesję i cookies.
- Po timeout użytkownik jest automatycznie przenoszony do ekranu logowania.

</details>

## Jak działa aplikacja od strony technicznej

```text
Użytkownik
   │
   ├── WebView ───────────────► new.plusx.tv
   │                             logowanie, reCAPTCHA, płatności
   │
   ├── CookieManager
   │       │
   │       └── HTTPS ─────────► new.plusx.tv
   │                             saldo, pakiety, reseller, M3U, profil
   │
   └── HTTPS ─────────────────► backend.torvinek.pl
                                 wiadomości, EPG, ręcznie wysłana diagnostyka
```

### Najważniejsze zasady

- Cookies panelu są używane tylko wobec dokładnego hosta `new.plusx.tv`.
- Cookies PlusX nie są wysyłane do backendu Torvinek.
- Aplikacja akceptuje wyłącznie połączenia HTTPS.
- Adresy podobne do właściwej domeny, np. `new.plusx.tv.example.org`, są odrzucane.
- Token backendu jest przekazywany podczas budowania i nie jest publikowany w repozytorium.
- Token klienta mobilnego powinien mieć wyłącznie minimalne, nieadministracyjne uprawnienia.

Więcej informacji: [NETWORK.md](NETWORK.md) oraz [BACKEND_SECURITY.md](BACKEND_SECURITY.md).

## Prywatność i bezpieczeństwo

### Czego aplikacja nie wysyła do backendu Torvinek

- loginu do PlusX,
- hasła,
- cookies sesji,
- danych reCAPTCHA,
- danych płatniczych,
- zawartości formularza karty,
- pełnej sesji panelu.

### Logowanie

Login, hasło oraz reCAPTCHA są obsługiwane na oryginalnej stronie `new.plusx.tv` otwartej w WebView. Aplikacja nie tworzy osobnego formularza logowania imitującego panel.

### Płatności

Płatności pozostają na stronie PlusX. Aplikacja nie implementuje własnego systemu płatności i nie przechwytuje danych karty.

### Uprawnienia Androida

Aplikacja korzysta wyłącznie z uprawnień potrzebnych do działania panelu i własnych powiadomień:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

| Uprawnienie | Do czego jest używane |
|---|---|
| `INTERNET` | Logowanie do panelu PlusX, pobieranie danych z `new.plusx.tv`, wiadomości, EPG, sprawdzanie aktualizacji i ręcznie wysyłana diagnostyka. |
| `POST_NOTIFICATIONS` | Wyświetlanie powiadomień o nowych wiadomościach PlusX oraz dostępnej aktualizacji aplikacji. Na Androidzie 13 i nowszym system prosi o zgodę użytkownika. |

Brak zgody na powiadomienia **nie blokuje korzystania z aplikacji** — wyłączone będą jedynie komunikaty wyświetlane poza aplikacją.

`POST_NOTIFICATIONS` nie daje dostępu do cudzych powiadomień ani ich treści. Pozwala PlusX Mobile wyłącznie wyświetlać własne komunikaty systemowe.

Aplikacja nie żąda dostępu do kontaktów, SMS-ów, mikrofonu, aparatu, lokalizacji ani plików użytkownika.

Szczegóły: [PRIVACY.md](PRIVACY.md) oraz [SECURITY.md](SECURITY.md).

## Diagnostyka

Diagnostyka nie działa w tle i nie jest wysyłana automatycznie przy każdym błędzie. Raport jest wysyłany dopiero po wejściu przez użytkownika do odpowiedniej zakładki, uzupełnieniu formularza i zatwierdzeniu wysyłki.

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
- ustawiony motyw,
- informacje pomocnicze związane z wybraną sekcją aplikacji.

### Diagnostyka zaawansowana

Może dołączyć lokalny snapshot danych powiązanych z wybraną sekcją. Przed wysłaniem aplikacja próbuje automatycznie usunąć między innymi:

- parametry sesji,
- nagłówki Bearer,
- wartości pól wyglądających jak hasła, tokeny lub sekrety,
- adresy IP,
- obce adresy e-mail.

Diagnostyki zaawansowanej nie można uruchomić dla ekranów doładowania ani ustawień konta.

> [!NOTE]
> Automatyczne oczyszczanie ogranicza ryzyko ujawnienia danych wrażliwych, ale przed wysłaniem raportu użytkownik powinien nadal unikać samodzielnego wpisywania haseł, tokenów i pełnych linków M3U w opisie problemu.

## Powiadomienia i aktualizacje

Aplikacja może informować o:

- nowych wiadomościach PlusX,
- dostępnej nowej wersji aplikacji,
- opisie zmian pobranym z GitHuba.

Na Androidzie 13 i nowszym przy pierwszym użyciu funkcji powiadomień pojawi się systemowe pytanie o zgodę. Możesz odmówić — pozostałe funkcje aplikacji nadal będą działać normalnie. Zgodę można później zmienić w ustawieniach systemowych Androida dla aplikacji **PlusX Mobile** lub odinstalować i zainstalować aplikacje oraz zaakceptować powiadomienia.

W zakładce **O aplikacji** można ręcznie sprawdzić aktualizację, zeby zaktualizować aplikacje "automatycznie" to trzeba w ustawieniach w zakladce Zezwól na instalowanie nie znanych aplikacji lub coś w tym stylu wlaczyć mozliwosć instalowania aplikacji czyli aktualizacji dla aplikacji 'PlusX Mobile'. Lub zaktualizować recznie, oficjalne wydania są publikowane w sekcji [Releases](https://github.com/Torvinek/PlusX-Mobile/releases).

## Instalacja

### Wymagania

- Android 9 lub nowszy,
- konto w panelu [PlusX](https://new.plusx.tv/login.php),
- połączenie z internetem.

### Kroki

1. Otwórz [najnowsze wydanie](https://github.com/Torvinek/PlusX-Mobile/releases/latest).
2. Pobierz plik APK z sekcji **Assets**.
3. Otwórz pobrany plik na telefonie.
4. Zezwól przeglądarce lub menedżerowi plików na instalację aplikacji z tego źródła.
5. Zainstaluj aplikację.
6. Uruchom PlusX Mobile i zaloguj się na oryginalnej stronie panelu.

> [!TIP]
> Ostrzeżenie o aplikacji instalowanej spoza Google Play nie oznacza automatycznie wykrycia wirusa. Android może wyświetlać takie ostrzeżenie w przypadku nowych lub mało znanych aplikacji APK instalowanych ręcznie.

## Weryfikacja pobranego APK

Każde oficjalne wydanie może zawierać plik z sumą SHA-256. Pozwala to sprawdzić, czy APK nie zostało uszkodzone lub podmienione po pobraniu.

### Windows PowerShell

```powershell
Get-FileHash -Algorithm SHA256 .\PlusX-Mobile-v1.5.3.apk
```

Porównaj wynik z plikiem `.sha256` dodanym do tego samego wydania.

### Linux

```bash
sha256sum PlusX-Mobile-v1.5.3.apk
```

## Budowanie ze źródeł

Kod projektu jest publicznie widoczny w celu przejrzystości, audytu, nauki i lokalnego testowania. Projekt jest **source-available**, a nie open source w znaczeniu licencji OSI.

### Wymagania techniczne

- JDK 17,
- Android SDK 35,
- Gradle Wrapper dołączony do repozytorium,
- internet podczas pierwszego pobierania zależności.

### Build debug

```bash
./gradlew test assembleDebug
```

Na Windowsie:

```powershell
.\gradlew.bat test assembleDebug
```

Wynik:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Funkcje korzystające z prywatnego backendu wymagają lokalnej konfiguracji tokenu aplikacyjnego. Szczegóły znajdują się w [BUILDING.md](BUILDING.md).

## Technologie

| Element | Wartość |
|---|---|
| Język | Kotlin |
| Minimalny Android | Android 9 / API 28 |
| Target / Compile SDK | 35 |
| JVM | 17 |
| Namespace | `pl.torvinek.plusxmobile` |
| Interfejs | Natywny Android + WebView w miejscach wymagających oryginalnego portalu |
| Źródło danych panelu | HTML pobierany z `new.plusx.tv` i przetwarzany lokalnie |
| Backend pomocniczy | `backend.torvinek.pl` — wiadomości, EPG, diagnostyka |
| CI/CD | GitHub Actions |
| Dystrybucja | Podpisane APK w GitHub Releases |

## Struktura projektu

```text
app/
├── src/main/java/pl/torvinek/plusxmobile/
│   ├── MainActivity.kt              # UI, routing i główna logika
│   ├── M3uHtmlParser.kt             # parser M3U, User Key i EPG
│   ├── ResellerHtmlParser.kt        # parser kont reseller
│   ├── ResellerPaginator.kt         # paginacja i scalanie kont
│   ├── ServerMessagesParser.kt      # parser wiadomości
│   ├── DiagnosticSanitizer.kt       # oczyszczanie diagnostyki
│   ├── NetworkPolicy.kt             # kontrola hostów i HTTPS
│   └── AppConfig.kt                 # konfiguracja adresów usług
├── src/main/res/                    # ikony, logo i zasoby Androida
└── src/test/                        # testy parserów i polityki sieciowej

.github/workflows/
├── android-ci.yml                   # testy i build debug
└── release.yml                      # podpisany release i SHA-256
```

## Ograniczenia

- PlusX nie udostępnia oficjalnego API wykorzystywanego przez ten projekt.
- Część danych jest pobierana przez parsowanie HTML.
- Zmiana struktury strony może tymczasowo zepsuć parser konkretnej zakładki.
- Logowanie, reCAPTCHA, płatności, 2FA i zmiana hasła pozostają w WebView.
- Niektóre funkcje zależą od dostępności `new.plusx.tv` lub backendu Torvinek.
- Aplikacja nie gwarantuje działania z każdym typem konta lub każdą przyszłą wersją panelu.

## Zgłaszanie problemów

Najlepsza droga zgłoszenia błędu:

1. Wejdź w aplikacji w **Ustawienia → Diagnostyka**.
2. Wybierz diagnostykę podstawową albo zaawansowaną.
3. Opisz krótko, co nie działa i na jakim ekranie pojawił się problem.
4. Wyślij raport.

Możesz również utworzyć zgłoszenie w zakładce [Issues](https://github.com/Torvinek/PlusX-Mobile/issues), lub w aplikacji **PlusX Mobile** po zalogowaniu w zakladce 'Ustawienia --> Diagnostyka' ale **nie publikuj** tam:

- loginu,
- hasła,
- cookies,
- User Key,
- pełnych linków M3U,
- tokenów,
- danych płatniczych.

## Dokumentacja

- [PRIVACY.md](PRIVACY.md) — prywatność i diagnostyka,
- [SECURITY.md](SECURITY.md) — zgłaszanie problemów bezpieczeństwa,
- [NETWORK.md](NETWORK.md) — połączenia sieciowe,
- [BACKEND_SECURITY.md](BACKEND_SECURITY.md) — zabezpieczenia backendu,
- [BUILDING.md](BUILDING.md) — lokalna konfiguracja i budowanie,
- [VALIDATION.md](VALIDATION.md) — walidacja repozytorium i buildów,
- [CONTRIBUTING.md](CONTRIBUTING.md) — zasady przesyłania zmian.

## Licencja

Główna część projektu jest udostępniana na warunkach [PlusX Mobile Source-Available License](LICENSE).

Kod można przeglądać, analizować i lokalnie budować w granicach opisanych w licencji. Bez pisemnej zgody autora zabronione jest między innymi:

- kopiowanie projektu do innych repozytoriów,
- tworzenie i publikowanie mirrorów,
- Edycja w celu finansowych, poprawy funkcji, wgrania malware'ru uzytkownikowi,
- redystrybucja kodu lub APK,
- publikowanie własnych wersji aplikacji,
- użycie komercyjne,
- podszywanie się pod oficjalne wydanie.

Plik [LICENSE-MIT](LICENSE-MIT) ma zastosowanie wyłącznie do elementów, które zostały wyraźnie oznaczone jako objęte licencją MIT. Nie zastępuje on głównej licencji całego projektu.

## Autor

**Torvinek**  
GitHub: [@Torvinek](https://github.com/Torvinek)\n
Telegram: [@marclxz](https://t.me/marclxz)\n
Email: [prywatny.marcelo@gmail.com](mailto:prywatny.marcelo@gmail.com)\n

<p align="center">
  <strong>© Torvinek 2026</strong><br>
  PlusX Mobile — prywatny, niezależny projekt mobilnego klienta panelu PlusX.
</p>
