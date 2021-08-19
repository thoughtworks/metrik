package metrik.project.domain.model

import org.apache.logging.log4j.util.Strings
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.net.URL

@Document(collection = "pipeline")
data class Pipeline(
    @Id
    val id: String = Strings.EMPTY,
    var projectId: String = Strings.EMPTY,
    val name: String = Strings.EMPTY,
    var username: String? = null,
    var credential: String = Strings.EMPTY,
    val url: String = Strings.EMPTY,
    var type: PipelineType = PipelineType.JENKINS,
) {
    val githubApiUrl: String
        get() =
            when (type) {
                PipelineType.GITHUB_ACTIONS -> {
                    val urls = URL(url).path.split("/")
                    val repo = urls.last()
                    val owner = urls[urls.size - 2]
                    "https://api.github.com/repos/$owner/$repo"
                }
                else -> url
            }
}
