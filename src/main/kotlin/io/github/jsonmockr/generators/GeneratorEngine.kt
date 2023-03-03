package io.github.jsonmockr.generators

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.readValue
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
    fun parse(spec: String): Map<String, JsonMocaGenerator> {
        val objectNode: ObjectNode = objectMapper.readValue(spec)
        return objectNode.fields().asSequence().toList().associate {
            it.key to engine.eval(it.value.asText().replace("#", "GeneratorFactory.").replace("'", "\""))
        } as Map<String, JsonMocaGenerator>
    }
}

object GeneratorFactory {
    fun text(length: Int = 0): JsonMocaGenerator = TextGenerator(length)
    fun number(length: Int = 0): JsonMocaGenerator = NumberGenerator(length)
    fun refs(resource: String, field: String): JsonMocaGenerator =
        ReferenceGenerator(resource, field)
}

interface JsonMocaGenerator {
    fun generate(data: Map<String, ArrayNode?>): Any?
}
