package io.github.jsonmockr.web.handlers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.jsonmockr.Utils.isCollection
import io.github.jsonmockr.configuration.JsonMocaConfiguration
import io.github.jsonmockr.configuration.NoContentException
import io.github.jsonmockr.configuration.Route
import org.springframework.stereotype.Component

@Component
class DeleteRequestHandler(
    private val getRequestHandler: GetRequestHandler,
    private val configuration: JsonMocaConfiguration,
    private val objectMapper: ObjectMapper,
) {
    fun delete(
        route: Route,
        requestPath: String,
    ): JsonNode {
        return route.takeIf { !isCollection(it.path) }?.let {
            val objectNode: ObjectNode = getRequestHandler.get(route, requestPath) as ObjectNode
            val resource = configuration.getResourceFromRoutePath(route.path)
            val resourceData = configuration.getResourceData(resource.name)

            configuration.updateResourceData(
                resource.name,
                objectMapper.createArrayNode()
                    .addAll(resourceData.filter { it[resource.idField] != objectNode[resource.idField] }),
            )

            throw NoContentException()
        } ?: throw NotImplementedError()
    }
}
