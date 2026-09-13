package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Icecream
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FloorPlanCategory
import com.example.data.model.FloorPlanPoint
import com.example.data.model.LocationCategory
import com.example.data.model.MapLocation
import com.example.data.model.MapViewMode
import java.util.Locale
import kotlin.math.abs

/**
 * Tela Aprimorada do Mapa de São Lourenço & Bairro Ramon:
 * Integrada com informações e rotas do Google Maps, coordenadas geográficas precisas,
 * Plus Codes, horários de funcionamento e navegação GPS a partir da casa.
 */
@Composable
fun InteractiveMapScreen(
    locations: List<MapLocation>,
    selectedLocation: MapLocation?,
    activeCategory: LocationCategory?,
    onSelectCategory: (LocationCategory?) -> Unit,
    onSelectLocation: (MapLocation?) -> Unit,
    onFeedback: (String) -> Unit,
    floorPoints: List<FloorPlanPoint> = emptyList(),
    selectedFloorPoint: FloorPlanPoint? = null,
    activeFloorCategory: FloorPlanCategory? = null,
    mapViewMode: MapViewMode = MapViewMode.NEIGHBORHOOD,
    floorSearchQuery: String = "",
    onSelectFloorPoint: (FloorPlanPoint?) -> Unit = {},
    onSelectFloorCategory: (FloorPlanCategory?) -> Unit = {},
    onChangeMapViewMode: (MapViewMode) -> Unit = {},
    onUpdateFloorSearch: (String) -> Unit = {},
    onNavigateToManual: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Mapa Interativo, 1 = Lista Detalhada

    val filteredLocations = remember(locations, activeCategory, searchQuery) {
        var list = locations
        if (activeCategory != null) {
            list = list.filter { it.category == activeCategory }
        }
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase(Locale.ROOT)
            list = list.filter { loc ->
                loc.title.lowercase(Locale.ROOT).contains(q) ||
                    loc.address.lowercase(Locale.ROOT).contains(q) ||
                    loc.description.lowercase(Locale.ROOT).contains(q) ||
                    loc.tags.any { it.lowercase(Locale.ROOT).contains(q) } ||
                    (loc.plusCode?.lowercase(Locale.ROOT)?.contains(q) == true)
            }
        }
        list
    }

    // Ações do Google Maps
    fun openGoogleMaps(location: MapLocation) {
        try {
            // Intent para abrir diretamente no app do Google Maps com pino e rótulo
            val uri = Uri.parse("geo:${location.latitude},${location.longitude}?q=${location.latitude},${location.longitude}(${Uri.encode(location.title)})")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps")
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            // Fallback para navegador web com a URL oficial do Google Maps
            val webUri = if (!location.googleMapsUrl.isNullOrBlank()) {
                Uri.parse(location.googleMapsUrl)
            } else {
                Uri.parse("https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}")
            }
            val webIntent = Intent(Intent.ACTION_VIEW, webUri)
            context.startActivity(webIntent)
        }
    }

    fun openGpsNavigation(location: MapLocation, travelMode: String = "driving") {
        try {
            // Rota traçada a partir da Casa (Estância Solarium, Castelo Branco 95: -22.112678, -45.056412) até o destino
            val uri = Uri.parse(
                "https://www.google.com/maps/dir/?api=1&origin=-22.112678,-45.056412&destination=${location.latitude},${location.longitude}&travelmode=$travelMode"
            )
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps")
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            val webUri = Uri.parse(
                "https://www.google.com/maps/dir/?api=1&origin=-22.112678,-45.056412&destination=${location.latitude},${location.longitude}&travelmode=$travelMode"
            )
            val webIntent = Intent(Intent.ACTION_VIEW, webUri)
            context.startActivity(webIntent)
        }
    }

    fun copyToClipboard(label: String, content: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, content)
        clipboard.setPrimaryClip(clip)
        onFeedback("$label copiado com sucesso!")
    }

    fun callPhoneNumber(phone: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phone.replace(Regex("[^0-9+]"), "")}"))
            context.startActivity(intent)
        } catch (_: Exception) {
            onFeedback("Não foi possível abrir o discador.")
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("interactive_map_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header e Painel de Controle
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)) {
                    // Título e Selo de Integração com Google Maps
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Mapa de São Lourenço",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF4285F4).copy(alpha = 0.12f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Place,
                                            contentDescription = null,
                                            tint = Color(0xFF4285F4),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "Google Maps",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Color(0xFF1A73E8)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Bairro Ramon & Circuito Hidromineral • Coordenadas Oficiais",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Badge com contagem
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "${filteredLocations.size} pontos",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Campo de Busca Rápida
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("map_search_input"),
                        placeholder = {
                            Text(
                                "Buscar ponto, rua ou atração...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Limpar busca",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Alternador entre "Mapa Gráfico" e "Lista com Google Maps"
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary,
                        divider = {}
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Mapa Interativo", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Lista de Locais", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Chips de Categorias
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = activeCategory == null,
                                onClick = { onSelectCategory(null) },
                                label = { Text("Todos", fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                        items(LocationCategory.entries.toTypedArray()) { cat ->
                            FilterChip(
                                selected = activeCategory == cat,
                                onClick = { onSelectCategory(cat) },
                                label = { Text(cat.label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Conteúdo: ou o Canvas do Mapa Gráfico ou a Lista Detalhada
            if (selectedTab == 0) {
                // Modo Mapa Visual com Canvas
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    NeighborhoodMapCanvas(
                        locations = filteredLocations,
                        selectedLocation = selectedLocation,
                        onPinTap = { onSelectLocation(it) },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Legenda no Canto Superior Direito
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                        shadowElevation = 3.dp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            LegendRow(color = Color(0xFFC85A32), label = "Sua Casa (Ramon)")
                            LegendRow(color = Color(0xFFE03131), label = "Emergência (Hospital/UPA)")
                            LegendRow(color = Color(0xFFE8590C), label = "Bares & Gastronomia")
                            LegendRow(color = Color(0xFF1C7ED6), label = "Pontos Turísticos")
                            LegendRow(color = Color(0xFF7048E8), label = "Trem / Eubiose")
                            LegendRow(color = Color(0xFF1098AD), label = "Ônibus & Transporte")
                        }
                    }

                    // Carrossel Rápido Inferior quando nenhum card está fixado
                    if (selectedLocation == null) {
                        Surface(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Text(
                                    text = "Toque em um local para ver coordenadas e rotas:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                                )
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                                ) {
                                    items(filteredLocations) { loc ->
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                            ),
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable { onSelectLocation(loc) }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = iconForCategory(loc.category, loc.id),
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Column {
                                                    Text(
                                                        text = loc.title,
                                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = loc.distanceEstimate,
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Modo Lista Detalhada com Coordenadas e Google Maps
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredLocations) { loc ->
                        LocationListItemCard(
                            location = loc,
                            onOpenMaps = { openGoogleMaps(loc) },
                            onNavigateGps = { openGpsNavigation(loc, "driving") },
                            onNavigateWalk = { openGpsNavigation(loc, "walking") },
                            onCopyCoords = { copyToClipboard("Coordenadas", "${loc.latitude}, ${loc.longitude}") },
                            onCopyPlusCode = { loc.plusCode?.let { copyToClipboard("Plus Code", it) } },
                            onCall = { loc.phone?.let { callPhoneNumber(it) } }
                        )
                    }
                }
            }
        }

        // Card Inferior com Detalhes Completos do Local Selecionado (no modo mapa)
        AnimatedVisibility(
            visible = selectedLocation != null && selectedTab == 0,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(14.dp)
        ) {
            if (selectedLocation != null) {
                EnhancedLocationBottomCard(
                    location = selectedLocation,
                    onClose = { onSelectLocation(null) },
                    onOpenMaps = { openGoogleMaps(selectedLocation) },
                    onNavigateGps = { openGpsNavigation(selectedLocation, "driving") },
                    onNavigateWalk = { openGpsNavigation(selectedLocation, "walking") },
                    onCopyAddress = { copyToClipboard("Endereço", selectedLocation.address) },
                    onCopyCoords = { copyToClipboard("Coordenadas", "${selectedLocation.latitude}, ${selectedLocation.longitude}") },
                    onCopyPlusCode = { selectedLocation.plusCode?.let { copyToClipboard("Plus Code", it) } },
                    onCall = { selectedLocation.phone?.let { callPhoneNumber(it) } }
                )
            }
        }
    }
}

/**
 * Card Inferior Expandido com Coordenadas, Plus Code, Google Maps e Traçar Rota
 */
@Composable
private fun EnhancedLocationBottomCard(
    location: MapLocation,
    onClose: () -> Unit,
    onOpenMaps: () -> Unit,
    onNavigateGps: () -> Unit,
    onNavigateWalk: () -> Unit,
    onCopyAddress: () -> Unit,
    onCopyCoords: () -> Unit,
    onCopyPlusCode: () -> Unit,
    onCall: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("location_detail_card"),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Cabeçalho com Categoria e Botão Fechar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = location.category.label,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    if (location.rating != null) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFF3BF)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFF59F00),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${location.rating} ★",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFE67700)
                                )
                                if (location.reviewsCount != null) {
                                    Text(
                                        text = " (${formatReviewCount(location.reviewsCount)})",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = Color(0xFF995400)
                                    )
                                }
                            }
                        }
                    }
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Fechar")
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Título e Endereço
            Text(
                text = location.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCopyAddress() }
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = location.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copiar endereço",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp)
                )
            }

            // Bloco de Coordenadas Geográficas e Plus Code
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "COORDENADAS GPS PRECISAS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = formatToDms(location.latitude, location.longitude),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Decimal: ${String.format(Locale.US, "%.6f, %.6f", location.latitude, location.longitude)}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = onCopyCoords,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copiar coordenadas",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    if (!location.plusCode.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCopyPlusCode() }
                        ) {
                            Text(
                                text = "Plus Code: ${location.plusCode}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1A73E8)
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copiar Plus Code",
                                tint = Color(0xFF1A73E8),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }

            // Descrição e Dica
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = location.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (location.openingHours != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = location.openingHours,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (location.tip != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = location.tip,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // Distância da casa
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Partindo da Casa: ${location.distanceEstimate}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (location.phone != null) {
                    OutlinedButton(
                        onClick = onCall,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Ligar", fontSize = 11.sp)
                    }
                }
            }

            // Botões de Ação do Google Maps
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botão Abrir no Google Maps
                OutlinedButton(
                    onClick = onOpenMaps,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ver no Maps", fontSize = 12.sp, maxLines = 1)
                }

                // Botão Traçar Rota GPS (Carro)
                Button(
                    onClick = onNavigateGps,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Traçar Rota", fontSize = 12.sp, maxLines = 1)
                }
            }
        }
    }
}

