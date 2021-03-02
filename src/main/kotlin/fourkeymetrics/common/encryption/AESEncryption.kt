package fourkeymetrics.common.encryption

import fourkeymetrics.project.model.Pipeline
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Configuration
data class AESEncryptionProperties(
    @Value("\${aes.iv}")
    val ivString: String,
    @Value("\${aes.key}")
    val keyString: String
)

@Component
class AESEncryptionService(@Autowired private var properties: AESEncryptionProperties) {
    private lateinit var key: SecretKey
    private lateinit var iv: IvParameterSpec
    private val algorithm = "AES/CBC/PKCS5Padding"

    init {
        this.key = getSecretKeyFromString(properties.keyString)
        this.iv = IvParameterSpec(properties.ivString.toByteArray())
    }

    fun encrypt(rawString: String): String {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key, iv)
        val cipherText = cipher.doFinal(rawString.toByteArray())
        return Base64.getEncoder()
            .encodeToString(cipherText)
    }

    fun decrypt(cipherText: String): String {
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

    @AfterReturning(
        pointcut = "execution(* org.springframework.data.mongodb.core.MongoTemplate.find(..))",
        returning = "collection"
    )
    fun decryptAfterRetrievingFromDB(collection: ArrayList<Any>) {
        var pipelines: List<Pipeline> = emptyList()
        try {
            pipelines = collection.map { it as Pipeline }
        } catch (e: ClassCastException) {
        }

        pipelines.forEach {
            it.username = encryptionService.decrypt(it.username)
            it.credential = encryptionService.decrypt(it.credential)
        }
    }

    @AfterReturning(
        pointcut = "execution(* org.springframework.data.mongodb.core.MongoTemplate.findOne(..))",
        returning = "pipeline"
    )
    fun decryptAfterRetrievingFromDB(pipeline: Pipeline?) {
        if (pipeline != null) {
            pipeline.username = encryptionService.decrypt(pipeline.username)
            pipeline.credential = encryptionService.decrypt(pipeline.credential)
        }
    }
}