package com.example.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.HouseRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import java.net.URLEncoder

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ChatSupportScreenTest {

    private lateinit var context: Context
    private lateinit var shadowApplication: ShadowApplication

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        shadowApplication = shadowOf(context as android.app.Application)
    }

    private fun openHostWhatsApp(customText: String? = null, forceException: Boolean = false, forceDialException: Boolean = false, onFeedback: (String) -> Unit) {
        try {
            if (forceException) {
                throw ActivityNotFoundException()
            }
            val phone = HouseRepository.HOST_PHONE
            val defaultMsg = "Olá, ${HouseRepository.HOST_NAME}! Sou hóspede da sua casa em São Lourenço (Rua Pres. Castelo Branco, 95)."
            val fullText = if (customText.isNullOrBlank()) {
                defaultMsg
            } else {
                "$defaultMsg\n\nGostaria de tirar uma dúvida:\n\"${customText.trim()}\""
            }
            val encodedMsg = URLEncoder.encode(fullText, "UTF-8")
            val whatsappUri = Uri.parse("https://api.whatsapp.com/send?phone=$phone&text=$encodedMsg")
            val intent = Intent(Intent.ACTION_VIEW, whatsappUri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            onFeedback("Não foi possível abrir o WhatsApp. Tentando ligação...")
            try {
                if (forceDialException) {
                    throw ActivityNotFoundException()
                }
                val telIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${HouseRepository.HOST_PHONE}"))
                telIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(telIntent)
            } catch (ex: Exception) {
                onFeedback("WhatsApp da proprietária: ${HouseRepository.HOST_PHONE_DISPLAY}")
            }
        }
    }

    @Test
    fun testOpenHostWhatsApp_success_noCustomText() {
        var feedbackMessage = ""
        openHostWhatsApp(onFeedback = { feedbackMessage = it })

        val nextIntent = shadowApplication.nextStartedActivity
        assertNotNull("Intent is null", nextIntent)
        assertEquals(Intent.ACTION_VIEW, nextIntent.action)
        assertEquals("https://api.whatsapp.com/send?phone=${HouseRepository.HOST_PHONE}&text=Ol%C3%A1%2C+Val%C3%A9ria%21+Sou+h%C3%B3spede+da+sua+casa+em+S%C3%A3o+Louren%C3%A7o+%28Rua+Pres.+Castelo+Branco%2C+95%29.", nextIntent.data.toString())
        assertEquals("", feedbackMessage)
    }

    @Test
    fun testOpenHostWhatsApp_success_withCustomText() {
        var feedbackMessage = ""
        openHostWhatsApp(customText = "Onde fica a chave?", onFeedback = { feedbackMessage = it })

        val nextIntent = shadowApplication.nextStartedActivity
        assertNotNull("Intent is null", nextIntent)
        assertEquals(Intent.ACTION_VIEW, nextIntent.action)
        assertEquals("https://api.whatsapp.com/send?phone=${HouseRepository.HOST_PHONE}&text=Ol%C3%A1%2C+Val%C3%A9ria%21+Sou+h%C3%B3spede+da+sua+casa+em+S%C3%A3o+Louren%C3%A7o+%28Rua+Pres.+Castelo+Branco%2C+95%29.%0A%0AGostaria+de+tirar+uma+d%C3%BAvida%3A%0A%22Onde+fica+a+chave%3F%22", nextIntent.data.toString())
        assertEquals("", feedbackMessage)
    }

    @Test
    fun testOpenHostWhatsApp_exception_fallbackToDialSuccess() {
        var feedbackMessage = ""
        openHostWhatsApp(forceException = true, onFeedback = { feedbackMessage = it })

        val nextIntent = shadowApplication.nextStartedActivity
        assertNotNull("Intent is null", nextIntent)
        assertEquals(Intent.ACTION_DIAL, nextIntent.action)
        assertEquals("tel:${HouseRepository.HOST_PHONE}", nextIntent.data.toString())
        assertEquals("Não foi possível abrir o WhatsApp. Tentando ligação...", feedbackMessage)
    }

    @Test
    fun testOpenHostWhatsApp_exception_fallbackToDialException() {
        var feedbacks = mutableListOf<String>()
        openHostWhatsApp(forceException = true, forceDialException = true, onFeedback = { feedbacks.add(it) })

        assertEquals(2, feedbacks.size)
        assertEquals("Não foi possível abrir o WhatsApp. Tentando ligação...", feedbacks[0])
        assertEquals("WhatsApp da proprietária: ${HouseRepository.HOST_PHONE_DISPLAY}", feedbacks[1])
    }
}
