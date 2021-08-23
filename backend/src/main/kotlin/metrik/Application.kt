package metrik

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableFeignClients
class Application

fun main(args: Array<String>) {
    @Suppress("SpreadOperator")
    SpringApplication.run(Application::class.java, *args)
}
