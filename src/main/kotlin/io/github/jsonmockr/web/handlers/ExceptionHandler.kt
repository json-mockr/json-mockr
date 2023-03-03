package io.github.jsonmockr.web.handlers

import io.github.jsonmockr.configuration.InvalidRequestException
import io.github.jsonmockr.configuration.NoContentException
import io.github.jsonmockr.configuration.ResourceNotFoundException
import io.github.jsonmockr.configuration.RouteNotFoundException
import io.github.jsonmockr.configuration.UnauthorizedException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(value = [ResourceNotFoundException::class, RouteNotFoundException::class])
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    fun handleNotFoundException(
        request: WebRequest,
    ): ResponseEntity<Any> = ResponseEntity.status(HttpStatus.NOT_FOUND).build()

    @ExceptionHandler(value = [InvalidRequestException::class])
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    fun handleBadRequestException(
        request: WebRequest,
    ): ResponseEntity<Any> = ResponseEntity.status(HttpStatus.BAD_REQUEST).build()

    @ExceptionHandler(value = [NotImplementedError::class])
    @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
    fun handleNotAllowedException(
        request: WebRequest,
    ): ResponseEntity<Any> = ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build()

    @ExceptionHandler(value = [NoContentException::class])
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun handleNoContentException(
        request: WebRequest,
    ): ResponseEntity<Any> = ResponseEntity.status(HttpStatus.NO_CONTENT).build()

    @ExceptionHandler(value = [UnauthorizedException::class])
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    fun handleUnauthorizedException(
        request: WebRequest,
    ): ResponseEntity<Any> = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
}
