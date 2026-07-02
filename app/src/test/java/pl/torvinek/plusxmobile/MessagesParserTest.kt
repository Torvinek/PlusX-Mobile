package pl.torvinek.plusxmobile

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MessagesParserTest {
    @Test
    fun parserReturnsThreeSeparateMessageCardsWithoutDashboardText() {
        val messages = ServerMessagesParser.parse(
            """
            <html><body>
              <aside>Navigation Home dns Sorting channels</aside>
              <div class="info-box">Your balance 12.68 EUR Your IP address 185.234.241.64</div>
              <section class="dashboard">DASHBOARD help forum bookmark</section>
              <div class="message-card green">
                <span>NOWOŚĆ</span><span>4 CZERWCA 2026</span>
                <h3>Nowe kanały w ofercie!</h3>
                <p>Do listy dodaliśmy nowe kanały: Kanał Zero.</p>
                <div><b>Informacja:</b> Kanały są już dostępne.</div>
              </div>
              <div class="message-card blue">
                <span>UPDATE</span><span>12 MAJA 2026</span>
                <h3>Optymalizacja CDN 1</h3>
                <p>Zmieniliśmy trasę dla kanałów.</p>
                <div><b>Instrukcja:</b> Zmiana nastąpi automatycznie.</div>
              </div>
              <div class="message-card orange">
                <span>MAINTENANCE</span><span>10 KWIETNIA 2026</span>
                <h3>VOD: Przerwa techniczna</h3>
                <p>Sekcja VOD jest tymczasowo niedostępna.</p>
                <div><b>Status prac:</b> Brak dokładnego terminu.</div>
              </div>
            </body></html>
            """.trimIndent()
        )

        assertEquals(3, messages.size)
        assertEquals("Nowe kanały w ofercie!", messages[0].title)
        assertEquals("Optymalizacja CDN 1", messages[1].title)
        assertEquals("VOD: Przerwa techniczna", messages[2].title)
        val allText = messages.joinToString(" ") { "${it.title} ${it.body}" }
        assertFalse(allText.contains("DASHBOARD"))
        assertFalse(allText.contains("12.68"))
        assertFalse(allText.contains("185.234.241.64"))
        assertFalse(allText.contains("dns"))
        assertTrue(messages.all { it.extraLabel != null })
    }

    @Test
    fun parserDecodesNamedPolishHtmlEntities() {
        val messages = ServerMessagesParser.parse(
            """
            <div class="message-card">
              <span>UPDATE</span>
              <h3>TVP Mi&lstrok;o&sacute;&cacute;, Remonty TV</h3>
              <p>Dost&eogon;pne na wszystkich playlistach.</p>
            </div>
            """.trimIndent()
        )

        assertEquals("TVP Miłość, Remonty TV", messages.first().title)
        assertTrue(messages.first().body.contains("Dostępne"))
    }
}
