package io.github.jsonmockr.generators.model

import com.fasterxml.jackson.databind.node.ArrayNode
import io.github.jsonmockr.generators.JsonMockrGenerator
import net.datafaker.Faker

class NumberGenerator(private val length: Int, private val faker: Faker) : JsonMockrGenerator {
    override fun generate(data: Map<String, ArrayNode?>): Any = faker.number().randomNumber(length, false)
}
