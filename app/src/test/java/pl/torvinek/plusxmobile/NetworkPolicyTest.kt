package pl.torvinek.plusxmobile

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkPolicyTest {
    @Test
    fun acceptsOnlyExactHttpsHost() {
        assertTrue(NetworkPolicy.isHttpsHost("https://new.plusx.tv/index.php", "new.plusx.tv"))
        assertFalse(NetworkPolicy.isHttpsHost("http://new.plusx.tv/index.php", "new.plusx.tv"))
        assertFalse(NetworkPolicy.isHttpsHost("https://new.plusx.tv.example.org/index.php", "new.plusx.tv"))
        assertFalse(NetworkPolicy.isHttpsHost("https://evil.example/?next=new.plusx.tv", "new.plusx.tv"))
    }
}
