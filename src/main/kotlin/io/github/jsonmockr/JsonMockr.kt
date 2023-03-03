package io.github.jsonmockr

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@SpringBootApplication
class JsonMockr {

    @Bean("yaml")
    fun yamlMapper() = ObjectMapper(YAMLFactory()).apply {
        findAndRegisterModules()
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        propertyNamingStrategy = PropertyNamingStrategies.KEBAB_CASE
    }

    @Bean("json")
    @Primary
    fun jsonMapper() = ObjectMapper().apply {
        findAndRegisterModules()
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        propertyNamingStrategy = PropertyNamingStrategies.LOWER_CAMEL_CASE
    }
}

fun main(args: Array<String>) {
    runApplication<JsonMockr>(*args)
}
