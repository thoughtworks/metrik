package fourkeymetrics.datasource

import org.springframework.stereotype.Component

@Component
class UpdateRepository {
    fun add() {
        TODO("Not yet implemented")
    }

    fun save(record: UpdateRecord) {
        TODO("Not yet implemented")
    }

    fun getLastUpdate(): UpdateRecord? {
        TODO("Not yet implemented")
    }
}


data class UpdateRecord(val updateTimestamp: Long)

