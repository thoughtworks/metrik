package metrik

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
@EnableAspectJAutoProxy
class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}