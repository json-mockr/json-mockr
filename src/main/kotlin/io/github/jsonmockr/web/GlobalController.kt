package io.github.jsonmockr.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.jsonmockr.configuration.JsonMockrConfiguration
import io.github.jsonmockr.configuration.model.Authorization.Basic
import io.github.jsonmockr.configuration.model.Authorization.Bearer
import io.github.jsonmockr.configuration.model.InvalidRequestException
import io.github.jsonmockr.configuration.model.Route
import io.github.jsonmockr.web.exceptions.UnauthorizedException
import io.github.jsonmockr.web.handlers.DeleteRequestHandler
import io.github.jsonmockr.web.handlers.GetRequestHandler
import io.github.jsonmockr.web.handlers.PostRequestHandler
import io.github.jsonmockr.web.handlers.PutRequestHandler
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpMethod.PUT
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.util.MimeTypeUtils
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/{path:^.*(?!mockr-debug)}/**")
class GlobalController(
    private val getRequestHandler: GetRequestHandler,
    private val postRequestHandler: PostRequestHandler,
    private val putRequestHandler: PutRequestHandler,
    private val deleteRequestHandler: DeleteRequestHandler,
    private val configuration: JsonMockrConfiguration,
) {

    @RequestMapping(
        method = [RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE],
        produces = [MimeTypeUtils.APPLICATION_JSON_VALUE],
    )
    fun entrypoint(
        request: HttpServletRequest,
        @RequestBody
        body: ObjectNode?,
        @RequestHeader("Authorization") authorization: String?,
        @PathVariable path: String? = null
    ): ResponseEntity<JsonNode> {
        val requestPath = request.servletPath.trimEnd('/')
        val httpMethod = HttpMethod.valueOf(request.method.uppercase())
        val route = configuration.getRouteFromPath(requestPath)

        when (route.authorization) {
            Bearer -> authorization?.startsWith("${Bearer.name} ").takeIf { it == true }
                ?: throw UnauthorizedException()

            Basic -> authorization?.startsWith("${Basic.name} ").takeIf { it == true } ?: throw UnauthorizedException()
            else -> {}
        }

        return handleRequest(route, requestPath, httpMethod, body)
    }

    private fun handleRequest(
        route: Route,
        requestPath: String,
        httpMethod: HttpMethod,
        body: ObjectNode?,
    ): ResponseEntity<JsonNode> {
        return when (httpMethod) {
            GET -> ResponseEntity.status(OK)
                .body(getRequestHandler.get(route, requestPath))
            POST -> ResponseEntity.status(CREATED)
                .body(postRequestHandler.post(route, body ?: throw InvalidRequestException()))
            PUT -> putRequestHandler.put(route, requestPath, body ?: throw InvalidRequestException())
                .let { ResponseEntity.status(NO_CONTENT).build() }
            DELETE -> deleteRequestHandler.delete(route, requestPath)
                .let { ResponseEntity.status(NO_CONTENT).build() }
            else -> throw NotImplementedError()
        }
    }
}
