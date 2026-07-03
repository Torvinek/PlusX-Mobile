<p align="center">
  <img src="https://raw.githubusercontent.com/Torvinek/PlusX-Mobile/main/assets/plusx-banner.png" width="820" alt="PlusX Mobile â€” nieoficjalna aplikacja Android dla panelu PlusX">
</p>

<h1 align="center">PlusX Mobile</h1>

<p align="center">
  <strong>Nieoficjalna aplikacja Android z natywnym interfejsem mobilnym dla panelu PlusX.</strong>
</p>

<p align="center">
  <a href="https://github.com/Torvinek/PlusX-Mobile/actions/workflows/android-ci.yml"><img src="https://img.shields.io/github/actions/workflow/status/Torvinek/PlusX-Mobile/android-ci.yml?branch=main&style=for-the-badge&label=Build" alt="Status builda"></a>
  <a href="https://github.com/Torvinek/PlusX-Mobile/releases/latest"><img src="https://img.shields.io/badge/Wersja-latest-1683D8?style=for-the-badge" alt="Najnowsza wersja"></a>
  <img src="https://img.shields.io/badge/Android-9%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android 9 lub nowszy">
  <img src="https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin 2.0.21">
  <a href="LICENSE"><img src="https://img.shields.io/badge/Kod-source--available-C62828?style=for-the-badge" alt="Licencja Source-Available"></a>
</p>

<p align="center">
  <a href="https://github.com/Torvinek/PlusX-Mobile/releases/latest"><strong>â¬‡ď¸Ź Pobierz najnowsze APK</strong></a>
  &nbsp;â€˘&nbsp;
  <a href="PRIVACY.md">PrywatnoĹ›Ä‡</a>
  &nbsp;â€˘&nbsp;
  <a href="SECURITY.md">BezpieczeĹ„stwo</a>
  &nbsp;â€˘&nbsp;
  <a href="BUILDING.md">Samodzielny build</a>
</p>

---

> [!IMPORTANT]
> **PlusX Mobile jest projektem niezaleĹĽnym i nieoficjalnym.** Nie jest wĹ‚asnoĹ›ciÄ… operatora panelu PlusX ani oficjalnÄ… aplikacjÄ… jego wĹ‚aĹ›ciciela. Do dziaĹ‚ania wymagane jest wĹ‚asne, aktywne konto w obsĹ‚ugiwanym panelu.

> [!CAUTION]
> **Kod jest publicznie widoczny wyĹ‚Ä…cznie w celu przejrzystoĹ›ci, audytu bezpieczeĹ„stwa i prywatnego testowania. Projekt nie jest open source.** Bez wczeĹ›niejszej pisemnej zgody autora zabronione jest kopiowanie kodu do innych repozytoriĂłw, tworzenie i publikowanie mirrorĂłw lub niezaleĹĽnych forkĂłw, redystrybucja ĹşrĂłdeĹ‚ albo APK, publikowanie aplikacji pochodnych oraz uĹĽycie komercyjne. WiÄ…ĹĽÄ…ce warunki znajdujÄ… siÄ™ w pliku [LICENSE](LICENSE).

## Spis treĹ›ci

