package pl.torvinek.plusxmobile

import java.net.URI

object M3uHtmlParser {
    data class M3uScreenData(
        val selectedUser: String,
        val availableUsers: List<String>,
        val userKey: String?,
        val groups: List<PlaylistGroup>,
        val epgLinks: List<PlaylistLink>
    )

    data class PlaylistGroup(
        val type: PlaylistGroupType,
        val links: List<PlaylistLink>
    )

    enum class PlaylistGroupType { TIVIMATE, SMART_IPTV, SS_IPTV }

    data class PlaylistLink(
        val cdn: Int,
        val region: String,
        val displayPath: String,
        val fullUrl: String
    )

    fun parse(html: String): M3uScreenData {
        val users = parseUsers(html)
        val selected = users.firstOrNull { it.second }?.first ?: users.firstOrNull()?.first.orEmpty()
        val urls = Regex("https?://[^\\s\"'<>]+", RegexOption.IGNORE_CASE)
            .findAll(html.replace("&amp;", "&"))
            .map { it.value.trimEnd(',', ';', ')') }
            .distinct()
            .toList()

        val links = urls.mapNotNull(::playlistLink)
        val groups = PlaylistGroupType.values().mapNotNull { type ->
            val grouped = links
                .filter { link -> groupTypeForPath(link.displayPath) == type }
                .distinctBy { it.cdn }
                .sortedBy { it.cdn }
            if (grouped.isEmpty()) null else PlaylistGroup(type, grouped)
        }
        val epg = links
            .filter { it.displayPath.contains("xml", true) || it.displayPath.contains("epg", true) || it.displayPath.contains(".gz", true) }
            .sortedBy { it.cdn }

        return M3uScreenData(
            selectedUser = selected,
            availableUsers = users.map { it.first }.distinct(),
            userKey = parseUserKey(html),
            groups = groups,
            epgLinks = epg
        )
    }

    private fun parseUsers(html: String): List<Pair<String, Boolean>> {
        val select = Regex("<select\\b[^>]*(?:name=[\"']selected_user[\"']|id=[\"']komukey[\"'])[^>]*>(.*?)</select>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
            .find(html)
            ?.groupValues
            ?.get(1)
            .orEmpty()
        return Regex("<option\\b([^>]*)>(.*?)</option>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
            .findAll(select)
            .map { match ->
                val attrs = match.groupValues[1]
                val value = Regex("value=[\"']([^\"']+)[\"']", RegexOption.IGNORE_CASE).find(attrs)?.groupValues?.get(1)
                val label = cleanText(match.groupValues[2])
                val user = when {
                    label.contains("@") || label.contains("protected", true) -> label
                    value.isNullOrBlank() -> label
                    else -> value
                }
                user to attrs.contains("selected", ignoreCase = true)
            }
            .filter { it.first.isNotBlank() }
            .toList()
    }

    private fun parseUserKey(html: String): String? {
        return Regex("User\\s*Key.*?<div[^>]*class=[\"'][^\"']*vod-value[^\"']*[\"'][^>]*>(.*?)</div>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
            .find(html)
            ?.groupValues
            ?.get(1)
            ?.let(::cleanText)
            ?.takeIf { it.isNotBlank() }
    }

    private fun playlistLink(url: String): PlaylistLink? {
        val path = runCatching { URI(url).path.orEmpty() }.getOrDefault("")
        val file = path.substringAfterLast("/")
        val cdn = when {
            file.contains("20", true) -> 3
            file.contains("16", true) -> 2
            else -> 1
        }
        val display = "${runCatching { URI(url).host.orEmpty() }.getOrDefault("")}/$file"
        val isKnown = listOf("play.php", "play16.php", "play20.php", "smarttv.php", "smarttv16.php", "smarttv20.php", "playss.php", "playss16.php", "playss20.php")
            .any { file.equals(it, ignoreCase = true) } || file.contains("epg", true) || file.endsWith(".gz", true) || file.endsWith(".xml", true)
        if (!isKnown) return null
        return PlaylistLink(cdn, region(cdn), display, url)
    }

    private fun groupTypeForPath(displayPath: String): PlaylistGroupType? {
        val file = displayPath.substringAfterLast("/")
        return when {
            file.startsWith("smarttv", true) -> PlaylistGroupType.SMART_IPTV
            file.startsWith("playss", true) -> PlaylistGroupType.SS_IPTV
            file.startsWith("play", true) -> PlaylistGroupType.TIVIMATE
            else -> null
        }
    }

    private fun region(cdn: Int): String {
        return when (cdn) {
            1 -> "Zalecany w Polsce"
            2 -> "Wielka Brytania, Niemcy, Holandia"
            else -> "Stany Zjednoczone i Kanada"
        }
    }

    private fun cleanText(value: String): String {
        return decodeProtectedEmails(value)
            .replace("&nbsp;", " ")
            .replace("&#160;", " ")
            .replace("&ensp;", " ")
            .replace("&emsp;", " ")
            .replace("&amp;", "&")
            .replace(Regex("<[^>]+>"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
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
