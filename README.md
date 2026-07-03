<p align="center">
  <img src="https://raw.githubusercontent.com/Torvinek/PlusX-Mobile/main/assets/plusx-banner.png" alt="PlusX Mobile" width="720">
</p>

<h1 align="center">PlusX Mobile</h1>

<p align="center">
  Nieoficjalna aplikacja Android z wygodnym, mobilnym interfejsem dla panelu PlusX.
</p>

<p align="center">
  <a href="https://github.com/Torvinek/PlusX-Mobile/releases/latest"><img alt="Release" src="https://img.shields.io/github/v/release/Torvinek/PlusX-Mobile?label=wersja"></a>
  <img alt="Android" src="https://img.shields.io/badge/Android-9%2B-3DDC84">
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.0.21-7F52FF">
  <img alt="Status" src="https://img.shields.io/badge/status-prywatny%20projekt-0EA5E9">
  <img alt="Kod" src="https://img.shields.io/badge/kod-niepubliczny-red">
</p>

<p align="center">
  <a href="https://github.com/Torvinek/PlusX-Mobile/releases/latest">Pobierz najnowsze APK</a> -
  <a href="#prywatność">Prywatność</a> -
  <a href="#bezpieczeństwo">Bezpieczeństwo</a> -
  <a href="#instalacja">Instalacja</a>
</p>

---

## Ważne

PlusX Mobile jest projektem niezależnym i nieoficjalnym. Nie jest własnością operatora panelu PlusX ani oficjalną aplikacją właściciela usługi. Do działania wymagane jest własne, aktywne konto w obsługiwanym panelu.

## Czym jest PlusX Mobile

PlusX Mobile powstał jako wygodniejszy panel na telefon. Oryginalna strona działa głównie jak panel desktopowy, dlatego aplikacja pokazuje najważniejsze funkcje w natywnym, mobilnym układzie.

Aplikacja korzysta z oryginalnego logowania przez WebView. Po zalogowaniu prezentuje dane w czytelniejszych ekranach, bez zmieniania zasad działania samego panelu.

## Najważniejsze funkcje

- logowanie przez oryginalny panel,
- obsługa reCAPTCHA bez obchodzenia zabezpieczeń,
- mobilny ekran główny z saldem,
- wiadomości z panelu i komunikaty z backendu,
- zakładka `Programy na dziś`,
- podgląd pakietów,
- wybór użytkownika przy pakietach,
- potwierdzenie przed zakupem pakietu,
- panel resellera,
- linki M3U z wyborem użytkownika,
- kopiowanie i pobieranie linków,
- tryb jasny i ciemny,
- diagnostyka do pomocy technicznej,
- powiadomienia o nowych wiadomościach i nowych wersjach aplikacji.

## Prywatność

Aplikacja nie zbiera danych użytkownika w celach analitycznych, reklamowych ani marketingowych.

Aplikacja nie zawiera reklam, trackerów ani zewnętrznych systemów analitycznych.

Logowanie odbywa się przez oryginalną stronę w WebView. Hasło i reCAPTCHA nie są obsługiwane przez osobny formularz aplikacji.

Dane diagnostyczne są wysyłane tylko wtedy, gdy użytkownik sam wybierze taką opcję w aplikacji. Diagnostyka służy do pomocy technicznej i zawiera tylko informacje potrzebne do znalezienia problemu, na przykład wersję aplikacji, model telefonu, ekran z problemem oraz opcjonalny opis wpisany przez użytkownika.

## Bezpieczeństwo

Projekt nie jest publikowany jako open source, ponieważ zawiera integracje z prywatnym backendem oraz mechanizmy komunikacji, które muszą być chronione przed nadużyciami.

Publiczne udostępnienie kodu mogłoby:

- ujawnić szczegóły prywatnej integracji,
- ułatwić nadużycia wobec backendu,
- zwiększyć ryzyko obchodzenia zabezpieczeń,
- utrudnić utrzymanie aplikacji jako prywatnego narzędzia.

Brak publicznego kodu źródłowego nie oznacza, że aplikacja zbiera dane. Oznacza tylko, że prywatny backend i szczegóły integracji nie są wystawiane publicznie.

## Instalacja

1. Wejdź w zakładkę `Releases`.
2. Pobierz najnowszy plik APK.
3. Zainstaluj aplikację na Androidzie.
4. Jeżeli Android pokaże ostrzeżenie o instalacji spoza Google Play, jest to normalne dla prywatnych aplikacji APK.

## Weryfikacja pliku

Przy każdym wydaniu na GitHubie znajduje się osobny plik `.sha256.txt` z sumą kontrolną APK.

Dzięki temu można sprawdzić, czy pobrany plik APK nie został uszkodzony ani podmieniony.

Przykład na Windows PowerShell:

```powershell
Get-FileHash -Algorithm SHA256 .\PlusXMobile-ReleasevX.Y.Z.apk
```

Wynik powinien zgadzać się z plikiem `.sha256.txt` dodanym do tego samego wydania.

## Aktualizacje

Aplikacja sama sprawdza najnowszą wersję z GitHuba i może pokazać informację o dostępnej aktualizacji. Opis zmian jest pobierany z aktualnego wydania, więc README nie musi być przepisywany po każdej drobnej poprawce.

## Zgłaszanie problemów

W aplikacji znajduje się diagnostyka. Użytkownik może wysłać podstawowe lub rozszerzone dane diagnostyczne, jeżeli coś nie działa poprawnie.

Diagnostyka nie obejmuje ekranów doładowania ani ustawień.

## Licencja

Kod aplikacji nie jest publicznie udostępniany. Plik APK i opis projektu są publikowane wyłącznie do prywatnego użytku użytkowników, którzy mają dostęp do obsługiwanego panelu.

© Torvinek 2026