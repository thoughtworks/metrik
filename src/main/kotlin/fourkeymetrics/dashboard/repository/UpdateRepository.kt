package fourkeymetrics.dashboard.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component

@Component
class UpdateRepository {

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    private val collectionName = "update"

    fun save(record: UpdateRecord) {
        mongoTemplate.save(record, collectionName)
    }

    fun getLastUpdate(dashboardId: String): UpdateRecord? {
        return mongoTemplate.findAll(UpdateRecord::class.java, collectionName)
            .filter { it.dashboardId == dashboardId }
            .maxByOrNull { it.updateTimestamp }
    }
}


data class UpdateRecord(val dashboardId: String, val updateTimestamp: Long)

