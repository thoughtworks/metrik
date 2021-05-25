package metrik.project.domain.model

import org.apache.logging.log4j.util.Strings
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document


@Document(collection = "project")
data class Project(
    @Id
    val id: String = Strings.EMPTY,
    var name: String = Strings.EMPTY,
    val synchronizationTimestamp: Long? = null,
)