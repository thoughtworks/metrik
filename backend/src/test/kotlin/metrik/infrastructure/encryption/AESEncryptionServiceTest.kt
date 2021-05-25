package metrik.infrastructure.encryption

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class AESEncryptionServiceTest {
    private lateinit var properties: AESEncryptionProperties
    private lateinit var aesEncryptionService: AESEncryptionService

    @BeforeEach
    internal fun setUp() {
        properties = AESEncryptionProperties("JaNdRgUkXp2s5v8y", "KbPeShVmYq3s6v9y")
        aesEncryptionService = AESEncryptionService(properties)
    }

    @Test
    internal fun `should encrypt`() {
        assertEquals(aesEncryptionService.encrypt("test"), "wbMbbtoNKyU6tiixRfSh+Q==")
    }

    @Test
    internal fun `should not encrypt null value`() {
        assertEquals(aesEncryptionService.encrypt(null), null)
    }

    @Test
    internal fun `should decrypt`() {
        assertEquals(aesEncryptionService.decrypt("wbMbbtoNKyU6tiixRfSh+Q=="), "test")
    }

    @Test
    internal fun `should not decrypt null value`() {
        assertEquals(aesEncryptionService.decrypt(null), null)
    }
}