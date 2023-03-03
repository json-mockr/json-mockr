package io.github.jsonmockr.generators

import com.fasterxml.jackson.databind.node.ArrayNode
import io.github.jsonmockr.Utils

class TextGenerator(private val length: Int) : JsonMocaGenerator {
    override fun generate(data: Map<String, ArrayNode?>): Any = Utils.genrerateRandomString(length)
}
