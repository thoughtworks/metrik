package metrik.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.transaction.TransactionManager

@Configuration
class MongoConfiguration {
    @Bean
    fun transactionManager(factory: MongoDatabaseFactory?): TransactionManager? {
        return MongoTransactionManager(factory!!)
    }
}