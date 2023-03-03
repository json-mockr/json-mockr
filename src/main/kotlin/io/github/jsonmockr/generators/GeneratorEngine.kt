package io.github.jsonmockr.generators

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.readValue
import net.datafaker.Faker
import org.springframework.stereotype.Component
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

@Component
class GeneratorParser(
    val objectMapper: ObjectMapper,
) {

    val engine: ScriptEngine = ScriptEngineManager().getEngineByExtension("kts").apply {
        eval("import io.github.jsonmockr.generators.GeneratorFactory")
    }
    fun parse(spec: String): Map<String, JsonMockrGenerator> {
        val objectNode: ObjectNode = objectMapper.readValue(spec)
        return objectNode.fields().asSequence().toList().associate {
            it.key to engine.eval(it.value.asText().replace("#", "GeneratorFactory.").replace("'", "\""))
        } as Map<String, JsonMockrGenerator>
    }
}

object GeneratorFactory {
    private val faker = Faker()
    fun text(length: Int = 0): JsonMockrGenerator = TextGenerator(length, faker)
    fun number(length: Int = 0): JsonMockrGenerator = NumberGenerator(length, faker)
    fun references(resource: String, field: String): JsonMockrGenerator =
        ReferenceGenerator(resource, field)

    fun name(): JsonMockrGenerator =
        NameGenerator(faker)
    fun fullName(): JsonMockrGenerator =
        FullNameGenerator(faker)

    fun email(): JsonMockrGenerator =
        EmailGenerator(faker)

    fun price(min: Int = 0, max: Int = 1000): JsonMockrGenerator =
        PriceGenerator(min.toDouble(), max.toDouble(), faker)
}

interface JsonMockrGenerator {
    fun generate(data: Map<String, ArrayNode?>): Any?
}
