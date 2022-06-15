package metrik.infrastructure.encryption

import metrik.project.domain.model.PipelineConfiguration
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.collections.ArrayList

@Configuration
data class AESEncryptionProperties(
    @Value("\${aes.iv}")
    val ivString: String,
    @Value("\${aes.key}")
    val keyString: String
)

@Component
class AESEncryptionService(@Autowired private var properties: AESEncryptionProperties) {
    private val key: SecretKey = getSecretKeyFromString(properties.keyString)
    private val iv: IvParameterSpec = IvParameterSpec(properties.ivString.toByteArray())
    private val algorithm = "AES/CBC/PKCS5Padding"

    fun encrypt(rawString: String?): String? {
        if (rawString == null) {
            return null
        }
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key, iv)
        val cipherText = cipher.doFinal(rawString.toByteArray())
        return Base64.getEncoder()
            .encodeToString(cipherText)
    }

    fun decrypt(cipherText: String?): String? {
        if (cipherText == null) {
            return null
        }
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, key, iv)
        val plainText = cipher.doFinal(
            Base64.getDecoder()
                .decode(cipherText)
        )
        return String(plainText)
    }

    private fun getSecretKeyFromString(key: String): SecretKey {
        return SecretKeySpec(key.encodeToByteArray(), 0, key.length, "AES")
    }
}

@Aspect
@Component
class DatabaseEncryptionAspect {
    @Autowired
    private lateinit var encryptionService: AESEncryptionService

    @Before(value = "execution(* org.springframework.data.mongodb.core.MongoTemplate.save(..)) && args(pipeline)")
    fun encryptBeforeSavingToDB(pipeline: PipelineConfiguration) {
        pipeline.username = encryptionService.encrypt(pipeline.username)
        pipeline.credential = encryptionService.encrypt(pipeline.credential)!!
    }

    @Before(
        "execution(* org.springframework.data.mongodb.core.MongoTemplate.insert(..)) && " +
            "args(pipelines, entityClass)"
    )
    fun <T> encryptBeforeSavingToDB(pipelines: ArrayList<T>, entityClass: Class<T>) {
        if (!entityClass.isAssignableFrom(PipelineConfiguration::class.java)) {
            return
        }
        pipelines.forEach {
            it as PipelineConfiguration
            it.username = encryptionService.encrypt(it.username)
            it.credential = encryptionService.encrypt(it.credential)!!
        }
    }

    @AfterReturning(
        pointcut = "execution(* org.springframework.data.mongodb.core.MongoTemplate.find(..)) && " +
            "args(query, entityClass)",
        returning = "pipelines"
    )
    fun <T> decryptAfterRetrievingFromDB(
        @Suppress("UnusedPrivateMember")
        query: Query,
        entityClass: Class<T>,
        pipelines: List<*>
    ) {
        if (!entityClass.isAssignableFrom(PipelineConfiguration::class.java)) {
            return
        }
        pipelines.forEach {
            it as PipelineConfiguration
            it.username = encryptionService.decrypt(it.username)
            it.credential = encryptionService.decrypt(it.credential)!!
        }
    }

    @AfterReturning(
        pointcut = "execution(* org.springframework.data.mongodb.core.MongoTemplate.findOne(..))",
        returning = "pipeline"
    )
    fun decryptAfterRetrievingFromDB(pipeline: PipelineConfiguration?) {
        if (pipeline != null) {
            pipeline.username = encryptionService.decrypt(pipeline.username)
            pipeline.credential = encryptionService.decrypt(pipeline.credential)!!
        }
    }
}
