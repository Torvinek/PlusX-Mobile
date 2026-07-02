package pl.torvinek.plusxmobile

object ResellerHtmlParser {
    const val RESELLER_URL = "https://new.plusx.tv/dealer.php?page=1&items_pp=1000&exp_filter=&sort=login_asc"

    data class Account(
        val login: String,
        val notice: String,
        val expiry: String,
        val days: String,
        val hasActivePackage: Boolean
    )

    data class PageData(
        val currentPage: Int,
        val accounts: List<Account>,
        val nextPages: List<Int>
    )

    fun parsePage(html: String): PageData {
        val currentPage = parseCurrentPage(html)
        return PageData(
            currentPage = currentPage,
            accounts = parseAccounts(html),
            nextPages = parseNextPages(html, currentPage)
        )
    }

    fun parseAccounts(html: String): List<Account> {
        return Regex("<tr\\b[^>]*class=[\"'][^\"']*table-card-row-wrapper[^\"']*[\"'][^>]*>(.*?)</tr>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
            .findAll(html)
            .mapNotNull { match ->
                val row = match.groupValues[1]
                val login = Regex("<a\\b[^>]*class=[\"'][^\"']*user-link[^\"']*[\"'][^>]*>(.*?)</a>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
                    .find(row)
                    ?.groupValues
                    ?.get(1)
                    ?.let(::cleanText)
                    .orEmpty()
                if (login.isBlank()) return@mapNotNull null

                Account(
                    login = login,
                    notice = extractNotice(row),
                    expiry = Regex("class=[\"'][^\"']*expiry-date[^\"']*[\"'][^>]*>(.*?)</span>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
                        .find(row)
                        ?.groupValues
                        ?.get(1)
                        ?.let(::cleanText)
                        .orEmpty(),
                    days = Regex("class=[\"'][^\"']*days-badge[^\"']*[\"'][^>]*>(.*?)</span>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
                        .find(row)
                        ?.groupValues
                        ?.get(1)
                        ?.let(::cleanText)
                        .orEmpty(),
                    hasActivePackage = !row.contains("No active packages", ignoreCase = true)
                )
            }
            .distinctBy { it.login }
            .toList()
    }

    fun parseCurrentPage(html: String): Int {
        return Regex("class=[\"'][^\"']*current-page-badge[^\"']*[\"'][^>]*>(\\d+)<", RegexOption.IGNORE_CASE)
            .find(html)
            ?.groupValues
            ?.get(1)
            ?.toIntOrNull()
            ?: 1
    }

    fun parseNextPages(html: String, currentPage: Int = parseCurrentPage(html)): List<Int> {
        return Regex("<a\\b[^>]*class=[\"'][^\"']*nav-arrow[^\"']*[\"'][^>]*href=[\"']([^\"']+)[\"'][^>]*>", RegexOption.IGNORE_CASE)
            .findAll(html)
            .mapNotNull { hrefMatch ->
                Regex("[?&]page=(\\d+)", RegexOption.IGNORE_CASE)
                    .find(hrefMatch.groupValues[1])
                    ?.groupValues
                    ?.get(1)
                    ?.toIntOrNull()
            }
            .filter { it > currentPage }
            .distinct()
            .sorted()
            .toList()
    }

    private fun cleanText(value: String): String {
        return decodeProtectedEmails(value)
            .replace("&euro;", "EUR")
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&#160;", " ")
            .replace(Regex("<[^>]+>"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun extractNotice(row: String): String {
        val noticeTag = Regex("<(?:input|textarea)\\b[^>]*(?:notice|note)[^>]*(?:>.*?</textarea>)?", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
            .find(row)
            ?.value
            .orEmpty()
        val attrs = listOf("value", "data-value", "data-note", "data-notice", "data-original", "data-original-value")
        attrs.forEach { attr ->
            extractAttribute(noticeTag, attr)?.let { value ->
                return cleanText(value)
            }
        }
        Regex("<textarea\\b[^>]*>(.*?)</textarea>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
            .find(noticeTag)
            ?.groupValues
            ?.get(1)
            ?.let(::cleanText)
            ?.takeIf { it.isNotBlank() }
            ?.let { return it }
        Regex("class=[\"'][^\"']*(?:notice|note)[^\"']*[\"'][^>]*>(.*?)</", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
            .find(row)
            ?.groupValues
            ?.get(1)
            ?.let(::cleanText)
            ?.takeIf { it.isNotBlank() }
            ?.let { return it }
        return ""
    }

    private fun extractAttribute(tag: String, name: String): String? {
        return Regex("\\b$name\\s*=\\s*([\"'])(.*?)\\1", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
            .find(tag)
            ?.groupValues
            ?.get(2)
            ?.takeIf { it.isNotBlank() }
    }

    private fun decodeProtectedEmails(value: String): String {
        return Regex("<[^>]*data-cfemail=[\"']([0-9a-fA-F]+)[\"'][^>]*>.*?</[^>]+>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)).replace(value) { match ->
            val hex = match.groupValues[1]
            val key = hex.take(2).toIntOrNull(16) ?: return@replace match.value
            hex.drop(2)
                .chunked(2)
                .mapNotNull { it.toIntOrNull(16)?.xor(key)?.toChar() }
                .joinToString("")
        }
    }
}
