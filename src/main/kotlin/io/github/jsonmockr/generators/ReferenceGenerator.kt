package io.github.jsonmockr.generators

import com.fasterxml.jackson.databind.node.ArrayNode

class ReferenceGenerator(private val resource: String, private val field: String) : JsonMocaGenerator {
    override fun generate(data: Map<String, ArrayNode?>): Any? {
        return data[resource]?.map {
            it.fields().asSequence().filter { it.key == field }.map { it.value }.toList()
        }?.flatten()?.random()
    }
}
