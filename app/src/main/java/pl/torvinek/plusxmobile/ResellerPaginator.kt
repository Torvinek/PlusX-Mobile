package pl.torvinek.plusxmobile

object ResellerPaginator {
    data class Result(
        val accounts: List<ResellerHtmlParser.Account>,
        val requestCount: Int
    )

    fun fetchAll(fetchHtml: (String) -> String): Result {
        val visitedPages = mutableSetOf<Int>()
        val visitedUrls = mutableSetOf<String>()
        val accounts = linkedMapOf<String, ResellerHtmlParser.Account>()
        var url = ResellerHtmlParser.RESELLER_URL
        var requests = 0

        while (requests < 100) {
            if (!visitedUrls.add(url)) break
            val html = fetchHtml(url)
            requests += 1
            if (isLoginPage(html)) break

            val pageData = ResellerHtmlParser.parsePage(html)
            if (!visitedPages.add(pageData.currentPage)) break

            var newAccounts = 0
            pageData.accounts.forEach { account ->
                if (!accounts.containsKey(account.login)) {
                    accounts[account.login] = account
                    newAccounts += 1
                }
            }
            if (requests > 1 && newAccounts == 0) break

            val nextPage = pageData.nextPages.firstOrNull { it !in visitedPages } ?: break
            url = resellerPageUrl(nextPage)
        }

        return Result(accounts.values.toList(), requests)
    }

    fun resellerPageUrl(page: Int): String {
        return "https://new.plusx.tv/dealer.php?page=$page&items_pp=1000&exp_filter=&sort=login_asc"
    }

    private fun isLoginPage(html: String): Boolean {
        return html.contains("login.php", ignoreCase = true) &&
            html.contains("password", ignoreCase = true) &&
            !html.contains("table-card-row-wrapper", ignoreCase = true)
    }
}
