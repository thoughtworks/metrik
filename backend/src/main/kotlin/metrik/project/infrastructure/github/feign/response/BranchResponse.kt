package metrik.project.infrastructure.github.feign.response;

import metrik.project.domain.service.githubactions.GithubBranch

import org.apache.logging.log4j.util.Strings

data class BranchResponse(
    val name: String = Strings.EMPTY,
)

