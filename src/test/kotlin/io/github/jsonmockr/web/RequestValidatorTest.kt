package io.github.jsonmockr.web

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.jsonmockr.configuration.InvalidRequestException
import io.github.jsonmockr.configuration.JsonMocaConfiguration
import io.github.jsonmockr.configuration.Resource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class RequestValidatorTest {

    private val configuration = mock(JsonMocaConfiguration::class.java)
    private val requestValidator = RequestValidator(configuration)
    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun setup() {
        `when`(configuration.getResourceData("test")).thenReturn(
            objectMapper.createArrayNode().apply {
                add(
                    objectMapper.createObjectNode().apply {
                        put("id", "ABC")
                        put("name", "John")
                        put("age", 12)
                        put("enabled", true)
                    },
                )
            },
        )
    }

    @Test
    fun `should not throw exception when fields are valid`() {
        requestValidator.validateRequest(
            Resource("test"),
            objectMapper.createObjectNode().apply {
                put("name", "Mary")
                put("age", 12)
                put("enabled", true)
            },
        )
    }

    @Test
    fun `should not throw exception when data is empty`() {
        `when`(configuration.getResourceData("test")).thenReturn(objectMapper.createArrayNode())
        requestValidator.validateRequest(
            Resource("test"),
            objectMapper.createObjectNode().apply {
                put("name", "Mary")
                put("age", 12)
                put("enabled", true)
            },
        )
    }

    @Test
    fun `should throw exception when fields have wrong type`() {
        assertThrows<InvalidRequestException> {
            requestValidator.validateRequest(
                Resource("test"),
                objectMapper.createObjectNode().apply {
                    put("name", "Mary")
                    put("age", 12)
                    put("enabled", "true")
                },
            )
        }
    }

    @Test
    fun `should throw exception when fields quantity do not match`() {
        assertThrows<InvalidRequestException> {
            requestValidator.validateRequest(
                Resource(name = "test", strictValidation = true),
                objectMapper.createObjectNode().apply {
                    put("name", "Mary")
                    put("age", 12)
                    put("enabled", true)
                    put("extra", true)
                },
            )
        }
    }
}
