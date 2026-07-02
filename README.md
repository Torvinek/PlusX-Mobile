# PlusX Mobile

`PlusX Mobile` to nieoficjalna aplikacja Android przygotowana jako wygodniejsza, mobilna nakladka na panel PlusX.

Aplikacja powstala dlatego, ze oryginalny panel jest wygodny glownie na komputerze, a na telefonie korzystanie z niego jest malo komfortowe.

## Co potrafi aplikacja

- logowanie przez oryginalny panel w WebView,
- obsluga reCAPTCHA bez obchodzenia zabezpieczen,
- mobilny ekran glowny z saldem,
- tryb jasny i ciemny,
- wiadomosci i komunikaty w czytelnym widoku,
- lista programow na dzis,
- podglad pakietow,
- wybor uzytkownika przy pakietach,
- potwierdzenie przed zakupem pakietu,
- panel resellera,
- linki M3U,
- kopiowanie i pobieranie linkow,
- diagnostyka do pomocy technicznej,
- animacje i wygodniejsza nawigacja na telefonie.

## Prywatnosc i bezpieczenstwo

Aplikacja nie zbiera danych uzytkownika w celach analitycznych, reklamowych ani marketingowych.

Aplikacja nie korzysta z trackerow, reklam ani zewnetrznych systemow analitycznych.

Logowanie odbywa sie przez oryginalna strone w WebView. Haslo i reCAPTCHA nie sa obslugiwane przez osobny, zewnetrzny formularz aplikacji.

Dane diagnostyczne sa wysylane tylko wtedy, gdy uzytkownik sam wybierze taka opcje w aplikacji. Diagnostyka sluzy do pomocy technicznej i zawiera informacje potrzebne do znalezienia problemu, np. wersje aplikacji, model telefonu, ekran, z ktorym jest problem, oraz opcjonalny opis od uzytkownika.

Diagnostyka nie jest narzedziem do sledzenia uzytkownika.

## Dlaczego kod zrodlowy nie jest publiczny

Projekt nie zostaje udostepniony jako open source, poniewaz zawiera integracje z prywatnym backendem, prywatnym panelem oraz mechanizmy komunikacji wymagajace ochrony przed naduzyciami.

Publiczne udostepnienie kodu mogloby:

- ulatwic naduzycia wobec prywatnego backendu,
- ujawnic szczegoly integracji,
- zwiekszyc ryzyko obchodzenia limitow i zabezpieczen,
- utrudnic utrzymanie aplikacji jako prywatnego narzedzia.

Z tego powodu repozytorium zawiera tylko gotowy plik APK oraz publiczny opis aplikacji. Brak kodu zrodlowego nie oznacza, ze aplikacja zbiera dane. Oznacza tylko, ze backend i integracje nie sa wystawiane publicznie.

## Instalacja

Pobierz plik APK z sekcji `Releases` i zainstaluj go na Androidzie.

Android moze pokazac ostrzezenie, poniewaz aplikacja jest instalowana spoza Google Play. To normalne przy prywatnych aplikacjach APK.

## Status

Aplikacja jest prywatnym projektem i moze wymagac aktualizacji, jezeli oryginalny panel zmieni swoj wyglad albo strukture HTML.

## Autor

© Torvinek 2026
