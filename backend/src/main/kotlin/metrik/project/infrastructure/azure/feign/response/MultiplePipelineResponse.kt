package metrik.project.infrastructure.azure.feign.response

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class MultiplePipelineResponse(
    val count: Int,
    val value: List<Value>
)

data class Value(
    val _links: Links,
    val folder: String,
    val id: Int,
    val name: String,
    val revision: Int,
    val url: String
)

data class Links(
    val self: Self,
    val web: Web
)

data class Self(
    val href: String
)

data class Web(
    val href: String
)