# Prywatność

## Logowanie i sesja PlusX

Logowanie odbywa się bezpośrednio na `https://new.plusx.tv` w WebView. Aplikacja nie wysyła loginu, hasła ani cookies sesji do backendu Torvinek.

Cookies są pobierane z Android `CookieManager` i używane wyłącznie w żądaniach HTTPS do dokładnego hosta `new.plusx.tv`. Sprawdzenie hosta nie opiera się na prostym `startsWith`, dlatego adresy podobne do `new.plusx.tv.example.org` są odrzucane.

## Backend Torvinek

Aplikacja łączy się z `https://backend.torvinek.pl` w celu:

- pobrania wiadomości,
- pobrania wydarzeń EPG,
- wysłania diagnostyki po zatwierdzeniu przez użytkownika.

Do tych żądań nie są dołączane cookies panelu PlusX.

## Diagnostyka podstawowa

Po ręcznym otwarciu diagnostyki i zatwierdzeniu formularza raport może zawierać:

- email kontaktowy wpisany w formularzu,
- opis problemu,
- datę i godzinę,
- zakres raportu,
- nazwę aktualnego ekranu,
- ustawiony motyw,
- wersję aplikacji,
- producenta, model i nazwę urządzenia,
- wersję Androida i SDK,
- rozdzielczość, gęstość i orientację ekranu,
- język systemu,
- nazwę wybranego użytkownika M3U,
- nazwę wybranego użytkownika Pakietów,
- liczbę użytkowników M3U/Pakietów,
- liczbę lokalnie zapisanych snapshotów diagnostycznych.

## Diagnostyka zaawansowana

Użytkownik wybiera jedną sekcję. Aplikacja dołącza zapisany lokalnie snapshot odpowiedzi HTML/JSON związany z tą sekcją. Przed wysłaniem wykonywane jest oczyszczanie:

- usuwanie parametrów sesji `ssn`,
- usuwanie nagłówków Bearer,
- usuwanie wartości pól podobnych do `password`, `passwd`, `token`, `secret` i `api_hash`,
- zamiana adresów IPv4 na `[ip-usuniete]`,
- usuwanie cudzych adresów email; pozostaje wyłącznie email kontaktowy wpisany przez użytkownika.

Aplikacja nie pozwala wybrać do diagnostyki zaawansowanej ekranu doładowania ani ustawień konta.

## Przechowywanie

Snapshoty diagnostyczne są przechowywane wyłącznie w pamięci procesu aplikacji. Maksymalnie przechowywanych jest 10 ostatnich wpisów. Są czyszczone po wylogowaniu lub zamknięciu procesu.

Po udanym wysłaniu kopia raportu jest umieszczana w schowku urządzenia, zgodnie z dotychczasowym zachowaniem aplikacji. Gdy backend nie odpowiada, aplikacja otwiera systemowe udostępnianie jako drogę awaryjną.

## Płatności

Płatności są wykonywane w oryginalnym portalu w WebView. Aplikacja nie implementuje własnego formularza karty i nie przesyła danych płatniczych do backendu Torvinek.

## Uprawnienia

Aplikacja deklaruje tylko uprawnienie `INTERNET`.
