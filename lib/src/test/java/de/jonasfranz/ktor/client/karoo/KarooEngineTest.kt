package de.jonasfranz.ktor.client.karoo

import de.jonasfranz.ktor.client.karoo.utils.mockEvent
import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.models.HardwareType
import io.hammerhead.karooext.models.HttpResponseState
import io.hammerhead.karooext.models.OnHttpResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

internal suspend fun HttpClient.runTestRequest() =
    get("https://example.com") {
        headers {
            append("Test", "test")
        }
    }

internal val testReponse =
    OnHttpResponse(
        HttpResponseState.Complete(
            statusCode = 200,
            headers =
                mapOf(
                    "Content-Size" to "Test".toByteArray().size.toString(),
                ),
            body = "Test".toByteArray(),
            error = null,
        ),
    )

@ExtendWith(MockKExtension::class)
class KarooEngineTest {
    @MockK
    lateinit var service: KarooSystemService

    private fun mockForTestRequest(response: OnHttpResponse) {
        service.mockEvent<OnHttpResponse.MakeHttpRequest> {
            when {
                it.url == "https://example.com" && it.headers["Test"] == "test" -> response
                else -> throw AssertionError("invalid request received: $it")
            }
        }
    }

    @Test
    fun `check if karoo engine checks if it connected`() =
        runTest {
            every { service.connected } returns false
            val client = service.toClient()
            assertThrows<KarooSystemNotConnectedException> { client.runTestRequest() }
        }

    @Test
    fun `check if karoo engine throws if running on K2`() =
        runTest {
            every { service.connected } returns true
            every { service.hardwareType } returns HardwareType.K2
            val client = service.toClient()
            assertThrows<KarooIsUnsupportedException> { client.runTestRequest() }
        }

    @Test
    fun `check if karoo engine can execute a valid request`() =
        runTest {
            mockForTestRequest(testReponse)
            val client = service.toClient()
            val response = client.runTestRequest()
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("Test", response.bodyAsText())
        }

    @Test
    fun `check if karoo engine throws on an invalid request`() =
        runTest {
            mockForTestRequest(testReponse)
            val client = service.toClient()
            assertThrows<KarooServiceException> { client.get("https://meoew") }
        }
}

internal fun KarooSystemService.toClient() = HttpClient(KarooEngine(KarooEngineConfig(this)))
