# Współpraca

1. Nie dodawaj sekretów ani danych użytkowników.
2. Nie wysyłaj cookies panelu do żadnej domeny poza dokładnym hostem panelu.
3. Nie wysyłaj tokenu backendu do żadnej domeny poza dokładnym hostem backendu.
4. Każda zmiana parsera powinna mieć test.
5. Przed pull requestem uruchom:

```bash
./gradlew test assembleDebug
```
