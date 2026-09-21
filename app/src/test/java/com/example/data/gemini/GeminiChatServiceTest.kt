package com.example.data.gemini

import com.example.data.repository.HouseRepository
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GeminiChatServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private var originalBaseUrl: String? = null

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        // Replace BASE_URL to hit MockWebServer since we made it internal var
        originalBaseUrl = GeminiChatService.BASE_URL
        GeminiChatService.BASE_URL = mockWebServer.url("/").toString().removeSuffix("/")

        // Inject fake API key to force network call
        GeminiChatService.testApiKey = "fake_key"
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()

        // Restore original BASE_URL and testApiKey
        if (originalBaseUrl != null) {
            GeminiChatService.BASE_URL = originalBaseUrl!!
        }
        GeminiChatService.testApiKey = null
    }

    @Test
    fun askAssistant_apiError_returnsFallbackMessage() = runTest {
        // Enqueue an error response (e.g. 500 Internal Server Error)
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        val query = "dummy query"
        // When there is an API error, if there is an instant answer, it returns it.
        // Otherwise, it returns the generic fallback message.
        val expectedInstant = HouseRepository.getInstantAnswer(query)
        val expectedFallback = expectedInstant
            ?: "Como anfitriã digital, informo que você encontra tudo no menu do app! Wi-Fi: ${HouseRepository.WIFI_SSID} (senha: ${HouseRepository.WIFI_PASSWORD}). Horário de silêncio: 22h às 7h."

        val actualWithKey = GeminiChatService.askAssistant(query)

        assertEquals(expectedFallback, actualWithKey)
    }
}
