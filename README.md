<p align="center">
  <img src="assets/plusx-banner.png" width="820" alt="PlusX Mobile">
</p>

<h1 align="center">PlusX Mobile</h1>

<p align="center">
  <strong>Nieoficjalna aplikacja Android z mobilnym interfejsem dla panelu PlusX.</strong>
</p>

<p align="center">
  <a href="https://github.com/Torvinek/PlusX-Mobile/releases/latest">
    <img src="https://img.shields.io/github/v/release/Torvinek/PlusX-Mobile?style=for-the-badge&label=Najnowsza%20wersja" alt="Najnowsza wersja">
  </a>
  <a href="https://github.com/Torvinek/PlusX-Mobile/releases">
    <img src="https://img.shields.io/github/downloads/Torvinek/PlusX-Mobile/total?style=for-the-badge&label=Pobrania" alt="Liczba pobrań">
  </a>
  <img src="https://img.shields.io/badge/Android-9%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android 9+">
</p>

<p align="center">
  <a href="https://github.com/Torvinek/PlusX-Mobile/releases/latest">
    <img src="https://img.shields.io/badge/Pobierz-najnowsze%20APK-009FE3?style=for-the-badge&logo=android&logoColor=white" alt="Pobierz APK">
  </a>
</p>

> [!IMPORTANT]
> PlusX Mobile jest projektem nieoficjalnym i nie jest powiązany z właścicielem panelu PlusX. Do działania wymaga aktywnego konta w obsługiwanym panelu.

## O aplikacji

Oryginalny panel PlusX został przygotowany głównie z myślą o komputerach. **PlusX Mobile** porządkuje najważniejsze funkcje panelu i prezentuje je w wygodnym, natywnym interfejsie dopasowanym do telefonu.

Aplikacja nie zastępuje oryginalnego systemu logowania ani płatności. Logowanie, reCAPTCHA i operacje wymagające dodatkowego potwierdzenia nadal odbywają się przez oryginalny portal.

## Najważniejsze funkcje

| Panel i konto | Pakiety i multimedia |
|---|---|
| Natywny ekran główny z saldem | Przegląd dostępnych pakietów |
| Wiadomości i komunikaty | Wybór użytkownika dla pakietów i M3U |
| Programy na dziś | Linki TiviMate, Smart IPTV i SS IPTV |
| Panel resellera | Kopiowanie i pobieranie list M3U |
| Ustawienia konta | Dostęp do EPG |
| Ręczne odświeżanie danych | Potwierdzenie przed rozpoczęciem zakupu |

### Wygoda użytkowania

- automatyczny motyw jasny i ciemny,
- czytelne karty i nawigacja mobilna,
- wyszukiwane listy użytkowników,
- animacje przejść i stany ładowania,
- poprawiona obsługa przycisku Wstecz,
- diagnostyka uruchamiana wyłącznie przez użytkownika.

## Instalacja

1. Otwórz stronę [Najnowsze wydanie](https://github.com/Torvinek/PlusX-Mobile/releases/latest).
2. Pobierz plik z rozszerzeniem `.apk`.
3. Zezwól przeglądarce lub menedżerowi plików na instalowanie aplikacji z tego źródła.
4. Uruchom instalator APK i potwierdź instalację.

> [!NOTE]
> Android lub Google Play Protect może wyświetlić ostrzeżenie, ponieważ aplikacja jest instalowana spoza Google Play. Jest to standardowe zachowanie dla prywatnie dystrybuowanych plików APK.

## Wymagania

- Android 9 lub nowszy,
- aktywne konto w panelu PlusX,
- połączenie z internetem,
- WebView / Google Chrome dostępny w systemie.

## Jak to działa

```text
Oryginalne logowanie w WebView
            ↓
     aktywna sesja i cookies
            ↓
 pobieranie wybranych stron panelu
            ↓
      odczyt danych z HTML
            ↓
  natywny interfejs PlusX Mobile
```

Panel nie udostępnia oficjalnego API dla tej aplikacji. Z tego powodu część danych jest odczytywana ze stron panelu. Jeżeli właściciel panelu zmieni strukturę HTML, wybrane funkcje mogą wymagać aktualizacji aplikacji.

## Prywatność i bezpieczeństwo

- brak reklam,
- brak trackerów marketingowych,
- brak zewnętrznych systemów analitycznych,
- hasło i reCAPTCHA są obsługiwane przez oryginalną stronę logowania,
- płatności oraz końcowe potwierdzenie zakupu pozostają w oryginalnym portalu,
- diagnostyka jest wysyłana tylko po świadomym uruchomieniu jej przez użytkownika.

Przy zgłaszaniu problemu **nie publikuj** loginu, hasła, User Key, plików cookies, pełnych linków M3U ani danych płatniczych.

## Dlaczego kod źródłowy nie jest publiczny

Repozytorium służy do publikowania wydań APK oraz dokumentacji użytkowej. Kod źródłowy pozostaje prywatny, ponieważ projekt zawiera integracje z prywatnym panelem i backendem oraz mechanizmy, których publiczne udostępnienie mogłoby ułatwić nadużycia.

Brak publicznego kodu źródłowego nie oznacza automatycznego zbierania danych. Zakres działania aplikacji opisano w sekcji [Prywatność i bezpieczeństwo](#prywatność-i-bezpieczeństwo).

## Zgłaszanie problemów

Przed utworzeniem zgłoszenia sprawdź, czy używasz najnowszej wersji aplikacji. W zgłoszeniu podaj:

- wersję PlusX Mobile,
- model telefonu i wersję Androida,
- ekran, na którym wystąpił problem,
- kroki pozwalające odtworzyć błąd,
- zrzut ekranu bez danych wrażliwych.

[Utwórz zgłoszenie błędu](https://github.com/Torvinek/PlusX-Mobile/issues/new?template=bug_report.yml)

## Najczęstsze pytania

<details>
<summary><strong>Czy jest to oficjalna aplikacja PlusX?</strong></summary>

Nie. PlusX Mobile jest niezależnym, nieoficjalnym projektem.

</details>

<details>
<summary><strong>Czy aplikacja omija reCAPTCHA albo zabezpieczenia logowania?</strong></summary>

Nie. Logowanie odbywa się przez oryginalny panel w WebView, a reCAPTCHA użytkownik przechodzi ręcznie.

</details>

<details>
<summary><strong>Dlaczego część funkcji może przestać działać po zmianach panelu?</strong></summary>

Aplikacja korzysta z danych dostępnych w HTML panelu. Zmiana układu lub nazw elementów strony może wymagać aktualizacji parserów.

</details>

<details>
<summary><strong>Gdzie pobrać bezpieczną, aktualną wersję?</strong></summary>

Wyłącznie z sekcji [Releases](https://github.com/Torvinek/PlusX-Mobile/releases) tego repozytorium.

</details>

## Status projektu

Projekt jest aktywnie rozwijany. Aktualizacje pojawiają się, gdy dodawane są nowe funkcje albo zmiany w oryginalnym panelu wymagają dostosowania aplikacji.

---

<p align="center">
  <strong>© Torvinek 2026</strong><br>
  Nieoficjalny projekt społecznościowy dla systemu Android.
</p>
