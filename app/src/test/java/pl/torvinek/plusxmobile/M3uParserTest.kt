package pl.torvinek.plusxmobile

import org.junit.Assert.assertEquals
import org.junit.Test

class M3uParserTest {
    @Test
    fun parserBuildsUsersUserKeyAndThreePlaylistGroupsSortedByCdn() {
        val data = M3uHtmlParser.parse(
            """
            <html><body>
              <select name="selected_user">
                <option value="main">main</option>
                <option value="client_a" selected>client_a</option>
                <option value="client_b">client_b</option>
              </select>
              <label>User Key</label><div class="vod-value">real_user_key_123456</div>
              <code>https://itvn.io/play20.php?access_key=aaa</code>
              <code>https://itvn.io/play.php?access_key=aaa</code>
              <code>https://itvn.io/play16.php?access_key=aaa</code>
              <code>https://itvn.io/smarttv20.php?access_key=aaa</code>
              <code>https://itvn.io/smarttv.php?access_key=aaa</code>
              <code>https://itvn.io/smarttv16.php?access_key=aaa</code>
              <code>https://itvn.io/playss20.php?access_key=aaa</code>
              <code>https://itvn.io/playss.php?access_key=aaa</code>
              <code>https://itvn.io/playss16.php?access_key=aaa</code>
              <code>https://itvn.io/xmltv/pl10.gz</code>
            </body></html>
            """.trimIndent()
        )

        assertEquals("client_a", data.selectedUser)
        assertEquals(listOf("main", "client_a", "client_b"), data.availableUsers)
        assertEquals("real_user_key_123456", data.userKey)
        assertEquals(3, data.groups.size)
        data.groups.forEach { group ->
            assertEquals(listOf(1, 2, 3), group.links.map { it.cdn })
            assertEquals(3, group.links.size)
        }
        assertEquals(1, data.epgLinks.size)
    }

    @Test
    fun parserDecodesProtectedSelectedEmail() {
        val data = M3uHtmlParser.parse(
            """
            <select name="selected_user">
              <option value="main" selected><span data-cfemail="3054555d5f705548515d405c551e535f5d">[email protected]</span></option>
            </select>
            """.trimIndent()
        )

        assertEquals("demo@example.com", data.selectedUser)
    }
}
