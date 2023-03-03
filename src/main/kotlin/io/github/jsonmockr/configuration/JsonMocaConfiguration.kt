package io.github.jsonmockr.configuration

import com.fasterxml.jackson.core.JsonPointer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.jsonmockr.Utils
import io.github.jsonmockr.Utils.DEFAULT_ID
import io.github.jsonmockr.configuration.Authorization.None
import io.github.jsonmockr.generators.GeneratorParser
import io.github.jsonmockr.generators.ObjectGenerator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher

@Component
class JsonMocaConfiguration(
    @Value("\${json-mockr.config-file}")
    private val configurationFile: String,
    resourceLoader: ResourceLoader,
    @Qualifier("yaml")
    private val yamlMapper: ObjectMapper,
    val jsonMapper: ObjectMapper,
    val generatorParser: GeneratorParser,
) {

    private val configuration =
        yamlMapper.readValue(
            resourceLoader.getResource(configurationFile).inputStream
                .takeIf { it.available() > 0 }
                ?.bufferedReader()
                ?.readText() ?: "nothing:",
            MockConfiguration::class.java,
        )

    private val generators = configuration.generators.associate {
        it.name to ObjectGenerator(generatorParser.parse(it.spec))
    }

    // TODO refactor
    private val database: MutableMap<String, ArrayNode> =
        configuration.resources.fold<Resource, Map<String, ArrayNode>>(mapOf()) { acc, resource ->
            val (resourceName, resourceData) = resource.name to
                (
                    resource.seedData?.let {
                        jsonMapper.readValue<JsonNode>(
                            resourceLoader.getResource(it.filePath).inputStream
                                .takeIf { it.available() > 0 }
                                ?.bufferedReader()
                                ?.readText() ?: "[]",
                        ).withArray(JsonPointer.valueOf(it.jsonPointer ?: ""))
                    } ?: run {
                        resource.generator?.let { gen ->
                            (0 until gen.quantity).map { generators[gen.name]?.generate(acc) ?: throw Exception() }
                        }.orEmpty().let { jsonMapper.valueToTree(it) }
                    } ?: jsonMapper.createArrayNode()
                    )
            acc + mapOf(resourceName to resourceData)
        }.toMutableMap()

    fun updateResourceData(resource: String, data: ArrayNode) {
        database[resource] = data
    }

    fun getResourceData(resource: String): ArrayNode {
        return database[resource] ?: jsonMapper.createArrayNode()
    }

    fun getRouteFromPath(path: String) =
        configuration.routes.find {
            AntPathMatcher().match(it.path, path)
        }?.let {
            it.copy(
                authorization = it.authorization ?: configuration.defaults?.authorization ?: None,
            )
        } ?: throw RouteNotFoundException()

    fun getResourceFromRoutePath(path: String): Resource {
        val lastSegment = Utils.LAST_SEGMENT_REGEX.toRegex().find(path)?.value.orEmpty()
        val name = path.replace(lastSegment, "").substringAfterLast("/")
        return configuration.resources.find { it.name == name }
            ?.let {
                it.copy(
                    strictValidation = it.strictValidation ?: configuration.defaults?.strictValidation ?: false,
                    envelope = it.envelope ?: configuration.defaults?.envelope,
                    createOnPut = it.createOnPut ?: configuration.defaults?.createOnPut ?: false,
                    idField = it.idField ?: configuration.defaults?.idField ?: DEFAULT_ID,
                    totalElements = it.totalElements ?: configuration.defaults?.totalElements,
                    idType = it.idType ?: configuration.defaults?.idType ?: IdType.String,
                    idLength = it.idLength ?: configuration.defaults?.idLength ?: Utils.DEFAULT_ID_LENGTH,
                )
            }
            ?: Resource(name)
    }
}
