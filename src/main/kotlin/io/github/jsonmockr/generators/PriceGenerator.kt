package io.github.jsonmockr.generators

import com.fasterxml.jackson.databind.node.ArrayNode
import net.datafaker.Faker

class PriceGenerator(private val min: Double, private val max: Double, private val faker: Faker) : JsonMockrGenerator {
    override fun generate(data: Map<String, ArrayNode?>): Any = faker.commerce().price(min, max)
}
