package fourkeymetrics.common.utlils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class RequestUtilTest {
    @Test
    internal fun `should build a bearer header`() {
        val header = RequestUtil.buildBearerHeader("test")

        assertThat(header["Authorization"]!![0]).isEqualTo("Bearer test")
    }

    @Test
    internal fun `should get domain with default port`() {
        val domain = RequestUtil.getDomain("http://www.test.com")

        assertThat(domain).isEqualTo("http://www.test.com:80")
    }

    @Test
    internal fun `should get domain with customized port`() {
        val domain = RequestUtil.getDomain("http://www.test.com:8090")

        assertThat(domain).isEqualTo("http://www.test.com:8090")
    }

    @Test
    internal fun `should get domain with https protocol`() {
        val domain = RequestUtil.getDomain("https://www.test.com")

        assertThat(domain).isEqualTo("https://www.test.com:80")
    }
}