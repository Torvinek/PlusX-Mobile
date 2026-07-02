# Połączenia sieciowe

## `new.plusx.tv`

Cel:

- logowanie i reCAPTCHA,
- dashboard i saldo,
- pakiety,
- reseller,
- M3U/EPG/User Key,
- profil,
- płatności i finalne potwierdzenie zakupu.

Dane:

- cookies sesji panelu,
- standardowe nagłówki HTTP,
- parametry potrzebne do działania strony.

Cookies mogą zostać dołączone wyłącznie do dokładnego hosta panelu przez `AppConfig.requirePortalUrl()` i `NetworkPolicy.requireHttpsHost()`.

## `backend.torvinek.pl`

Cel:

- `GET /telegram/plusx/wiadomosci`,
- `GET /telegram/plusx/epgevent`,
- `POST /telegram/plusx/diagnostics`.

Dane:

- aplikacyjny Bearer przekazany podczas budowania,
- żądanie pobrania wiadomości/EPG,
- raport diagnostyczny zatwierdzony przez użytkownika.

Do backendu nie są dołączane cookies panelu PlusX.

## Ochrona przed pomyleniem hostów

Każdy URL jest parsowany i sprawdzany na podstawie protokołu i pełnej nazwy hosta. Dozwolone jest tylko HTTPS. Domeny podobne, np. `backend.torvinek.pl.example.org`, nie przechodzą walidacji.
