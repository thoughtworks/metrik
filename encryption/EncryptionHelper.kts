package fourkeymetrics.common.encryption

import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class AESEncryptionHelper {
    private var keyString: String?
    private var ivString: String?
    private var key: SecretKey
    private var iv: IvParameterSpec
    private val algorithm = "AES/CBC/PKCS5Padding"

    init {
        print("Please enter key, you can find it for every env in src/main/resources, or press ENTER to use default value for local:")
        keyString = readLine()
        if (keyString == "") {
            keyString = "&E)H@MbQeThWmZq4"
        }
        println("Key: $keyString")
        print("Please enter IV, you can find it for every env in src/main/resources, or press ENTER to use default value for local:")
        ivString = readLine()
        if (ivString == "") {
            ivString = "D(G+KbPeShVkYp3s"
        }
        println("IV: $ivString")

        this.key = getSecretKeyFromString(keyString!!)
        this.iv = IvParameterSpec(ivString!!.toByteArray())
    }

    fun encrypt(rawString: String): String {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key, iv)
        val cipherText = cipher.doFinal(rawString.toByteArray())
        return Base64.getEncoder()
            .encodeToString(cipherText)
    }

    private fun getSecretKeyFromString(key: String): SecretKey {
        return SecretKeySpec(key.encodeToByteArray(), 0, key.length, "AES")
    }
}

val helper = AESEncryptionHelper()
println()
while (true) {
    print("Please string need to be encrypted: ")
    val rawString = readLine()
    println(helper.encrypt(rawString!!))
}
