package metrik.project.domain.service.githubactions

import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class GithubUtilTest {

    @InjectMockKs
    private lateinit var githubUtil: GithubUtil

    @Test
    fun `should get repo and owner successfully from url`() {
        val url = "https://api.github.com/repos/test_owner/test_repo"

        assertEquals(
            Pair("test_owner", "test_repo"), githubUtil.getOwnerRepoFromUrl(url)
        )
    }

    @Test
    fun `should throw exception when parse wrong githubActions url`() {

        assertThrows(
            IndexOutOfBoundsException::class.java
        ) {
            githubUtil.getOwnerRepoFromUrl("https://api.github.com")
        }
    }

    @Test
    fun `should get well-formed bearer token`() {
        assertEquals(
            "Bearer 12345", githubUtil.getToken("12345")
        )
    }
}
