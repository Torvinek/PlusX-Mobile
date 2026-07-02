# Bezpieczeństwo backendu mobilnego

## Co zapewnia to repozytorium

- prawdziwy token backendu nie jest zapisany w kodzie ani historii repozytorium,
- oficjalny build może otrzymać token z GitHub Actions Secrets,
- cookies panelu PlusX są dołączane wyłącznie do dokładnego hosta `new.plusx.tv`,
- token mobilny jest dołączany wyłącznie do dokładnego hosta `backend.torvinek.pl`,
- diagnostyka działa jak wcześniej, ale jej snapshot jest dodatkowo oczyszczany z cookies, nagłówków autoryzacji, User Key, access key, parametrów URL, adresów IP i cudzych adresów e-mail.

## Ważne ograniczenie Androida

Sekret potrzebny aplikacji podczas działania nie może być absolutnie ukryty w APK. GitHub Actions Secrets chronią go przed publikacją w repozytorium i logach builda, ale token umieszczony w gotowej aplikacji może zostać odzyskany przez zdeterminowaną osobę.

Dlatego token mobilny nie może być tym samym sekretem co:

- hasło panelu administracyjnego,
- token administracyjny backendu,
- dane Cloudflare Tunnel,
- sesja Telegram,
- klucz SSH,
- keystore Androida.

## Tryb zgodny z obecną aplikacją

Utwórz osobny token mobilny, który może wyłącznie:

- `GET /telegram/plusx/wiadomosci`,
- `GET /telegram/plusx/epgevent`,
- `POST /telegram/plusx/diagnostics`.

Nie może otwierać `/panel`, wysyłać wiadomości na Telegram, odczytywać pliku sesji ani wykonywać operacji administracyjnych. Dodaj limit żądań, limit wielkości raportu i możliwość natychmiastowej rotacji tokenu.

## Wariant bez sekretu w APK — zalecany docelowo

Najmocniejszy wariant to usunięcie statycznego tokenu z klienta:

1. endpointy wiadomości i EPG są publiczne tylko do odczytu i objęte rate limitingiem,
2. endpoint diagnostyki przyjmuje wyłącznie ściśle walidowany JSON o ograniczonym rozmiarze,
3. panel administracyjny ma całkowicie oddzielne uwierzytelnienie,
4. backend nie przyjmuje cookies, loginów, haseł, User Key ani pełnych linków M3U,
5. sesja Telegram i sekrety serwera nigdy nie opuszczają LXC.

Aplikacja już obsługuje pusty `PLUSX_BACKEND_TOKEN`: wtedy nie wysyła nagłówka `Authorization`. Po odpowiednim zabezpieczeniu endpointów po stronie serwera można więc całkowicie usunąć token mobilny bez przebudowy logiki aplikacji.
