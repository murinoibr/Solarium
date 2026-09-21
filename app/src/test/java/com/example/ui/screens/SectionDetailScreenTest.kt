package com.example.ui.screens

import androidx.compose.ui.text.font.FontWeight
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SectionDetailScreenTest {

    @Test
    fun `parseMarkdownText handles plain text`() {
        val result = parseMarkdownText("Hello world")
        assertEquals("Hello world", result.text)
        assertTrue(result.spanStyles.isEmpty())
    }

    @Test
    fun `parseMarkdownText handles single bold block`() {
        val result = parseMarkdownText("Hello **bold** world")
        assertEquals("Hello bold world", result.text)

        val styles = result.spanStyles
        assertEquals(1, styles.size)
        assertEquals(FontWeight.Bold, styles[0].item.fontWeight)
        assertEquals(6, styles[0].start)
        assertEquals(10, styles[0].end)
    }

    @Test
    fun `parseMarkdownText handles multiple bold blocks`() {
        val result = parseMarkdownText("**Hello** normal **bold**")
        assertEquals("Hello normal bold", result.text)

        val styles = result.spanStyles
        assertEquals(2, styles.size)

        assertEquals(FontWeight.Bold, styles[0].item.fontWeight)
        assertEquals(0, styles[0].start)
        assertEquals(5, styles[0].end)

        assertEquals(FontWeight.Bold, styles[1].item.fontWeight)
        assertEquals(13, styles[1].start)
        assertEquals(17, styles[1].end)
    }

    @Test
    fun `parseMarkdownText handles bold at the beginning`() {
        val result = parseMarkdownText("**Bold** beginning")
        assertEquals("Bold beginning", result.text)

        val styles = result.spanStyles
        assertEquals(1, styles.size)
        assertEquals(FontWeight.Bold, styles[0].item.fontWeight)
        assertEquals(0, styles[0].start)
        assertEquals(4, styles[0].end)
    }

    @Test
    fun `parseMarkdownText handles bold at the end`() {
        val result = parseMarkdownText("Ending **bold**")
        assertEquals("Ending bold", result.text)

        val styles = result.spanStyles
        assertEquals(1, styles.size)
        assertEquals(FontWeight.Bold, styles[0].item.fontWeight)
        assertEquals(7, styles[0].start)
        assertEquals(11, styles[0].end)
    }

    @Test
    fun `parseMarkdownText handles unclosed bold tags by bolding the rest of the text`() {
        // Based on the implementation: `split("**")` will create 2 parts if there's 1 bold tag.
        // The first part is text, the second part will be bolded.
        val result = parseMarkdownText("Unclosed **bold")
        assertEquals("Unclosed bold", result.text)

        val styles = result.spanStyles
        assertEquals(1, styles.size)
        assertEquals(FontWeight.Bold, styles[0].item.fontWeight)
        assertEquals(9, styles[0].start)
        assertEquals(13, styles[0].end)
    }

    @Test
    fun `parseMarkdownText handles empty text`() {
        val result = parseMarkdownText("")
        assertEquals("", result.text)
        assertTrue(result.spanStyles.isEmpty())
    }
}
