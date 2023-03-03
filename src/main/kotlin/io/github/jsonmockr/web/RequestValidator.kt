package io.github.jsonmockr.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.jsonmockr.Utils
import io.github.jsonmockr.configuration.InvalidRequestException
import io.github.jsonmockr.configuration.JsonMockrConfiguration
import io.github.jsonmockr.configuration.Resource
import org.springframework.stereotype.Component

@Component
class RequestValidator(private val configuration: JsonMockrConfiguration) {

    private val cache = mutableMapOf<String, Map<String, JsonNodeType>>()
    fun validateRequest(resource: Resource, body: ObjectNode) {
        val template = cache[resource.name] ?: configuration.getResourceData(resource.name).firstOrNull()
            ?.let {
                templateFromJsonObject(it as ObjectNode)
                    .filter { it.key != resource.idField }
                    .also {
                        cache[resource.name] = it
                    }
            }

        template?.let {
            val request = body.fields().asSequence()
                .filter { it.key != resource.idField }
                .map { it.key to it.value }
                .toMap()

            validateRequest(
                resource.idField ?: Utils.DEFAULT_ID,
                resource.strictValidation ?: false,
                template,
                request,
            )
        }
    }

    private fun templateFromJsonObject(jsonObject: ObjectNode): Map<String, JsonNodeType> {
        return jsonObject.fields().asSequence().map {
            it.key to it.value.nodeType
        }.toMap()
    }

    private fun validateRequest(
        idField: String,
        strictValidation: Boolean,
        template: Map<String, JsonNodeType>,
        request: Map<String, JsonNode>,
    ) {
        if (strictValidation && template.isNotEmpty()) {
            val bodySize = request.filter {
                it.key != idField
            }.size

            val templateSize = template.filter {
                it.key != idField
            }.size

            if (bodySize != templateSize) throw InvalidRequestException()
        }

        template.all { entry ->
            request[entry.key]?.let { validateType(it, entry.value) } ?: false
        }.takeIf { it } ?: throw InvalidRequestException()
    }

    private fun validateType(node: JsonNode, nodeType: JsonNodeType): Boolean {
        return node.nodeType == nodeType
    }
}
