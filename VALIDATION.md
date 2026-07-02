# Walidacja paczki

Paczka została przygotowana z aktualnego projektu `PlusXMobile.zip` i zachowuje:

- namespace/applicationId `pl.torvinek.plusxmobile`,
- wersję `1.5.1` / versionCode `7`,
- dashboard, logowanie WebView i cookies panelu,
- wiadomości oraz EPG z backendu,
- pakiety, reseller i M3U,
- podstawową i zaawansowaną diagnostykę wraz z wysyłką na backend,
- plik `app/src/main/assets/plusx_embedded_resource.dat` o rozmiarze 3 145 728 bajtów,
- release minification/shrink resources i podpisywanie przez sekrety CI.

Wykonane kontrole:

- brak prawdziwego tokenu backendu w repozytorium,
- brak `local.properties`, `signing.properties`, keystore i sesji Telegram,
- kompilacja czystych klas Kotlin odpowiedzialnych za politykę hostów i sanitizację,
- test sanitizacji cookies, Bearer, User Key, access key, parametrów URL, IP i cudzych emaili,
- test odrzucania domen podobnych do `new.plusx.tv.example.org`,
- poprawność plików XML,
- kontrola struktury ZIP i suma SHA-256.

Pełny Android build jest uruchamiany przez workflow `.github/workflows/android-ci.yml`. Oficjalny release z działającym backendem wymaga ustawienia sekretu `PLUSX_BACKEND_TOKEN` w GitHub Actions.

## Uczciwe ograniczenie

GitHub Actions Secrets zabezpieczają token przed publikacją w kodzie i logach. Statycznego tokenu używanego przez aplikację nie da się uczynić absolutnie niewydobywalnym z gotowego APK. Wariant bez sekretu w APK wymaga odpowiedniej zmiany backendu opisanej w `BACKEND_SECURITY.md`.
