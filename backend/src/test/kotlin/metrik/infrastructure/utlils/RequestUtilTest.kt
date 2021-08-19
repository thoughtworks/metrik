package metrik.infrastructure.utlils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class RequestUtilTest {
    @Test
    fun `should build a bearer header`() {
        val header = RequestUtil.buildHeaders(mapOf(Pair("Authorization", "Bearer test")))

        assertThat(header["Authorization"]!![0]).isEqualTo("Bearer test")
    }

    @Test
    fun `should get domain with HTTP port`() {
        val domain = RequestUtil.getDomain("http://www.test.com")

        assertThat(domain).isEqualTo("http://www.test.com:80")
    }

    @Test
    fun `should get domain with HTTPS port`() {
        val domain = RequestUtil.getDomain("https://www.test.com")

        assertThat(domain).isEqualTo("https://www.test.com:443")
    }

    @Test
    fun `should get domain with customized port`() {
        val domain = RequestUtil.getDomain("http://www.test.com:8090")

        assertThat(domain).isEqualTo("http://www.test.com:8090")
    }

    @Test
    fun `should get domain with HTTPS protocol`() {
        val domain = RequestUtil.getDomain("https://www.test.com")

        assertThat(domain).isEqualTo("https://www.test.com:443")
    }
}
