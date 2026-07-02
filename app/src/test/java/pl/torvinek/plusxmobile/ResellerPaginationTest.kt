package pl.torvinek.plusxmobile

import org.junit.Assert.assertEquals
import org.junit.Test

class ResellerPaginationTest {
    @Test
    fun threePagesWithOneHundredAccountsEachReturnThreeHundredUniqueAccounts() {
        val pages = mapOf(
            1 to dealerFixture(page = 1, start = 1, end = 100, navPages = listOf(2)),
            2 to dealerFixture(page = 2, start = 101, end = 200, navPages = listOf(3)),
            3 to dealerFixture(page = 3, start = 201, end = 300, navPages = emptyList())
        )

        val result = ResellerPaginator.fetchAll { url ->
            pages[url.pageParam()] ?: error("missing fixture")
        }

        assertEquals(3, result.requestCount)
        assertEquals(300, result.accounts.size)
        assertEquals("client_001", result.accounts.first().login)
        assertEquals("client_300", result.accounts.last().login)
    }

    @Test
    fun currentPageOneWithArrowToPageOneStopsAfterOneRequest() {
        val result = ResellerPaginator.fetchAll {
            dealerFixture(page = 1, start = 1, end = 1, navPages = listOf(1))
        }

        assertEquals(1, result.requestCount)
        assertEquals(1, result.accounts.size)
    }

    @Test
    fun pageOnePointingToPageTwoFetchesExactlyTwoPages() {
        val pages = mapOf(
            1 to dealerFixture(page = 1, start = 1, end = 1, navPages = listOf(2)),
            2 to dealerFixture(page = 2, start = 2, end = 2, navPages = emptyList())
        )

        val result = ResellerPaginator.fetchAll { url ->
            pages[url.pageParam()] ?: error("missing fixture")
        }

        assertEquals(2, result.requestCount)
        assertEquals(2, result.accounts.size)
    }

    @Test
    fun pageTwoPointingToPageTwoDoesNotLoop() {
        val pages = mapOf(
            1 to dealerFixture(page = 1, start = 1, end = 1, navPages = listOf(2)),
            2 to dealerFixture(page = 2, start = 2, end = 2, navPages = listOf(2))
        )

        val result = ResellerPaginator.fetchAll { url ->
            pages[url.pageParam()] ?: error("missing fixture")
        }

        assertEquals(2, result.requestCount)
        assertEquals(2, result.accounts.size)
    }

    @Test
    fun resellerParserReadsNoticeFromDataAttribute() {
        val data = ResellerHtmlParser.parseAccounts(
            """
            <tr class="table-card-row-wrapper">
              <td><a class="user-link">client_note</a></td>
              <td><input value="" class="inline-notice-input" data-note="Salon TV"></td>
              <td><span class="expiry-date">01.08.2026</span><span class="days-badge">30 Days</span></td>
            </tr>
            """.trimIndent()
        )

        assertEquals("Salon TV", data.single().notice)
    }

    private fun String.pageParam(): Int {
        return Regex("[?&]page=(\\d+)").find(this)?.groupValues?.get(1)?.toInt() ?: 1
    }

    private fun dealerFixture(page: Int, start: Int, end: Int, navPages: List<Int>): String {
        val rows = (start..end).joinToString("\n") { index ->
            val login = "client_${index.toString().padStart(3, '0')}"
            """
            <tr class="table-card-row-wrapper">
              <td><a class="user-link">$login</a></td>
              <td><input class="inline-notice-input" value="note-${index.toString().padStart(3, '0')}"></td>
              <td><span class="expiry-date">01.08.2026</span><span class="days-badge">30 Days</span></td>
            </tr>
            """.trimIndent()
        }
        val pagination = navPages.joinToString("\n") {
            """<a class="nav-arrow" href="dealer.php?page=$it&items_pp=1000&exp_filter=&sort=login_asc">$it</a>"""
        }
        return """
            <html>
              <body>
                <span class="current-page-badge">$page</span>
                <table>$rows</table>
                <nav>$pagination</nav>
              </body>
            </html>
        """.trimIndent()
    }
}
