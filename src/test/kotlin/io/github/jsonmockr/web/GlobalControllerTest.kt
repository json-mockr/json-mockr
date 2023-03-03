package io.github.jsonmockr.web

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.jsonmockr.configuration.Authorization
import io.github.jsonmockr.configuration.InvalidRequestException
import io.github.jsonmockr.configuration.JsonMockrConfiguration
import io.github.jsonmockr.configuration.Route
import io.github.jsonmockr.configuration.UnauthorizedException
import io.github.jsonmockr.web.handlers.DeleteRequestHandler
import io.github.jsonmockr.web.handlers.GetRequestHandler
import io.github.jsonmockr.web.handlers.PostRequestHandler
import io.github.jsonmockr.web.handlers.PutRequestHandler
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class GlobalControllerTest {

    private val getRequestHandler = mock(GetRequestHandler::class.java)
    private val putRequestHandler: PutRequestHandler = mock(PutRequestHandler::class.java)
    private val postRequestHandler = mock(PostRequestHandler::class.java)
    private val deleteRequestHandler = mock(DeleteRequestHandler::class.java)
    private val configuration = mock(JsonMockrConfiguration::class.java)
    private val httpServletRequest = mock(HttpServletRequest::class.java)

    private val globalController = GlobalController(
        getRequestHandler,
        postRequestHandler,
        putRequestHandler,
        deleteRequestHandler,
        configuration,
    )

    private val path = "/users"
    private val route = Route(path)

    @BeforeEach
    fun setup() {
        `when`(httpServletRequest.servletPath).thenReturn(path)
        `when`(configuration.getRouteFromPath(path)).thenReturn(route)
    }

    @Test
    fun `should throw 404 with required bearer auth`() {
        assertThrows<UnauthorizedException> {
            `when`(httpServletRequest.method).thenReturn("GET")
            `when`(configuration.getRouteFromPath("/users")).thenReturn(
                Route(
                    "/users",
                    authorization = Authorization.Bearer,
                ),
            )
            globalController.entrypoint(httpServletRequest, ObjectMapper().createObjectNode(), "")
        }
    }

    @Test
    fun `should throw 404 with wrong auth`() {
        assertThrows<UnauthorizedException> {
            `when`(httpServletRequest.method).thenReturn("GET")
            `when`(configuration.getRouteFromPath("/users")).thenReturn(
                Route(
                    "/users",
                    authorization = Authorization.Bearer,
                ),
            )
            globalController.entrypoint(httpServletRequest, ObjectMapper().createObjectNode(), "Basic ")
        }
    }

    @Test
    fun `should not throw 404 with required basic auth`() {
        `when`(httpServletRequest.method).thenReturn("GET")
        `when`(configuration.getRouteFromPath("/users")).thenReturn(
            Route(
                "/users",
                authorization = Authorization.Basic,
            ),
        )
        globalController.entrypoint(httpServletRequest, ObjectMapper().createObjectNode(), "Basic ---")
    }

    @Test
    fun `should not throw 404 with required bearer auth`() {
        `when`(httpServletRequest.method).thenReturn("GET")
        `when`(configuration.getRouteFromPath("/users")).thenReturn(
            Route(
                "/users",
                authorization = Authorization.Bearer,
            ),
        )
        globalController.entrypoint(httpServletRequest, ObjectMapper().createObjectNode(), "Bearer ---")
    }

    @Test
    fun `should perform get`() {
        `when`(httpServletRequest.method).thenReturn("GET")
        globalController.entrypoint(httpServletRequest, ObjectMapper().createObjectNode(), "")
        verify(getRequestHandler, times(1)).get(route, "/users")
    }

    @Test
    fun `should perform post`() {
        `when`(httpServletRequest.method).thenReturn("POST")
        val body = ObjectMapper().createObjectNode()
        globalController.entrypoint(httpServletRequest, body, "")
        verify(postRequestHandler, times(1)).post(route, body)
    }

    @Test
    fun `post should throw InvalidRequestException with null body`() {
        `when`(httpServletRequest.method).thenReturn("POST")
        assertThrows<InvalidRequestException> {
            globalController.entrypoint(httpServletRequest, null, "")
        }
    }

    @Test
    fun `put should throw InvalidRequestException with null body`() {
        `when`(httpServletRequest.method).thenReturn("PUT")
        assertThrows<InvalidRequestException> {
            globalController.entrypoint(httpServletRequest, null, "")
        }
    }

    @Test
    fun `should perform put`() {
        `when`(httpServletRequest.method).thenReturn("PUT")
        val body = ObjectMapper().createObjectNode()
        globalController.entrypoint(httpServletRequest, body, "")
        verify(putRequestHandler, times(1)).put(route, "/users", body)
    }

    @Test
    fun `should perform delete`() {
        `when`(httpServletRequest.method).thenReturn("DELETE")
        globalController.entrypoint(httpServletRequest, null, "")
        verify(deleteRequestHandler, times(1)).delete(route, "/users")
    }

    @Test
    fun `should throw not implemented`() {
        `when`(httpServletRequest.method).thenReturn("PATCH")
        assertThrows<NotImplementedError> {
            globalController.entrypoint(httpServletRequest, null, "")
        }
    }
}
