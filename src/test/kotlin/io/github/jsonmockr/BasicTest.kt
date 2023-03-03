package io.github.jsonmockr

import com.intuit.karate.junit5.Karate
import io.github.jsonmockr.karate.MocaKarateTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.TestPropertySource

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(
    properties = [
        "json-mockr.config-file=classpath:configuration/basic.yaml",
    ],
)
class BasicTest(@LocalServerPort val serverPort: Int) : MocaKarateTest(serverPort) {
    @Karate.Test
    fun `run basic tests`(): Karate {
        return runFeatureTestsFromPath("basic.feature")
    }
}
