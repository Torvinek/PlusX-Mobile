package pl.torvinek.plusxmobile

object ServerMessagesParser {
    enum class MessageType { NEWS, UPDATE, MAINTENANCE, WARNING, INFO }

    data class ServerMessage(
        val id: String,
        val type: MessageType,
        val label: String,
        val date: String?,
        val title: String,
        val body: String,
        val extraLabel: String?,
        val extraText: String?
    )

    fun parse(html: String): List<ServerMessage> {
        val cleaned = removeNoise(html)
        val labelRegex = Regex("(NOWO(?:Ś|S)Ć|NOWOSC|UPDATE|MAINTENANCE|WARNING)", RegexOption.IGNORE_CASE)
        val labels = labelRegex.findAll(cleaned).toList()
        if (labels.isEmpty()) return emptyList()

        return labels.mapIndexedNotNull { index, match ->
            val start = maxOf(0, cleaned.lastIndexOf("<", match.range.first).takeIf { it >= 0 } ?: match.range.first)
            val nextStart = labels.getOrNull(index + 1)?.range?.first ?: cleaned.length
            val rawBlock = cleaned.substring(start, nextStart)
            parseBlock(index, rawBlock)
        }.distinctBy { it.title + it.date }
    }

    private fun parseBlock(index: Int, rawBlock: String): ServerMessage? {
        val text = cleanText(rawBlock)
        if (text.contains("DASHBOARD", true) || text.contains("Your balance", true) || text.contains("Your IP address", true)) {
            return null
        }

        val label = Regex("(NOWO(?:Ś|S)Ć|NOWOSC|UPDATE|MAINTENANCE|WARNING)", RegexOption.IGNORE_CASE)
            .find(text)
            ?.value
            ?.uppercase()
            ?.replace("NOWOŚĆ", "NOWOSC")
            ?: "INFO"
        val type = when (label) {
            "NOWOSC" -> MessageType.NEWS
            "UPDATE" -> MessageType.UPDATE
            "MAINTENANCE" -> MessageType.MAINTENANCE
            "WARNING" -> MessageType.WARNING
            else -> MessageType.INFO
        }
        val date = Regex("(\\d{1,2}\\s+[A-ZĄĆĘŁŃÓŚŹŻ]+\\s+\\d{4})", RegexOption.IGNORE_CASE)
            .find(text)
            ?.value
        val headingTitle = Regex("<h[1-6]\\b[^>]*>(.*?)</h[1-6]>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
            .find(rawBlock)
            ?.groupValues
            ?.get(1)
            ?.let(::cleanText)
            ?.takeIf { it.isNotBlank() }
        val title = headingTitle ?: Regex("(Nowe kanały w ofercie!|Nowe kanaly w ofercie!|Optymalizacja CDN 1|VOD:\\s*Przerwa techniczna|[A-ZĄĆĘŁŃÓŚŹŻ][^.!?]{4,80}[.!?])")
            .find(text.substringAfter(date.orEmpty(), text))
            ?.value
            ?.trim()
            ?: return null

        val extraMatch = Regex("(Informacja|Instrukcja|Status prac):\\s*(.+)$", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
            .find(text)
        val bodySource = text
            .substringAfter(title, "")
            .substringBefore(extraMatch?.value.orEmpty())
            .trim()
        val body = bodySource.ifBlank { text.substringAfter(title, "").trim() }

        return ServerMessage(
            id = "${label.lowercase()}-$index",
            type = type,
            label = label,
            date = date,
            title = title,
            body = body,
            extraLabel = extraMatch?.groupValues?.getOrNull(1),
            extraText = extraMatch?.groupValues?.getOrNull(2)?.trim()
        )
    }

    private fun removeNoise(html: String): String {
        return html
            .replace(Regex("<script\\b.*?</script>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)), " ")
            .replace(Regex("<style\\b.*?</style>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)), " ")
            .replace(Regex("<nav\\b.*?</nav>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)), " ")
            .replace(Regex("<aside\\b.*?</aside>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)), " ")
            .replace(Regex("<div\\b[^>]*class=[\"'][^\"']*(page-loader-wrapper|search-bar|info-box|navbar|sidebar|chat)[^\"']*[\"'][^>]*>.*?</div>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)), " ")
    }

    private fun cleanText(value: String): String {
        return value
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .decodeNamedEntities()
            .replace(Regex("<[^>]+>"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun String.decodeNamedEntities(): String {
        return this
            .replace("&lstrok;", "ł")
            .replace("&Lstrok;", "Ł")
            .replace("&oacute;", "ó")
            .replace("&Oacute;", "Ó")
            .replace("&aacute;", "á")
            .replace("&Aacute;", "Á")
            .replace("&eogon;", "ę")
            .replace("&Eogon;", "Ę")
            .replace("&eacute;", "é")
            .replace("&Eacute;", "É")
            .replace("&cacute;", "ć")
            .replace("&Cacute;", "Ć")
            .replace("&nacute;", "ń")
            .replace("&Nacute;", "Ń")
            .replace("&sacute;", "ś")
            .replace("&Sacute;", "Ś")
            .replace("&zacute;", "ź")
            .replace("&Zacute;", "Ź")
            .replace("&zdot;", "ż")
            .replace("&Zdot;", "Ż")
            .replace("&amp;", "&")
    }
}
