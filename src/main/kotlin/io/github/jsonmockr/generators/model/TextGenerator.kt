package io.github.jsonmockr.generators.model

import com.fasterxml.jackson.databind.node.ArrayNode
import io.github.jsonmockr.generators.JsonMockrGenerator
import net.datafaker.Faker

class TextGenerator(private val length: Int, private val faker: Faker) : JsonMockrGenerator {
    override fun generate(data: Map<String, ArrayNode?>): Any = faker.text().text(length)
}
