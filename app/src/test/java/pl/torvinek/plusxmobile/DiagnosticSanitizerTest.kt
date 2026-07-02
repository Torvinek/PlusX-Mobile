package pl.torvinek.plusxmobile

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DiagnosticSanitizerTest {
    @Test
    fun removesHeadersKeysQueryValuesIpsAndForeignEmails() {
        val input = """
            Authorization: Bearer super-secret-token
            Cookie: PHPSESSID=abc123
            https://itvn.io/play.php?access_key=REALKEY&selected_user=user123
            <label>User Key</label><div class="vod-value">real_user_key_123456</div>
            password=myPassword
            IP: 192.168.0.22
            owner@example.com
            contact@example.org
        """.trimIndent()

        val cleaned = DiagnosticSanitizer.sanitize(input, "contact@example.org")

        listOf(
            "super-secret-token",
            "abc123",
            "REALKEY",
            "user123",
            "real_user_key_123456",
            "myPassword",
            "192.168.0.22",
            "owner@example.com"
        ).forEach { secret -> assertFalse(cleaned.contains(secret)) }

        assertTrue(cleaned.contains("contact@example.org"))
        assertTrue(cleaned.contains("[usunieto]"))
        assertTrue(cleaned.contains("[ip-usuniete]"))
        assertTrue(cleaned.contains("[email-usuniety]"))
    }
}
