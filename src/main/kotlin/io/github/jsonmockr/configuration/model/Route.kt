package io.github.jsonmockr.configuration.model

data class Route(
    val path: String,
    val authorization: Authorization? = Authorization.None,
)
