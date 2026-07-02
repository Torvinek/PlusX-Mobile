package pl.torvinek.plusxmobile

/**
 * Oczyszcza zawartość dołączaną do diagnostyki zaawansowanej.
 *
 * Zachowuje strukturę HTML/JSON potrzebną do diagnozy parserów, ale usuwa
 * typowe sekrety, cookies, klucze dostępu, pełne parametry URL, adresy IP
 * i cudze adresy e-mail. Email kontaktowy podany świadomie przez użytkownika
 * pozostaje w raporcie.
 */
internal object DiagnosticSanitizer {
    private const val SENSITIVE_NAMES =
        "password|passwd|pass|token|secret|api_hash|api_key|access_key|user_key|userkey|ssn|phpsessid|sessionid|session_id|cookie"

    fun sanitize(content: String, contactEmail: String): String {
        var result = content

        // Nagłówki HTTP i linie podobne do nagłówków.
        result = result.replace(
            Regex("(?im)^(Cookie|Set-Cookie|Authorization)\\s*:\\s*.*$"),
            "$1: [usunieto]"
        )

        // Wszystkie wartości parametrów URL są usuwane. Struktura URL zostaje,
        // ale np. access_key, selected_user i identyfikatory sesji nie wyciekną.
        result = result.replace(
            Regex("(?i)([?&][A-Za-z0-9_.-]+=)[^&#\\s\\\"'<>]+"),
            "$1[usunieto]"
        )

        // JSON: "access_key": "wartosc" oraz analogiczne pola.
        result = result.replace(
            Regex("(?i)([\\\"](?:$SENSITIVE_NAMES)[\\\"]\\s*:\\s*[\\\"])[^\\\"]*([\\\"])")
        ) { match ->
            match.groupValues[1] + "[usunieto]" + match.groupValues[2]
        }

        // Zwykłe zapisy key=value, key: value itd.
        result = result.replace(
            Regex("(?i)\\b($SENSITIVE_NAMES)([\\\"'\\s:=]+)([^\\\"'\\s<>&]+)"),
            "$1$2[usunieto]"
        )

        // Pola HTML: name="access_key" ... value="...".
        result = result.replace(
            Regex(
                "(?is)(<input\\b(?=[^>]*\\bname\\s*=\\s*[\\\"](?:$SENSITIVE_NAMES)[\\\"])(?=[^>]*\\bvalue\\s*=\\s*[\\\"])(?:[^>]*?\\bvalue\\s*=\\s*[\\\"]))[^\\\"]*([\\\"])"
            )
        ) { match ->
            match.groupValues[1] + "[usunieto]" + match.groupValues[2]
        }

        // Tekstowe/HTML-owe wartości występujące bez nazwy pola, np.:
        // <label>User Key</label><div>ABC...</div>
        result = result.replace(
            Regex("(?is)((?:User\\s*Key|Access\\s*Key)\\s*(?:</?[^>]+>|[:=\\s]){0,8})([A-Za-z0-9._/+\\-=]{8,})")
        ) { match ->
            match.groupValues[1] + "[usunieto]"
        }

        // Pełne linki M3U/EPG zachowują host i ścieżkę, ale parametry są już
        // oczyszczone powyżej. Dodatkowo usuwamy dane basic-auth w URL.
        result = result.replace(
            Regex("(?i)(https://)[^/@\\s]+:[^/@\\s]+@"),
            "$1[usunieto]@"
        )

        // IPv4, w tym adresy prywatnej infrastruktury.
        result = result.replace(
            Regex("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b"),
            "[ip-usuniete]"
        )

        // Adres kontaktowy podany przez użytkownika zostaje, pozostałe e-maile są usuwane.
        result = result.replace(
            Regex("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", RegexOption.IGNORE_CASE)
        ) { match ->
            if (match.value.equals(contactEmail, ignoreCase = true)) {
                match.value
            } else {
                "[email-usuniety]"
            }
        }

        return result
    }
}
