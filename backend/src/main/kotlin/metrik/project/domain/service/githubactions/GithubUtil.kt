package metrik.project.domain.service.githubactions

import org.springframework.stereotype.Component
import java.net.URL

@Component
class GithubUtil {

    fun getOwnerRepoFromUrl(url: String): Pair<String, String> {
        val components = URL(url).path.split("/")
        val owner = components[components.size - ownerIndex]
        val repo = components.last()
        return Pair(owner, repo)
    }

    fun getToken(token: String) = "$tokenPrefix $token"

    private companion object {
        const val ownerIndex = 2
        const val tokenPrefix = "Bearer"
    }
}
