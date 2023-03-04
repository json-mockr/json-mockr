package io.github.jsonmockr.web.handlers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.jsonmockr.Utils.DEFAULT_ID_SEPARATOR
import io.github.jsonmockr.Utils.getAllPossiblePaths
import io.github.jsonmockr.Utils.isCollection
import io.github.jsonmockr.configuration.JsonMockrConfiguration
import io.github.jsonmockr.configuration.model.Route
import io.github.jsonmockr.web.exceptions.ResourceNotFoundException
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher

@Component
class GetRequestHandler(
    private val configuration: JsonMockrConfiguration,
    private val objectMapper: ObjectMapper,
) {

    fun get(route: Route, requestPath: String): JsonNode {
        val resourceData = filterResourceData(
            route,
            requestPath,
        )
        val resource = configuration.getResourceFromRoutePath(route.path)
        return when (isCollection(route.path)) {
            true -> resource.envelope?.let {
                objectMapper.createObjectNode().apply {
                    set<ObjectNode>(it, resourceData)
                    resource.totalElements?.let {
                        put(it, resourceData.size())
                    }
                }
            } ?: resourceData

            false -> resourceData.firstOrNull() ?: throw ResourceNotFoundException()
        }
    }

    private fun filterResourceData(route: Route, requestPath: String): JsonNode =
        processAllPaths(getAllPossiblePaths(requestPath), configuration.getResourceFromRoutePath(route.path).name)

    private fun processAllPaths(paths: List<String>, resourceName: String): JsonNode =
        paths.foldIndexed(mutableMapOf<String, JsonNode>()) { idx, tmpDatabase, curr ->
            val (resource, data) = processSinglePath(curr, tmpDatabase)
            tmpDatabase[resource] =
                data.takeIf { !it.isEmpty || idx == paths.size - 1 } ?: throw ResourceNotFoundException()
            tmpDatabase
        }[resourceName] ?: throw ResourceNotFoundException()

    private fun processSinglePath(
        path: String,
        tmpDatabase: MutableMap<String, JsonNode>,
    ): Pair<String, JsonNode> {
        val route = configuration.getRouteFromPath(path)
        val resource = configuration.getResourceFromRoutePath(route.path)
        val pathVariables = AntPathMatcher().extractUriTemplateVariables(route.path, path)

        tmpDatabase[resource.name] = tmpDatabase[resource.name] ?: configuration.getResourceData(resource.name)

        pathVariables.entries.reversed().take(if (isCollection(route.path)) 1 else 2).forEach { filter ->
            val filtered = tmpDatabase[resource.name]?.filter { jsonNode ->
                val targetField = filter.key.split(DEFAULT_ID_SEPARATOR).fold(jsonNode) { acc, key ->
                    when (acc) {
                        is ObjectNode -> acc.get(key)
                        else -> acc
                    }
                }

                val filterAsString: String = objectMapper.readValue<JsonNode>(
                    objectMapper.writeValueAsString(mapOf(resource.idField to filter.value)),
                ).get(resource.idField).asText()

                targetField?.let {
                    targetField.asText().equals(filterAsString)
                } ?: false
            }

            tmpDatabase[resource.name] = objectMapper.createArrayNode().addAll(filtered)
        }

        return resource.name to (tmpDatabase[resource.name] ?: throw ResourceNotFoundException())
    }
}
