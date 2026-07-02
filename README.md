# PlusX Mobile - krotki opis

`PlusX Mobile` to prywatna aplikacja Android do wygodnego korzystania z panelu `new.plusx.tv` na telefonie.

Nie bylo oficjalnego API, kodu backendu ani dostepu do bazy danych. Dlatego aplikacja dziala jako mobilna nakladka:

- logowanie odbywa sie przez oryginalny WebView,
- reCAPTCHA przechodzi uzytkownik,
- po zalogowaniu aplikacja uzywa cookies,
- pobiera HTML z panelu,
- parsuje dane,
- pokazuje je w ladnym mobilnym UI.

## Co zostalo zrobione

- dashboard z saldem,
- duze logo PlusX w naglowku,
- tryb jasny i ciemny,
- wiadomosci z panelu i Telegrama,
- zakladka `Programy na dzis` z wydarzeniami EPG,
- pakiety z cenami miesiac/rok,
- wybor uzytkownika w pakietach,
- potwierdzenie przed zakupem pakietu,
- doladowanie konta przez oryginalny portal,
- Reseller Panel z kontami, statusem i data wygasniecia,
- Linki M3U z prawdziwym User Key,
- dropdown uzytkownika w M3U,
- loga dla TiviMate, Smart IPTV, SS IPTV i EPG,
- animacje klikniec i przejsc,
- ekran `Proszę czekać...`,
- poprawione cofanie,
- stopka `© Torvinek 2026`.

## Backend

Aplikacja korzysta z prywatnego backendu Telegram:

```text
https://backend.torvinek.pl
```

Endpointy:

```text
/telegram/plusx/status
/telegram/plusx/wiadomosci
/telegram/plusx/epgevent
/telegram/plusx/diagnostics
/panel
```

Backend pobiera wiadomosci i dzisiejsze wydarzenia EPG z Telegrama. Przyjmuje tez diagnostyke z aplikacji i pokazuje ja w prywatnym panelu:

```text
https://backend.torvinek.pl/panel
```

Panel ma logowanie i pozwala oznaczac zgloszenia jako zalatwione albo niezalatwione.

## APK

Projekt:

```text
D:\PlusX Mobile\PlusXMobile
```

Aktualny debug APK:

```text
D:\PlusX Mobile\PlusXMobile-debug.apk
```

Aktualny release APK:

```text
D:\PlusX Mobile\PlusXMobile-release.apk
```

## Uwaga

Aplikacja jest prywatna. Poniewaz bazuje na parsowaniu HTML, moze wymagac poprawek, jezeli `new.plusx.tv` zmieni uklad strony.
