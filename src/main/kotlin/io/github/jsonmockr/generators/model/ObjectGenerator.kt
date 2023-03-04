package io.github.jsonmockr.generators.model

import com.fasterxml.jackson.databind.node.ArrayNode
import io.github.jsonmockr.generators.JsonMockrGenerator

data class ObjectGenerator(private val fields: Map<String, JsonMockrGenerator>) : JsonMockrGenerator {
    override fun generate(data: Map<String, ArrayNode?>): Any {
        return fields.map {
            it.key to it.value.generate(data)
        }.toMap()
    }
}
