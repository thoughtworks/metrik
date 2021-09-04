package metrik.project.infrastructure.github.feign

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component

class TokenAuthInterceptor(
    private val tokenHolder: TokenContextHolder
) : RequestInterceptor {

    override fun apply(template: RequestTemplate?) {
        template!!.header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenHolder.holder.get()}")
    }
}

@Component
class TokenContextHolder {
    val holder = ThreadLocal<String>()
}
