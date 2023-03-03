package io.github.jsonmockr

import java.security.SecureRandom
import java.util.Base64
import kotlin.math.min
import kotlin.math.pow

object Utils {

    const val DEFAULT_ID = "id"
    const val DEFAULT_ID_LENGTH = 4
    const val DEFAULT_ID_SEPARATOR = "@"
    const val LAST_SEGMENT_REGEX = "/(\\{[@A-Za-z0-9_-]*}/?)\$"
    private const val MAX_ID_LENGTH = 20
    private val random: SecureRandom = SecureRandom()
    private val encoder: Base64.Encoder = Base64.getUrlEncoder().withoutPadding()

    fun genrerateRandomString(length: Int): String {
        val buffer = ByteArray(20)
        random.nextBytes(buffer)
        return encoder.encodeToString(buffer).substring(0, min(length, MAX_ID_LENGTH))
    }

    fun generateRadomNumber(length: Int): Int = (0 until 10.0.pow(length.toDouble()).toInt()).random()

    fun isCollection(path: String): Boolean {
        val lastSegment = LAST_SEGMENT_REGEX.toRegex().find(path)?.value.orEmpty()
        val resource = path.replace(lastSegment, "").substringAfterLast("/")
        val regex = "/(\\{[@A-Za-z0-9_-]*}/$resource?)\$".toRegex()
        return regex.containsMatchIn(path).takeIf { it || path == "/$resource" }?.let { true } ?: false
    }

    fun getAllPossiblePaths(path: String): List<String> {
        val parts = path.split("/").filter { it.isNotEmpty() }
        return parts.fold(listOf()) { acc, curr ->
            when (acc.isEmpty()) {
                true -> acc + "/$curr"
                false -> acc + (acc.last() + "/" + curr)
            }
        }
    }
}
