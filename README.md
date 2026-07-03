<p align="center">
  <img src="assets/plusx-logo.png" alt="PlusX.tv" width="380">
</p>

<h1 align="center">PlusX Mobile</h1>

<p align="center">
  Nieoficjalna aplikacja Android z natywnym, mobilnym interfejsem dla panelu PlusX.
</p>

<p align="center">
  <a href="https://github.com/Torvinek/PlusX-Mobile/actions/workflows/android-ci.yml"><img alt="Android CI" src="https://github.com/Torvinek/PlusX-Mobile/actions/workflows/android-ci.yml/badge.svg?branch=main"></a>
  <a href="https://github.com/Torvinek/PlusX-Mobile/releases/latest"><img alt="Najnowsza wersja" src="https://img.shields.io/github/v/release/Torvinek/PlusX-Mobile?display_name=tag&label=wersja&color=0ea5e9"></a>
  <img alt="Android 9+" src="https://img.shields.io/badge/Android-9%2B-3DDC84">
  <img alt="Kotlin 2.0.21" src="https://img.shields.io/badge/Kotlin-2.0.21-7F52FF">
  <a href="LICENSE"><img alt="Licencja Source-Available" src="https://img.shields.io/badge/licencja-source--available-c2410c"></a>
</p>

<p align="center">
  <strong><a href="https://github.com/Torvinek/PlusX-Mobile/releases/latest">Pobierz najnowsze APK</a></strong>
  · <a href="#najważniejsze-funkcje">Funkcje</a>
  · <a href="#prywatność-i-bezpieczeństwo">Prywatność</a>
  · <a href="#instalacja">Instalacja</a>
  · <a href="#samodzielne-budowanie">Budowanie</a>
</p>

---

> [!IMPORTANT]
> **PlusX Mobile jest projektem niezależnym i nieoficjalnym.** Nie jest własnością operatora panelu PlusX ani oficjalną aplikacją właściciela usługi. Do działania wymagane jest własne, aktywne konto w obsługiwanym panelu.

## O aplikacji

Oryginalny panel PlusX został zaprojektowany głównie z myślą o komputerach. PlusX Mobile porządkuje jego najważniejsze funkcje i przedstawia je w natywnym interfejsie dopasowanym do telefonu.

Logowanie, reCAPTCHA, płatności i końcowe potwierdzenie zakupu nadal odbywają się przez oryginalny portal. Aplikacja nie omija zabezpieczeń i nie zastępuje oficjalnego systemu logowania.

## Co nowego w PlusX Mobile 1.5.3

- dodano **Historię zakupów** pobieraną z `balance_history.php`,
- dodano filtrowanie historii według konta głównego oraz użytkowników z panelu Resellera,
- naprawiono rozpoznawanie identyfikatorów operacji, aby nie były błędnie traktowane jako użytkownicy,
- dodano wykrywanie doładowań konta i łączenie kolejnych doładowań w jedną czytelną pozycję,
- historia pokazuje datę, kwotę, użytkownika i prawdopodobnie zakupiony pakiet,
- kwoty są formatowane jako `x.xx EUR`,
- poprawiono rozpoznawanie pakietów na podstawie ceny, również przy możliwych rabatach resellera,
- rozszerzono informacje z panelu Resellera o notatkę, status konta, aktywny pakiet i termin wygaśnięcia,
- poprawiono sytuację, w której nieaktywne konto mogło wyglądać jak aktywne w zakładce **Kup pakiet**,
- zablokowano zakup kolejnego pakietu, gdy użytkownik ma już aktywny pakiet,
- poprawiono ekran ustawień: całe kafelki są klikalne i mają czytelne oznaczenie otwierania,
- usunięto z ustawień informację o blokadzie po IP,
- doprecyzowano komunikat dotyczący zmiany adresu e-mail,
- poprawiono przełączanie motywu i wykrywanie jasnego/ciemnego trybu systemowego,
- zachowano wbudowany mechanizm aktualizacji oraz zakładkę **O aplikacji**.

## Najważniejsze funkcje

