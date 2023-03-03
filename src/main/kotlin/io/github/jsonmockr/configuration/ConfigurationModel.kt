package io.github.jsonmockr.configuration

import io.github.jsonmockr.Utils.DEFAULT_ID
import io.github.jsonmockr.Utils.DEFAULT_ID_LENGTH
import io.github.jsonmockr.configuration.Authorization.None

data class MockConfiguration(
    val defaults: Defaults?,
    val routes: List<Route> = listOf(),
    val resources: List<Resource> = emptyList(),
    val generators: List<Generator> = emptyList(),
)

data class Defaults(
    val envelope: String? = null,
    val idField: String? = null,
    val totalElements: String? = null,
    val idType: IdType? = null,
    val strictValidation: Boolean? = null,
    val createOnPut: Boolean? = null,
    val authorization: Authorization? = null,
    val idLength: Int? = null,
)

data class Route(
    val path: String,
    val authorization: Authorization? = None,
)

data class Resource(
    val name: String,
    val seedData: SeedData? = null,
    val generator: GeneratorConfig? = null,
    val strictValidation: Boolean? = false,
    val envelope: String? = null,
    val idField: String? = DEFAULT_ID,
    val totalElements: String? = null,
    val idType: IdType? = IdType.String,
    val createOnPut: Boolean? = false,
    val idLength: Int? = DEFAULT_ID_LENGTH,
)

data class GeneratorConfig(val name: String, val quantity: Int)
data class Generator(
    val spec: String,
    val name: String,
)

data class SeedData(
    val filePath: String,
    val jsonPointer: String? = null,
)

enum class Authorization {
    Bearer, Basic, None
}

enum class IdType {
    String, Number
}

class RouteNotFoundException : Exception()
class ResourceNotFoundException : Exception()
class InvalidRequestException : Exception()
class NoContentException : Exception()
class UnauthorizedException : Exception()
