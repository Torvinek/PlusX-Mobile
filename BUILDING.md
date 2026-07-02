# Budowanie PlusX Mobile

## Wymagania

- JDK 17,
- Android SDK 35,
- Android Build Tools zgodne z AGP 8.7.3,
- połączenie internetowe podczas pierwszego pobierania zależności.

## Konfiguracja lokalna

Skopiuj `local.properties.example` do `local.properties` i ustaw ścieżkę SDK.

Aby zachować działanie wiadomości, EPG i wysyłania diagnostyki, dodaj:

```properties
PLUSX_BACKEND_TOKEN=wartosc_tokenu_aplikacyjnego
```

`local.properties` jest ignorowany przez Git. Nie wysyłaj go do repozytorium.

Można też użyć zmiennej środowiskowej lub parametru Gradle:

```bash
PLUSX_BACKEND_TOKEN="..." ./gradlew test assembleDebug
```

```bash
./gradlew test assembleDebug -PPLUSX_BACKEND_TOKEN="..."
```

Kolejność odczytu: parametr Gradle, zmienna środowiskowa, `local.properties`, wartość domyślna.

## Debug

```bash
./gradlew test assembleDebug
```

## Release

Skopiuj `signing.properties.example` do `signing.properties` i uzupełnij dane swojego keystore. Plik oraz keystore są ignorowane przez Git.

```bash
./gradlew test assembleRelease
```

## Ważne o tokenie

GitHub Actions Secrets chronią token przed ujawnieniem w repozytorium i logach. Nie sprawiają jednak, że token staje się niewydobywalny z gotowego klienta Android. Token używany przez aplikację musi być ograniczony do minimalnych uprawnień i nie może być kluczem administracyjnym.
