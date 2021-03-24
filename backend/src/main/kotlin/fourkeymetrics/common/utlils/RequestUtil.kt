package fourkeymetrics.common.utlils

import org.springframework.http.HttpHeaders
import java.net.URL

object RequestUtil {
    private const val HTTP_PORT = 80
    private const val HTTPS_PORT = 443
    private const val UNDEFINED_PORT = -1

    fun buildHeaders(headers: Map<String, String>): HttpHeaders {
        val result = HttpHeaders()
        result.setAll(headers)
        return result
    }

    fun getDomain(pipelineURL: String): String {
        val url = URL(pipelineURL)

        var port = HTTP_PORT
        if (url.port != UNDEFINED_PORT) {
            port = url.port
        } else if (url.protocol.toLowerCase() == "https") {
            port = HTTPS_PORT
        }
        return "${url.protocol}://${url.host}:$port"
    }
}