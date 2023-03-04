package io.github.jsonmockr.web.handlers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.jsonmockr.Utils.DEFAULT_ID_LENGTH
import io.github.jsonmockr.Utils.generateRadomNumber
import io.github.jsonmockr.Utils.genrerateRandomString
import io.github.jsonmockr.Utils.isCollection
import io.github.jsonmockr.configuration.JsonMockrConfiguration
import io.github.jsonmockr.configuration.model.IdType.Number
import io.github.jsonmockr.configuration.model.Route
import io.github.jsonmockr.web.RequestValidator
import org.springframework.stereotype.Component

@Component
class PostRequestHandler(
    private val configuration: JsonMockrConfiguration,
    private val validator: RequestValidator,
) {
    fun post(
        route: Route,
        body: ObjectNode,
    ): JsonNode {
        return route.takeIf { isCollection(it.path) }?.let {
            val resource = configuration.getResourceFromRoutePath(route.path)
            val resourceData = configuration.getResourceData(resource.name)

            validator.validateRequest(resource, body)

            val newNode: ObjectNode = when (resource.idType) {
                Number -> body.put(
                    resource.idField,
                    generateRadomNumber(resource.idLength ?: DEFAULT_ID_LENGTH),
                )

                else -> body.put(
                    resource.idField,
                    genrerateRandomString(resource.idLength ?: DEFAULT_ID_LENGTH),
                )
            }

            configuration.updateResourceData(
                resource.name,
                resourceData.add(newNode),
            )
            body
        } ?: throw NotImplementedError()
    }
}
