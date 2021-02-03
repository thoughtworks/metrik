package fourkeymetrics.dashboard.model

import org.apache.logging.log4j.util.Strings
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document


@Document(collection = "dashboard")
data class Dashboard(
    @Id
    val id: String = Strings.EMPTY,
    var name: String = Strings.EMPTY,
    val synchronizationTimestamp: Long? = null,
)