- [O projekcie](#o-projekcie)
- [NajwaĹĽniejsze funkcje](#najwaĹĽniejsze-funkcje)
- [Jak dziaĹ‚a aplikacja](#jak-dziaĹ‚a-aplikacja)
- [PrywatnoĹ›Ä‡ i diagnostyka](#prywatnoĹ›Ä‡-i-diagnostyka)
- [BezpieczeĹ„stwo sekretĂłw](#bezpieczeĹ„stwo-sekretĂłw)
- [Instalacja](#instalacja)
- [Samodzielny build](#samodzielny-build)
- [Weryfikacja wydania](#weryfikacja-wydania)
- [ZgĹ‚aszanie bĹ‚Ä™dĂłw](#zgĹ‚aszanie-bĹ‚Ä™dĂłw-i-problemĂłw-bezpieczeĹ„stwa)
- [Dokumentacja](#dokumentacja)
- [Licencje](#licencje)

## O projekcie

Oryginalny panel `new.plusx.tv` zostaĹ‚ przygotowany przede wszystkim z myĹ›lÄ… o komputerach. PlusX Mobile porzÄ…dkuje jego najwaĹĽniejsze funkcje i przedstawia je w interfejsie dopasowanym do telefonu.

Aplikacja nie ma dostÄ™pu do bazy danych panelu i nie omija zabezpieczeĹ„ logowania. DziaĹ‚a jako mobilna warstwa nad istniejÄ…cym serwisem:

1. logowanie odbywa siÄ™ na oryginalnej stronie w `WebView`,
2. uĹĽytkownik sam wpisuje dane i przechodzi reCAPTCHA,
3. aplikacja korzysta z lokalnej sesji po poprawnym zalogowaniu,
4. strony panelu sÄ… pobierane bezpoĹ›rednio przez urzÄ…dzenie uĹĽytkownika,
5. parsery zamieniajÄ… potrzebne fragmenty HTML na natywny interfejs Androida,
6. pĹ‚atnoĹ›ci, zmiana hasĹ‚a, 2FA i finalne potwierdzenie zakupu pozostajÄ… w oryginalnym portalu.

## NajwaĹĽniejsze funkcje

| Obszar | Funkcje |
|---|---|
| **Panel gĹ‚Ăłwny** | saldo, odĹ›wieĹĽanie balansu, szybka nawigacja, animacje oraz jasny i ciemny motyw |
| **WiadomoĹ›ci** | informacje z panelu i backendu, oczyszczanie treĹ›ci oraz usuwanie duplikatĂłw |
| **Programy na dziĹ›** | wydarzenia EPG, kanaĹ‚, godziny rozpoczÄ™cia i zakoĹ„czenia oraz bitrate |
| **Pakiety** | ceny miesiÄ™czne i roczne, wybĂłr uĹĽytkownika oraz potwierdzenie przed przejĹ›ciem do zakupu |
| **Reseller Panel** | automatyczna paginacja, status konta, data wygaĹ›niÄ™cia i przejĹ›cie do pakietĂłw lub M3U |
| **Linki M3U** | prawdziwy User Key, TiviMate, Smart IPTV, SS IPTV, CDN-y oraz EPG |
| **Ustawienia** | profil, motyw, zmiana hasĹ‚a i 2FA przez oryginalny portal |
| **Diagnostyka** | raport podstawowy albo zaawansowany wysyĹ‚any dopiero po Ĺ›wiadomym zatwierdzeniu przez uĹĽytkownika |

## Jak dziaĹ‚a aplikacja

### Panel PlusX

PoĹ‚Ä…czenia do panelu sÄ… wykonywane bezpoĹ›rednio pomiÄ™dzy telefonem uĹĽytkownika a:

```text
https://new.plusx.tv
```

Cookies sesji sÄ… uĹĽywane wyĹ‚Ä…cznie dla wĹ‚aĹ›ciwego hosta panelu. Nie sÄ… przekazywane do backendu projektu.

### Backend projektu

Backend Torvinek sĹ‚uĹĽy do pobierania wiadomoĹ›ci, programu EPG oraz â€” po zgodzie uĹĽytkownika â€” odbierania diagnostyki:

```text
https://backend.torvinek.pl
```

Klient panelu i klient backendu sÄ… rozdzielone. Token backendu nie jest wysyĹ‚any do panelu PlusX, a cookies panelu nie sÄ… wysyĹ‚ane do backendu Torvinek.

WiÄ™cej informacji znajduje siÄ™ w [NETWORK.md](NETWORK.md) i [BACKEND_SECURITY.md](BACKEND_SECURITY.md).

## PrywatnoĹ›Ä‡ i diagnostyka

### Dane, ktĂłrych aplikacja nie wysyĹ‚a do backendu Torvinek

- hasĹ‚a do panelu,
- cookies sesji,
- User Key,
- peĹ‚nych linkĂłw M3U i wartoĹ›ci `access_key`,
- danych pĹ‚atniczych,
- zawartoĹ›ci kont resellerĂłw,
- nieoczyszczonego surowego HTML stron panelu.

### Diagnostyka

Diagnostyka nie jest wysyĹ‚ana automatycznie w tle. UĹĽytkownik sam otwiera formularz, wybiera zakres danych i zatwierdza wysĹ‚anie raportu.

Raport podstawowy moĹĽe zawieraÄ‡:

- adres email kontaktowy wpisany Ĺ›wiadomie przez uĹĽytkownika,
- opis problemu,
- datÄ™ i godzinÄ™,
- zakres raportu,
- wersjÄ™ aplikacji,
- aktualny ekran i ustawiony motyw,
- producenta, model i nazwÄ™ urzÄ…dzenia,
- wersjÄ™ Androida i SDK,
- rozdzielczoĹ›Ä‡, gÄ™stoĹ›Ä‡ i orientacjÄ™ ekranu,
- jÄ™zyk systemu,
- nazwÄ™ aktualnie wybranego uĹĽytkownika M3U,
- nazwÄ™ aktualnie wybranego uĹĽytkownika PakietĂłw,
- liczbÄ™ dostÄ™pnych uĹĽytkownikĂłw M3U/PakietĂłw,
- liczbÄ™ lokalnie zapisanych snapshotĂłw diagnostycznych.

Raport zaawansowany moĹĽe dodatkowo zawieraÄ‡ oczyszczony snapshot wybranej sekcji aplikacji. Przed wysĹ‚aniem sanitizator prĂłbuje wykryÄ‡ i usunÄ…Ä‡ typowe dane wraĹĽliwe, w tym:

- parametry sesji `ssn`,
- nagĹ‚Ăłwki Bearer,
- wartoĹ›ci pĂłl podobnych do `password`, `passwd`, `token`, `secret` i `api_hash`,
- adresy IPv4,
- obce adresy email,
- typowe parametry dostÄ™powe i wartoĹ›ci przypominajÄ…ce klucze.

Do diagnostyki zaawansowanej nie moĹĽna wybraÄ‡ ekranu doĹ‚adowania ani ustawieĹ„ konta.

PeĹ‚ny i wiÄ…ĹĽÄ…cy opis zakresu raportĂłw znajduje siÄ™ w [PRIVACY.md](PRIVACY.md).

## BezpieczeĹ„stwo sekretĂłw

Publiczne repozytorium nie zawiera:

- tokenu backendu,
- haseĹ‚ do keystore,
- prywatnego keystore,
- sesji Telegram,
- danych Cloudflare Tunnel,
- plikĂłw `.env`, `local.properties` ani `signing.properties`,
- danych logowania uĹĽytkownikĂłw.

Sekrety oficjalnego builda sÄ… przekazywane przez GitHub Actions Secrets. Lokalne pliki konfiguracyjne sÄ… ignorowane przez Git.

> [!WARNING]
> Sekret uĹĽywany przez aplikacjÄ™ klienckÄ… nie moĹĽe byÄ‡ jednoczeĹ›nie wbudowany w APK i absolutnie niewydobywalny z tego APK. Dlatego token mobilny musi mieÄ‡ wyĹ‚Ä…cznie minimalne uprawnienia potrzebne do wiadomoĹ›ci, EPG i diagnostyki. Nie powinien zapewniaÄ‡ dostÄ™pu administracyjnego, dostÄ™pu do Telegrama, SSH, Cloudflare ani infrastruktury serwera.

SzczegĂłĹ‚owy model zabezpieczeĹ„ backendu opisuje [BACKEND_SECURITY.md](BACKEND_SECURITY.md).

## Instalacja

1. OtwĂłrz stronÄ™ [najnowszego wydania](https://github.com/Torvinek/PlusX-Mobile/releases/latest).
2. Pobierz plik APK.
3. Pobierz plik `.sha256.txt`, ktory znajduje sie przy kazdym wydaniu na GitHubie.
4. Porownaj sume SHA-256 pobranego APK z wartoscia zapisana w pliku `.sha256.txt`.
5. Zezwol Androidowi na instalowanie aplikacji z wybranego zrodla.
6. Zainstaluj APK i zaloguj sie przez oryginalny formularz panelu.

**Wymagany system:** Android 9 lub nowszy, czyli API 28+.

## Samodzielny build

Kod moĹĽna pobraÄ‡ lokalnie wyĹ‚Ä…cznie w celu prywatnego audytu i testowania, zgodnie z [LICENSE](LICENSE).

Wymagania:

- JDK 17,
- Android SDK 35,
- Android Studio lub Gradle Wrapper.

### Linux / macOS

```bash
./gradlew test assembleDebug
```

### Windows

```powershell
.\gradlew.bat test assembleDebug
```

Gotowy debug APK:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Podstawowe funkcje panelu moĹĽna zbudowaÄ‡ bez prywatnego tokenu. WiadomoĹ›ci, EPG i wysyĹ‚ka diagnostyki do chronionego backendu wymagajÄ… lokalnej konfiguracji opisanej w [BUILDING.md](BUILDING.md).

## Weryfikacja wydania

KaĹĽdy push i pull request uruchamia testy oraz budowanie debug APK w GitHub Actions. Oficjalne wydania utworzone z tagĂłw mogÄ… byÄ‡ podpisywane automatycznie i publikowane razem z sumÄ… SHA-256.

### Windows PowerShell

```powershell
Get-FileHash .\PlusXMobile-ReleasevX.Y.Z.apk -Algorithm SHA256
```

### Linux

```bash
sha256sum PlusXMobile-ReleasevX.Y.Z.apk
```

Przy kazdym GitHub Release publikowany jest osobny plik `.sha256.txt` dla APK. Wynik komendy musi byc identyczny z wartoscia zapisana w tym pliku.

## ZgĹ‚aszanie bĹ‚Ä™dĂłw i problemĂłw bezpieczeĹ„stwa

- ZwykĹ‚e bĹ‚Ä™dy zgĹ‚aszaj przez zakĹ‚adkÄ™ **Issues**.
- Nie publikuj w zgĹ‚oszeniu cookies, tokenĂłw, User Key, peĹ‚nych linkĂłw M3U, danych kont ani surowego HTML panelu.
- Problemy bezpieczeĹ„stwa zgĹ‚aszaj zgodnie z [SECURITY.md](SECURITY.md).

## Dokumentacja

- [PRIVACY.md](PRIVACY.md) â€” prywatnoĹ›Ä‡ i zakres diagnostyki,
- [NETWORK.md](NETWORK.md) â€” domeny i separacja ruchu,
- [BUILDING.md](BUILDING.md) â€” lokalne budowanie i konfiguracja,
- [SECURITY.md](SECURITY.md) â€” bezpieczne zgĹ‚aszanie podatnoĹ›ci,
- [BACKEND_SECURITY.md](BACKEND_SECURITY.md) â€” model tokenĂłw backendu,
- [CONTRIBUTING.md](CONTRIBUTING.md) â€” zasady proponowania zmian,
- [VALIDATION.md](VALIDATION.md) â€” wykonane kontrole projektu.

## Licencje

### Kod PlusX Mobile

Kod naleĹĽÄ…cy do autora projektu jest udostÄ™pniony na niestandardowej licencji **PlusX Mobile Source-Available License v1.0**:

- [LICENSE](LICENSE)

To nie jest licencja open source. Pozwala na przeglÄ…danie kodu, prywatny audyt, lokalne sklonowanie i prywatny build testowy, ale bez pisemnej zgody autora zabrania miÄ™dzy innymi:

- kopiowania lub importowania caĹ‚oĹ›ci albo istotnych fragmentĂłw kodu do innego repozytorium,
- tworzenia i utrzymywania publicznych mirrorĂłw,
- publikowania forka jako osobnego projektu lub alternatywnej dystrybucji,
- redystrybucji kodu ĹşrĂłdĹ‚owego,
- redystrybucji oficjalnego albo zmodyfikowanego APK,
- tworzenia i publikowania aplikacji pochodnych,
- wykorzystania komercyjnego,
- usuwania informacji o autorze i prawach autorskich,
- podszywania siÄ™ pod oficjalne wydanie PlusX Mobile.

### Licencja MIT

Repozytorium zawiera takĹĽe plik:

- [LICENSE-MIT](LICENSE-MIT)

`LICENSE-MIT` **nie obejmuje caĹ‚ego repozytorium i nie zastÄ™puje gĹ‚Ăłwnej licencji projektu**. Ma zastosowanie wyĹ‚Ä…cznie do konkretnego pliku, komponentu lub fragmentu kodu, ktĂłry wprost wskazuje, ĹĽe podlega licencji MIT.

Brak takiego wyraĹşnego oznaczenia oznacza, ĹĽe dany element podlega gĹ‚Ăłwnej licencji [LICENSE](LICENSE), a nie `LICENSE-MIT`.

Biblioteki, usĹ‚ugi, zasoby, nazwy i znaki towarowe stron trzecich zachowujÄ… wĹ‚asne licencje oraz prawa ich wĹ‚aĹ›cicieli.

---

<p align="center">
  <strong>Â© Torvinek 2026 Â· PlusX Mobile</strong><br>
  <sub>Source available for transparency â€” redistribution prohibited.</sub>
</p>