| Obszar | Możliwości |
|---|---|
| **Konto i dashboard** | saldo, wiadomości, szybka nawigacja, jasny i ciemny motyw |
| **Historia zakupów** | transakcje, doładowania, filtrowanie po użytkowniku i rozpoznawanie pakietów |
| **Pakiety** | lista pakietów, wybór użytkownika, informacje o aktywnym pakiecie i potwierdzenie zakupu |
| **Reseller Panel** | konta, notatki, status, data wygaśnięcia, zakup pakietu i przejście do M3U |
| **Linki M3U** | wybór użytkownika, User Key, TiviMate, Smart IPTV, SS IPTV oraz EPG |
| **Programy na dziś** | wydarzenia EPG pobierane z backendu aplikacji |
| **Wiadomości** | komunikaty z panelu PlusX oraz backendu Torvinek z usuwaniem duplikatów |
| **Ustawienia** | profil, motyw, zmiana hasła, 2FA, diagnostyka i informacje o aplikacji |
| **Aktualizacje** | sprawdzanie nowych wydań i przejście do najnowszego APK |

## Jak działa aplikacja

```text
Użytkownik
   │
   ├── logowanie i reCAPTCHA ──> new.plusx.tv (WebView)
   │
   ├── cookies sesji ──────────> wyłącznie new.plusx.tv
   │
   ├── HTML panelu ────────────> lokalne parsery ──> natywny interfejs Android
   │
   └── wiadomości / EPG / diagnostyka ──> backend.torvinek.pl
```

Aplikacja nie posiada oficjalnego API panelu. Po zalogowaniu pobiera wybrane podstrony, odczytuje potrzebne informacje z HTML i prezentuje je w mobilnym układzie. Zmiana struktury panelu może wymagać aktualizacji parserów.

## Prywatność i bezpieczeństwo

Aplikacja deklaruje wyłącznie uprawnienie:

```text
android.permission.INTERNET
```

### `new.plusx.tv`

Służy do logowania, obsługi sesji, pobierania danych panelu, zakupu pakietów i płatności. Cookies sesji mogą zostać wysłane wyłącznie do dokładnego hosta `new.plusx.tv` przez połączenie HTTPS.

### `backend.torvinek.pl`

Służy wyłącznie do:

- pobierania wiadomości,
- pobierania wydarzeń EPG,
- wysyłania diagnostyki zatwierdzonej przez użytkownika.

Do backendu Torvinek nie są dołączane cookies panelu PlusX. Diagnostyka jest uruchamiana ręcznie z poziomu aplikacji i przed wysłaniem podlega oczyszczaniu z danych wrażliwych.

Płatności są obsługiwane przez oryginalny portal w WebView. Aplikacja nie zawiera własnego formularza karty i nie przesyła danych płatniczych do backendu Torvinek.

Szczegóły:

- [Polityka prywatności](PRIVACY.md)
- [Połączenia sieciowe](NETWORK.md)
- [Bezpieczeństwo projektu](SECURITY.md)
- [Bezpieczeństwo backendu](BACKEND_SECURITY.md)

## Instalacja

