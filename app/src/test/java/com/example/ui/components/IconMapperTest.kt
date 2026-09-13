package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tv
import org.junit.Assert.assertEquals
import org.junit.Test

class IconMapperTest {

    @Test
    fun getSectionIcon_knownIcons_returnsCorrectVector() {
        assertEquals(Icons.Default.Favorite, getSectionIcon("Favorite"))
        assertEquals(Icons.Default.Place, getSectionIcon("Place"))
        assertEquals(Icons.Default.Key, getSectionIcon("Key"))
        assertEquals(Icons.Default.Tv, getSectionIcon("Tv"))
        assertEquals(Icons.Default.Bed, getSectionIcon("Bed"))
        assertEquals(Icons.Default.Restaurant, getSectionIcon("Restaurant"))
        assertEquals(Icons.Default.Shield, getSectionIcon("Shield"))
        assertEquals(Icons.Default.Delete, getSectionIcon("Delete"))
        assertEquals(Icons.Default.DirectionsCar, getSectionIcon("DirectionsCar"))
        assertEquals(Icons.Default.Explore, getSectionIcon("Explore"))
    }

    @Test
    fun getSectionIcon_unknownIcon_returnsFallbackVector() {
        assertEquals(Icons.AutoMirrored.Filled.Help, getSectionIcon("Unknown"))
        assertEquals(Icons.AutoMirrored.Filled.Help, getSectionIcon(""))
        assertEquals(Icons.AutoMirrored.Filled.Help, getSectionIcon("Random"))
    }
}
