package io.github.jsonmockr.generators.model

import com.fasterxml.jackson.databind.node.ArrayNode
import io.github.jsonmockr.generators.JsonMockrGenerator
import net.datafaker.Faker

class PriceGenerator(private val min: Double, private val max: Double, private val faker: Faker) : JsonMockrGenerator {
    override fun generate(data: Map<String, ArrayNode?>): Any = faker.commerce().price(min, max)
}