/**
 * Item de Lista Completo com Informações e Ações do Google Maps
 */
@Composable
private fun LocationListItemCard(
    location: MapLocation,
    onOpenMaps: () -> Unit,
    onNavigateGps: () -> Unit,
    onNavigateWalk: () -> Unit,
    onCopyCoords: () -> Unit,
    onCopyPlusCode: () -> Unit,
    onCall: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("location_list_card_${location.id}"),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Linha Superior com Categoria, Avaliação e Distância
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = location.category.label,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    if (location.rating != null) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFF3BF)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFF59F00),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${location.rating}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFE67700)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = location.distanceEstimate,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Título
            Text(
                text = location.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Endereço
            Text(
                text = location.address,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Coordenadas e Plus Code
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "GPS: ${String.format(Locale.US, "%.5f, %.5f", location.latitude, location.longitude)}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (!location.plusCode.isNullOrBlank()) {
                            Text(
                                text = "Plus Code: ${location.plusCode}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = Color(0xFF1A73E8)
                            )
                        }
                    }

                    IconButton(
                        onClick = onCopyCoords,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copiar coordenadas",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            // Descrição curta
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = location.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (location.openingHours != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = location.openingHours,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Botões de Ação
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenMaps,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Abrir Maps", fontSize = 11.sp)
                }

                Button(
                    onClick = onNavigateGps,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Traçar Rota", fontSize = 11.sp)
                }

                if (location.phone != null) {
                    IconButton(
                        onClick = onCall,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Call,
                            contentDescription = "Ligar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Canvas Geográfico com pontos precisos e estilizados de São Lourenço & Ramon
 */
@Composable
private fun NeighborhoodMapCanvas(
    locations: List<MapLocation>,
    selectedLocation: MapLocation?,
    onPinTap: (MapLocation) -> Unit,
    modifier: Modifier = Modifier
) {
    val pinOffsets = remember(locations) {
        mapOf(
            "house" to Pair(0.42f, 0.44f),
            "trash_bin" to Pair(0.36f, 0.46f),
            "sape" to Pair(0.55f, 0.42f),
            "bus_stop" to Pair(0.68f, 0.41f),
            "templo_eubiose" to Pair(0.48f, 0.24f),
            "quinta_cedro" to Pair(0.18f, 0.18f),
            "balonismo" to Pair(0.85f, 0.28f),
            "parque_aguas" to Pair(0.68f, 0.68f),
            "feirarte" to Pair(0.63f, 0.64f),
            "calcadao_gastronomico" to Pair(0.56f, 0.58f),
            "unique_cafes_store" to Pair(0.53f, 0.58f),
            "basilica_matriz" to Pair(0.58f, 0.51f),
            "cervejaria_antonieta" to Pair(0.63f, 0.51f),
            "circuito_das_cervejas" to Pair(0.50f, 0.53f),
            "pizzaria_agostini" to Pair(0.46f, 0.60f),
            "restaurante_casarao" to Pair(0.66f, 0.48f),
            "sorveteria_miro" to Pair(0.51f, 0.47f),
            "supermercado_centro" to Pair(0.50f, 0.66f),
            "trem_aguas" to Pair(0.78f, 0.58f),
            "morro_cruzeiro" to Pair(0.30f, 0.82f),
            "rota_cafe_unique" to Pair(0.70f, 0.82f),
            "bar_do_quinzinho" to Pair(0.58f, 0.86f),
            "rodoviaria" to Pair(0.84f, 0.76f),
            "hospital_sao_lourenco" to Pair(0.76f, 0.46f),
            "upa_sao_lourenco" to Pair(0.82f, 0.84f),
            "bombeiros_sao_lourenco" to Pair(0.80f, 0.78f),
            "policia_militar" to Pair(0.58f, 0.45f),
            "drogaria_raia" to Pair(0.52f, 0.52f)
        )
    }

    Box(
        modifier = modifier
            .background(Color(0xFFE8ECE9))
            .pointerInput(locations) {
                detectTapGestures { tapOffset ->
                    val w = size.width
                    val h = size.height
                    val hit = locations.find { loc ->
                        val coords = pinOffsets[loc.id] ?: Pair(0.5f, 0.5f)
                        val pinX = coords.first * w
                        val pinY = coords.second * h
                        val distSq = (tapOffset.x - pinX) * (tapOffset.x - pinX) + (tapOffset.y - pinY) * (tapOffset.y - pinY)
                        distSq <= (48 * density) * (48 * density)
                    }
                    if (hit != null) {
                        onPinTap(hit)
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawStylizedCityMap()
        }

        locations.forEach { loc ->
            val coords = pinOffsets[loc.id] ?: Pair(0.5f, 0.5f)
            val isSelected = selectedLocation?.id == loc.id
            val pinColor = when {
                loc.id == "house" -> Color(0xFFC85A32)
                loc.id == "hospital_sao_lourenco" || loc.id == "upa_sao_lourenco" -> Color(0xFFE03131)
                loc.id == "bombeiros_sao_lourenco" -> Color(0xFFD9480F)
                loc.id == "policia_militar" -> Color(0xFF1971C2)
                loc.id == "drogaria_raia" -> Color(0xFF0CA678)
                loc.id == "templo_eubiose" -> Color(0xFF7048E8)
                loc.id == "basilica_matriz" -> Color(0xFF4C6EF5)
                loc.id == "unique_cafes_store" || loc.id == "rota_cafe_unique" -> Color(0xFFD9480F)
                loc.id == "cervejaria_antonieta" || loc.id == "circuito_das_cervejas" || loc.id == "bar_do_quinzinho" -> Color(0xFFF08C00)
                loc.category == LocationCategory.EMERGENCY -> Color(0xFFE03131)
                loc.category == LocationCategory.FOOD -> Color(0xFFE8590C)
                loc.category == LocationCategory.ATTRACTION -> Color(0xFF1C7ED6)
                loc.category == LocationCategory.TRANSPORT -> Color(0xFF1098AD)
                loc.category == LocationCategory.SERVICES -> Color(0xFF2B8A3E)
                loc.category == LocationCategory.HOUSE -> Color(0xFFC85A32)
                else -> Color(0xFF495057)
            }

            Box(modifier = Modifier.fillMaxSize()) {
                MapPinView(
                    location = loc,
                    relativeX = coords.first,
                    relativeY = coords.second,
                    isSelected = isSelected,
                    pinColor = pinColor,
                    onClick = { onPinTap(loc) }
                )
            }
        }
    }
}

@Composable
private fun MapPinView(
    location: MapLocation,
    relativeX: Float,
    relativeY: Float,
    isSelected: Boolean,
    pinColor: Color,
    onClick: () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable(onClick = onClick)
                    .testTag("map_pin_${location.id}")
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = pinColor,
                    shadowElevation = if (isSelected) 8.dp else 3.dp,
                    modifier = Modifier.size(if (isSelected) 42.dp else 34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = iconForCategory(location.category, location.id),
                            contentDescription = location.title,
                            tint = Color.White,
                            modifier = Modifier.size(if (isSelected) 22.dp else 18.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.White.copy(alpha = 0.95f),
                    shadowElevation = 2.dp,
                    modifier = Modifier.padding(top = 3.dp)
                ) {
                    Text(
                        text = if (location.id == "house") "★ A CASA" else location.title.take(16),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
                        ),
                        color = if (isSelected) pinColor else Color(0xFF1E293B),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    ) { measurables, constraints ->
        val placeable = measurables.first().measure(constraints)
        val parentWidth = constraints.maxWidth
        val parentHeight = constraints.maxHeight

        val x = (relativeX * parentWidth - placeable.width / 2).toInt()
        val y = (relativeY * parentHeight - placeable.height).toInt()

        layout(parentWidth, parentHeight) {
            placeable.placeRelative(x = x, y = y)
        }
    }
}

private fun DrawScope.drawStylizedCityMap() {
    val w = size.width
    val h = size.height

    // Fundo do mapa
    drawRect(color = Color(0xFFE9EFE8), size = size)

    // Rio Verde (passa cortando São Lourenço)
    val riverPath = Path().apply {
        moveTo(w * 0.15f, h * 0.95f)
        cubicTo(
            w * 0.40f, h * 0.85f,
            w * 0.65f, h * 0.75f,
            w * 0.88f, h * 0.60f
        )
        lineTo(w * 0.98f, h * 0.52f)
    }
    drawPath(riverPath, color = Color(0xFFBBE4F7), style = Stroke(width = 18f, cap = StrokeCap.Round))

    // Área do Parque das Águas (Lago e Bosque)
    val parkBrush = Brush.radialGradient(
        colors = listOf(Color(0xFFC5E3C7), Color(0xFFDCEDDD)),
        center = Offset(w * 0.68f, h * 0.68f),
        radius = w * 0.22f
    )
    drawCircle(
        brush = parkBrush,
        radius = w * 0.22f,
        center = Offset(w * 0.68f, h * 0.68f)
    )

    // Lago Mineral no Parque das Águas
    drawCircle(
        color = Color(0xFF90D5F5),
        radius = w * 0.08f,
        center = Offset(w * 0.72f, h * 0.70f)
    )

    // Fazenda Quinta do Cedro (Norte)
    drawRoundRect(
        color = Color(0xFFD6EAD8),
        topLeft = Offset(w * 0.06f, h * 0.06f),
        size = Size(w * 0.32f, h * 0.20f),
        cornerRadius = CornerRadius(30f, 30f)
    )

    // Malha Viária
    val streetColor = Color(0xFFFFFFFF)
    val streetBorderColor = Color(0xFFD4DAD4)

    // Avenida Principal / Av. Dom Pedro II
    val mainRoad = Path().apply {
        moveTo(w * 0.08f, h * 0.44f)
        lineTo(w * 0.92f, h * 0.42f)
    }
    drawPath(mainRoad, color = streetBorderColor, style = Stroke(width = 30f, cap = StrokeCap.Round))
    drawPath(mainRoad, color = streetColor, style = Stroke(width = 22f, cap = StrokeCap.Round))

    // Rua Pres. Castelo Branco (Ramon)
    val houseStreet = Path().apply {
        moveTo(w * 0.25f, h * 0.44f)
        lineTo(w * 0.45f, h * 0.44f)
        lineTo(w * 0.50f, h * 0.34f)
    }
    drawPath(houseStreet, color = streetBorderColor, style = Stroke(width = 22f, cap = StrokeCap.Round))
    drawPath(houseStreet, color = streetColor, style = Stroke(width = 16f, cap = StrokeCap.Round))

    // Acesso ao Le Sapé e Ponto de Ônibus
    val sapeStreet = Path().apply {
        moveTo(w * 0.45f, h * 0.44f)
        lineTo(w * 0.68f, h * 0.42f)
    }
    drawPath(sapeStreet, color = Color(0xFFFFE3D6), style = Stroke(width = 18f, cap = StrokeCap.Round))

    // Conexão Ramon -> Centro / Parque das Águas
    val centerConnect = Path().apply {
        moveTo(w * 0.55f, h * 0.44f)
        lineTo(w * 0.60f, h * 0.58f)
        lineTo(w * 0.68f, h * 0.68f)
    }
    drawPath(centerConnect, color = streetBorderColor, style = Stroke(width = 20f, cap = StrokeCap.Round))
    drawPath(centerConnect, color = streetColor, style = Stroke(width = 14f, cap = StrokeCap.Round))

    // Rota a pé tracejada (Casa -> Ponto de Ônibus / Le Sapé)
    val walkPath = Path().apply {
        moveTo(w * 0.42f, h * 0.44f)
        lineTo(w * 0.55f, h * 0.42f)
        lineTo(w * 0.68f, h * 0.41f)
    }
    drawPath(
        walkPath,
        color = Color(0xFFC85A32),
        style = Stroke(
            width = 4f,
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
        )
    )
}

@Composable
private fun LegendRow(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = color,
            modifier = Modifier.size(9.dp)
        ) {}
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun iconForCategory(cat: LocationCategory, id: String = ""): ImageVector {
    return when {
        id == "trem_aguas" -> Icons.Default.Train
        id == "supermercado_centro" -> Icons.Default.ShoppingBag
        id == "hospital_sao_lourenco" || id == "upa_sao_lourenco" -> Icons.Default.LocalHospital
        id == "bombeiros_sao_lourenco" -> Icons.Default.LocalFireDepartment
        id == "policia_militar" -> Icons.Default.Security
        id == "drogaria_raia" -> Icons.Default.LocalPharmacy
        id == "cervejaria_antonieta" || id == "circuito_das_cervejas" || id == "bar_do_quinzinho" -> Icons.Default.LocalBar
        id == "unique_cafes_store" || id == "rota_cafe_unique" -> Icons.Default.LocalCafe
        id == "sorveteria_miro" -> Icons.Default.Icecream
        cat == LocationCategory.EMERGENCY -> Icons.Default.LocalHospital
        cat == LocationCategory.HOUSE -> Icons.Default.Home
        cat == LocationCategory.SERVICES -> Icons.Default.Delete
        cat == LocationCategory.TRANSPORT -> Icons.Default.DirectionsBus
        cat == LocationCategory.FOOD -> Icons.Default.Restaurant
        cat == LocationCategory.ATTRACTION -> Icons.Default.Explore
        else -> Icons.Default.Place
    }
}

private fun formatToDms(latitude: Double, longitude: Double): String {
    val latDms = toDms(latitude, isLatitude = true)
    val lngDms = toDms(longitude, isLatitude = false)
    return "$latDms, $lngDms"
}

private fun toDms(coordinate: Double, isLatitude: Boolean): String {
    val absCoord = abs(coordinate)
    val degrees = absCoord.toInt()
    val minutesDouble = (absCoord - degrees) * 60.0
    val minutes = minutesDouble.toInt()
    val seconds = (minutesDouble - minutes) * 60.0

    val direction = if (isLatitude) {
        if (coordinate >= 0) "N" else "S"
    } else {
        if (coordinate >= 0) "L" else "O"
    }

    return String.format(Locale.ROOT, "%d°%02d'%04.1f\"%s", degrees, minutes, seconds, direction)
}

private fun formatReviewCount(count: Int): String {
    return when {
        count >= 1000 -> String.format(Locale.ROOT, "%.1fk", count / 1000.0)
        else -> count.toString()
    }
}
