package io.github.jsonmockr.web.handlers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.jsonmockr.Utils.isCollection
import io.github.jsonmockr.configuration.IdType.Number
import io.github.jsonmockr.configuration.JsonMockrConfiguration
import io.github.jsonmockr.configuration.Route
import io.github.jsonmockr.web.RequestValidator
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher

@Component
class PutRequestHandler(
    val configuration: JsonMockrConfiguration,
    val getRequestHandler: GetRequestHandler,
    val validator: RequestValidator,
    val objectMapper: ObjectMapper,
) {
    fun put(
        route: Route,
        requestPath: String,
        body: ObjectNode,
    ): JsonNode {
        return route.takeIf { !isCollection(it.path) }?.let {
            val resource = configuration.getResourceFromRoutePath(route.path)
            val resourceData = configuration.getResourceData(resource.name)

            val objectNode: ObjectNode = resource.createOnPut?.takeIf { it }?.let {
                objectMapper.createObjectNode()
            } ?: getRequestHandler.get(route, requestPath) as ObjectNode

            validator.validateRequest(resource, body)

            val pathVariables = AntPathMatcher().extractUriTemplateVariables(route.path, requestPath)
            val tmpNode = objectNode.setAll<ObjectNode>(body)

            val jsonNode: ObjectNode = when (resource.idType) {
                Number -> tmpNode.put(
                    resource.idField,
                    pathVariables[resource.idField]?.toInt(),
                )

                else -> tmpNode.put(
                    resource.idField,
                    pathVariables[resource.idField],
                )
            }

            configuration.updateResourceData(
                resource.name,
                objectMapper.createArrayNode().addAll(
                    resourceData.filter { it[resource.idField] != objectNode[resource.idField] },
                ).add(jsonNode),
            )

            tmpNode
        } ?: throw NotImplementedError()
    }
}
