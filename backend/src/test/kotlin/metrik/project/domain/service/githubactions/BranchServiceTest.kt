package metrik.project.domain.service.githubactions

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import metrik.project.domain.model.PipelineConfiguration
import metrik.project.infrastructure.github.feign.GithubFeignClient
import metrik.project.*
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class BranchServiceTest {
    @InjectMockKs
    private lateinit var branchService: BranchService

    @MockK
    private lateinit var githubFeignClient: GithubFeignClient

    private val testPipeline =
        PipelineConfiguration(id = "test pipeline", credential = "fake token", url = "https://test.com/test/test")

    @Test
    fun `should get all branches`() {
        every {
            githubFeignClient.retrieveBranches(
                credential = any(), owner = any(), repo = any()
            )
        } returns listOf(
            branch1,
            branch2
        )

        val branches = branchService.retrieveBranches(testPipeline)

        Assertions.assertThat(branches.size).isEqualTo(2)
        Assertions.assertThat(branches[0]).isEqualTo(branch1.name)
        Assertions.assertThat(branches[1]).isEqualTo(branch2.name)
    }
}


