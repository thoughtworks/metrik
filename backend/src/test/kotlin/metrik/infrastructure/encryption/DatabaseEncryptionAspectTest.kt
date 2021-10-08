package metrik.infrastructure.encryption

import com.ninjasquad.springmockk.MockkBean
import io.mockk.Called
import io.mockk.every
import io.mockk.verify
import metrik.project.domain.model.Execution
import metrik.project.domain.model.Pipeline
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo

@SpringBootTest
internal class DatabaseEncryptionAspectTest {
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @MockkBean
    private lateinit var encryptionService: AESEncryptionService

    @BeforeEach
    fun setUp() {
        every { encryptionService.encrypt(any()) } returns "encrypted"
        every { encryptionService.decrypt(any()) } returns "decrypted"

        mongoTemplate.dropCollection("pipeline")
    }

    @Test
    fun `should encrypt data before call save() method in mongoTemplate for pipeline data`() {
        val username = "mock-username"
        val credential = "mock-credential"

        val pipelineSaved = mongoTemplate.save(Pipeline(username = username, credential = credential))

        verify(exactly = 1) { encryptionService.encrypt(username) }
        verify(exactly = 1) { encryptionService.encrypt(credential) }
        assertThat(pipelineSaved.username).isEqualTo("encrypted")
        assertThat(pipelineSaved.credential).isEqualTo("encrypted")
    }

    @Test
    fun `should not encrypt data before call save() method in mongoTemplate for non pipeline data`() {
        mongoTemplate.save(Execution())

        verify { encryptionService wasNot Called }
    }

    @Test
    fun `should encrypt data before call insert() method in mongoTemplate for pipeline data`() {
        val username = "mock-username"
        val credential = "mock-credential"

        val pipelinesSaved = mongoTemplate.insert(
            mutableListOf(Pipeline(username = username, credential = credential)),
            Pipeline::class.java
        ).toList()

        verify(exactly = 1) { encryptionService.encrypt(username) }
        verify(exactly = 1) { encryptionService.encrypt(credential) }
        assertThat(pipelinesSaved[0].username).isEqualTo("encrypted")
        assertThat(pipelinesSaved[0].credential).isEqualTo("encrypted")
    }

    @Test
    fun `should not encrypt data before call insert() method in mongoTemplate for non pipeline data`() {
        mongoTemplate.insert(
            mutableListOf(Execution()),
            Execution::class.java
        ).toList()

        verify { encryptionService wasNot Called }
    }

    @Test
    fun `should decrypt data after call find() method in mongoTemplate for pipeline data only`() {
        val username = "mock-username"
        val credential = "mock-credential"
        val projectId = "fake-project"

        mongoTemplate.save(Pipeline(projectId = projectId, username = username, credential = credential))

        val pipelinesRetrieved = mongoTemplate.find(
            Query().addCriteria(Criteria.where("projectId").isEqualTo(projectId)),
            Pipeline::class.java
        )

        verify(exactly = 2) { encryptionService.decrypt("encrypted") }
        assertThat(pipelinesRetrieved[0].username).isEqualTo("decrypted")
        assertThat(pipelinesRetrieved[0].credential).isEqualTo("decrypted")
    }

    @Test
    fun `should not decrypt data after call find() method in mongoTemplate for non pipeline data`() {
        mongoTemplate.save(Execution(pipelineId = "1"))

        mongoTemplate.find(
            Query().addCriteria(Criteria.where("pipelineId").isEqualTo("1")),
            Execution::class.java
        )

        verify { encryptionService wasNot Called }
    }

    @Test
    fun `should decrypt data after call findOne() method in mongoTemplate for pipeline data only`() {
        val username = "mock-username"
        val credential = "mock-credential"
        val projectId = "fake-project"

        mongoTemplate.save(Pipeline(projectId = projectId, username = username, credential = credential))

        val pipelineRetrieved = mongoTemplate.findOne(
            Query().addCriteria(Criteria.where("projectId").isEqualTo(projectId)),
            Pipeline::class.java
        )

        verify(exactly = 2) { encryptionService.decrypt("encrypted") }
        assertThat(pipelineRetrieved!!.username).isEqualTo("decrypted")
        assertThat(pipelineRetrieved.credential).isEqualTo("decrypted")
    }

    @Test
    fun `should not decrypt data after call findOne() method in mongoTemplate for non pipeline data`() {
        mongoTemplate.save(Execution(pipelineId = "1"))

        mongoTemplate.findOne(
            Query().addCriteria(Criteria.where("projectId").isEqualTo("1")),
            Pipeline::class.java
        )

        verify { encryptionService wasNot Called }
    }

    @Test
    fun `should not decrypt data after call findOne() method in mongoTemplate when it returns null`() {
        mongoTemplate.findOne(
            Query().addCriteria(Criteria.where("pipelineId").isEqualTo("1")),
            Pipeline::class.java
        )

        verify { encryptionService wasNot Called }
    }
}
