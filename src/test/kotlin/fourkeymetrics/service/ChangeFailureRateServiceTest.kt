package fourkeymetrics.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fourkeymetrics.model.Build
import fourkeymetrics.repository.BuildRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Import(ChangeFailureRateService::class, ObjectMapper::class)
class ChangeFailureRateServiceTest {
    @Autowired
    private lateinit var changeFailureRateService: ChangeFailureRateService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var buildRepository: BuildRepository

    /**
     * test file: builds-cfr.json
     * build 1 : 1606780800, SUCCESS (deploy to prod)
     * build 2 : 1609459200, FAILED (deploy to prod)
     * build 3 : 1610668800, SUCCESS (deploy to prod)
     * build 4 : 1611964800, SUCCESS (deploy to prod)
     * build 5 : 1611974800, SUCCESS (deploy to uat)
     */
    @Test
    fun `should get change failure rate within time range given 3 valid build inside when calculate CFR`() {
        val pipelineId = "1"
        val targetStage = "deploy to prod"
        // build 2 - build 5
        val startTimestamp = 1609459200L
        val endTimestamp = 1611994800L

        val mockBuildList: List<Build> =
            objectMapper.readValue(this.javaClass.getResource("/service/builds-cfr.json").readText())

        `when`(buildRepository.getAllBuilds(pipelineId)).thenReturn(mockBuildList)

        assertThat(
            changeFailureRateService.getChangeFailureRate(
                pipelineId,
                targetStage,
                startTimestamp,
                endTimestamp
            )
        ).isEqualTo(1/3F)
    }

    /**
     * test file: builds-cfr.json
     * build 1 : 1606780800, SUCCESS (deploy to prod)
     * build 2 : 1609459200, FAILED (deploy to prod)
     * build 3 : 1610668800, SUCCESS (deploy to prod)
     * build 4 : 1611964800, SUCCESS (deploy to prod)
     * build 5 : 1611974800, SUCCESS (deploy to uat)
     */
    @Test
    fun `should get zero within time range given no valid build when calculate CFR`() {
        val pipelineId = "1"
        val targetStage = "deploy to prod"
        // build 5
        val startTimestamp = 1611974800L
        val endTimestamp = 1611974800L

        val mockBuildList: List<Build> =
            objectMapper.readValue(this.javaClass.getResource("/service/builds-cfr.json").readText())

        `when`(buildRepository.getAllBuilds(pipelineId)).thenReturn(mockBuildList)

        assertThat(
            changeFailureRateService.getChangeFailureRate(
                pipelineId,
                targetStage,
                startTimestamp,
                endTimestamp
            )
        ).isEqualTo(0F)
    }
}