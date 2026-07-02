# Bezpieczeństwo

Nie publikuj problemów zawierających:

- login lub hasło,
- cookies,
- User Key,
- pełny link M3U,
- token backendu,
- keystore lub hasło do keystore,
- plik sesji Telegram,
- dane płatnicze.

Problemy bezpieczeństwa zgłaszaj prywatnie właścicielowi repozytorium.

## Model sekretów

Repozytorium i workflow nie zawierają prawdziwych sekretów. Sekrety builda są dostarczane przez GitHub Actions Secrets albo lokalne pliki ignorowane przez Git.

Klient mobilny nie powinien otrzymywać sekretów administracyjnych. Token aplikacyjny musi być ograniczony po stronie backendu do publicznych funkcji mobilnych, posiadać rate limiting i możliwość rotacji.
