# Współpraca

1. Nie dodawaj sekretów ani danych użytkowników.
2. Nie zmieniaj logowania tak, aby omijało reCAPTCHA.
3. Nie wysyłaj cookies panelu do żadnej domeny poza dokładnym hostem panelu.
4. Nie wysyłaj tokenu backendu do żadnej domeny poza dokładnym hostem backendu.
5. Każda zmiana parsera powinna mieć test.
6. Przed pull requestem uruchom:

```bash
./gradlew test assembleDebug
```
