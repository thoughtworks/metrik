package fourkeymetrics.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.apache.logging.log4j.util.Strings
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component


@Component
@ConfigurationProperties(prefix = "mongodb")
class MongoDBProperties {
    var host: String = Strings.EMPTY
    var port: Int = 0
    var database: String = Strings.EMPTY
    var username: String = Strings.EMPTY
    var password: String = Strings.EMPTY
}

@Configuration
class MongoDBConfig {
    @Autowired
    private lateinit var mongoDBProperties: MongoDBProperties

    @Bean
    fun mongoClient(): MongoClient {
        val connectionString = ConnectionString(
                "mongodb://${mongoDBProperties.host}:${mongoDBProperties.port}/${mongoDBProperties.database}}")
        val mongoClientSettings: MongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .credential(MongoCredential.createCredential(
                        mongoDBProperties.username, mongoDBProperties.database,
                        mongoDBProperties.password.toCharArray()))
                .build()
        return MongoClients.create(mongoClientSettings)
    }

    @Bean
    fun mongoTemplate(): MongoTemplate {
        return MongoTemplate(mongoClient(), mongoDBProperties.database)
    }
}
