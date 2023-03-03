package io.github.jsonmockr.karate

import com.intuit.karate.junit5.Karate

abstract class MocaKarateTest(private val serverPort: Int) {
    fun runFeatureTestsFromPath(file: String): Karate {
        return Karate.run("src/test/resources/features/$file")
            .systemProperty("apiUrl", "http://localhost:$serverPort")
    }
}
