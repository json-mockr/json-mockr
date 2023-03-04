package io.github.jsonmockr.configuration.model

data class MockConfiguration(
    val defaults: Defaults?,
    val routes: List<Route> = listOf(),
    val resources: List<Resource> = emptyList(),
    val generators: List<Generator> = emptyList(),
)
