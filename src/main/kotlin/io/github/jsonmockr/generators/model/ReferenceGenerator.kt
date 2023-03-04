package io.github.jsonmockr.generators.model

import com.fasterxml.jackson.databind.node.ArrayNode
import io.github.jsonmockr.generators.JsonMockrGenerator

class ReferenceGenerator(private val resource: String, private val field: String) : JsonMockrGenerator {
    override fun generate(data: Map<String, ArrayNode?>): Any? {
        return data[resource]?.map {
            it.fields().asSequence().filter { it.key == field }.map { it.value }.toList()
        }?.flatten()?.random()
    }
}
