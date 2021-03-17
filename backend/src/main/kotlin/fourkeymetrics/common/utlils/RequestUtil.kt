package fourkeymetrics.common.utlils

import org.springframework.http.HttpHeaders
import java.net.URL

object RequestUtil {
    private const val HTTP_DEFAULT_PORT = 80

    fun buildBearerHeader(credential: String): HttpHeaders {
        val headers = HttpHeaders()
        headers.setBearerAuth(credential)
        return headers
    }

    fun getDomain(pipelineURL: String): String {
        val url = URL(pipelineURL)
        val port = if (url.port == -1) {
            HTTP_DEFAULT_PORT
        } else {
            url.port
        }
        return "${url.protocol}://${url.host}:$port"
    }
}