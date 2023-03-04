package io.github.jsonmockr.web

import com.fasterxml.jackson.databind.node.ArrayNode
import io.github.jsonmockr.configuration.JsonMockrConfiguration
import io.github.jsonmockr.configuration.model.Defaults
import io.github.jsonmockr.configuration.model.Generator
import io.github.jsonmockr.configuration.model.Resource
import io.github.jsonmockr.configuration.model.Route
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/mockr-debug"], produces = [MediaType.APPLICATION_JSON_VALUE])
class DebugController(val configuration: JsonMockrConfiguration) {

    @GetMapping("/routes")
    fun getRoutes(): List<Route> = configuration.getRoutes()

    @GetMapping("/resources")
    fun getResources(): List<Resource> = configuration.getResources()

    @GetMapping("/resources/{name}")
    fun getResourceByName(@PathVariable name: String): ArrayNode = configuration.getResourceData(name)

    @GetMapping("/defaults")
    fun getDefaults(): Defaults? = configuration.getDefaults()

    @GetMapping("/generators")
    fun getGenerators(): List<Generator> = configuration.getGenerators()
}