1. Otwórz [najnowsze wydanie](https://github.com/Torvinek/PlusX-Mobile/releases/latest).
2. Pobierz plik `PlusX-Mobile-vX.Y.Z.apk`.
3. Zezwól przeglądarce lub menedżerowi plików na instalowanie aplikacji z tego źródła.
4. Zainstaluj APK i uruchom PlusX Mobile.
5. Zaloguj się bezpośrednio na stronie PlusX wyświetlonej w aplikacji.

Ostrzeżenie o aplikacji spoza Google Play nie oznacza automatycznie wykrycia złośliwego kodu. PlusX Mobile jest dystrybuowany jako podpisany plik APK przez GitHub Releases.

## Weryfikacja pobranego APK

Do oficjalnego wydania dołączany jest plik z sumą SHA-256:

```text
PlusX-Mobile-vX.Y.Z.apk.sha256
```

Weryfikacja w PowerShellu:

```powershell
Get-FileHash -Algorithm SHA256 .\PlusX-Mobile-vX.Y.Z.apk
```

Otrzymany skrót powinien być identyczny z wartością opublikowaną przy tym samym wydaniu.

## Samodzielne budowanie

Wymagania:

- JDK 17,
- Android SDK 35,
- Android Build Tools 35,
- połączenie z internetem przy pierwszym pobieraniu zależności.

Podstawowy build:

```bash
./gradlew test assembleDebug
```

Debug APK zostanie zapisany w:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Funkcje zależne od prywatnego backendu wymagają własnej, uprawnionej konfiguracji. Sekrety oficjalnego wydania są przekazywane przez GitHub Actions Secrets i nie znajdują się w repozytorium.

Pełna instrukcja: [BUILDING.md](BUILDING.md)

## Zgłaszanie problemów

Najwygodniej skorzystać z diagnostyki dostępnej w aplikacji:

```text
Ustawienia → Diagnostyka
```

Możesz również utworzyć [zgłoszenie na GitHubie](https://github.com/Torvinek/PlusX-Mobile/issues/new/choose).

> [!CAUTION]
> W zgłoszeniach nie publikuj loginu, hasła, cookies, User Key, pełnych linków M3U, tokenów, danych płatniczych ani prywatnych danych klientów.

Problemy związane z bezpieczeństwem zgłaszaj zgodnie z plikiem [SECURITY.md](SECURITY.md).

## Dokumentacja projektu

| Dokument | Zawartość |
|---|---|
| [BUILDING.md](BUILDING.md) | lokalne budowanie aplikacji |
| [PRIVACY.md](PRIVACY.md) | dane przetwarzane przez aplikację |
| [NETWORK.md](NETWORK.md) | dozwolone hosty i rodzaje połączeń |
| [SECURITY.md](SECURITY.md) | zgłaszanie problemów bezpieczeństwa |
| [BACKEND_SECURITY.md](BACKEND_SECURITY.md) | model ochrony backendu i tokenu aplikacyjnego |
| [VALIDATION.md](VALIDATION.md) | zakres walidacji projektu |
| [CONTRIBUTING.md](CONTRIBUTING.md) | zasady przesyłania zmian |

## Informacje techniczne

| Parametr | Wartość |
|---|---|
| Nazwa | PlusX Mobile |
| Namespace / applicationId | `pl.torvinek.plusxmobile` |
| Język | Kotlin |
| Minimalny Android | Android 9 / API 28 |
| Target / Compile SDK | API 35 |
| JDK / JVM | 17 |
| Interfejs | natywny Android + WebView dla logowania i operacji portalu |
| Dystrybucja | podpisane APK przez GitHub Releases |

## Licencja

Kod źródłowy jest publicznie widoczny **w celu przejrzystości, prywatnego audytu bezpieczeństwa i lokalnych testów**, ale projekt nie jest udostępniany na zasadach Open Source.

Całość materiałów należących do autora podlega licencji [PlusX Mobile Source-Available License](LICENSE). Bez wcześniejszej pisemnej zgody zabronione jest między innymi:

- kopiowanie lub publikowanie kodu w innym repozytorium,
- tworzenie publicznych mirrorów i niezależnych dystrybucji,
- rozpowszechnianie własnych lub zmodyfikowanych plików APK,
- tworzenie aplikacji pochodnych,
- wykorzystywanie projektu komercyjnie,
- podszywanie się pod oficjalne wydanie PlusX Mobile.

Plik [LICENSE-MIT](LICENSE-MIT) nie zmienia licencji całego projektu i może dotyczyć wyłącznie materiałów wyraźnie oznaczonych jako udostępnione na warunkach MIT.

## Autor i zastrzeżenia

Projekt rozwijany przez **Torvinek**.

PlusX Mobile nie jest powiązany z operatorem panelu PlusX. Nazwy i znaki należą do ich odpowiednich właścicieli. Użytkownik odpowiada za przestrzeganie regulaminu usługi oraz za bezpieczeństwo własnego konta.

<p align="center">
  <strong>© Torvinek 2026 · Wszelkie prawa zastrzeżone</strong>
</p>
