package metrik.configuration

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfiguration {
    @Bean
    fun caffeineConfiguration(): Caffeine<Any, Any> {
        return Caffeine.newBuilder().expireAfterAccess(EXPIRE_TIME_MINUTES, TimeUnit.MINUTES)
    }

    companion object {
        private const val EXPIRE_TIME_MINUTES = 10L
    }
}
