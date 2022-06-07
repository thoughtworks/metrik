package metrik.project.domain.repository

import metrik.infrastructure.utlils.toTimestamp
import metrik.project.domain.model.Commit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class CommitRepository {
    companion object {
        private const val PROP_PIPELINE_ID: String = "pipelineId"
        private const val PROP_COMMIT_ID = "commitId"
    }

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    private val collectionName = "commit"

    fun getTheLatestCommit(pipelineId: String): Commit? {
        val query = Query()
        query.addCriteria(
            Criteria
                .where(PROP_PIPELINE_ID).`is`(pipelineId)
        )
        val commits = mongoTemplate.find(
            query,
            Commit::class.java,
            collectionName
        )
        if (commits.isEmpty()) {
            return null
        }

        return commits.sortedByDescending { it.timestamp }[0]
    }

    fun findByTimePeriod(pipelineId: String, startTimestamp: Long, endTimestamp: Long): List<Commit> {
        val query = Query().addCriteria(
            Criteria
                .where(PROP_PIPELINE_ID).`is`(pipelineId)
        )
        val commits = mongoTemplate.find(
            query,
            Commit::class.java,
            collectionName
        )

        return commits.sortedBy { it.timestamp }.filter { it.timestamp in startTimestamp..endTimestamp }
    }

    fun hasDuplication(githubCommits: List<metrik.project.domain.service.githubactions.GithubCommit>): Boolean {
        return githubCommits.any {
            val query = Query().addCriteria(
                Criteria
                    .where(PROP_COMMIT_ID).`is`(it.id)
            )
            mongoTemplate.findOne(query, Commit::class.java, collectionName) != null
        }
    }

    fun save(pipelineId: String, allGithubCommits: MutableList<metrik.project.domain.service.githubactions.GithubCommit>) {
        allGithubCommits.parallelStream()
            .map { Commit(commitId = it.id, timestamp = it.timestamp.toTimestamp(), pipelineId = pipelineId) }
            .forEach { save(pipelineId, it) }
    }

    private fun save(pipelineId: String, commit: Commit) {
        val query = Query().addCriteria(
            Criteria
                .where(PROP_PIPELINE_ID).`is`(pipelineId)
                .and(PROP_COMMIT_ID).`is`(commit.commitId)
        )
        val found = mongoTemplate.findAndReplace(
            query,
            commit,
            collectionName
        )
        if (found == null) {
            mongoTemplate.save(commit, collectionName)
        }
    }
}
