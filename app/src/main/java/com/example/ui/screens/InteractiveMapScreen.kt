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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FloorPlanCategory
import com.example.data.model.FloorPlanPoint
import com.example.data.model.LocationCategory
import com.example.data.model.MapLocation
import com.example.data.model.MapViewMode

/**
 * Tela do Mapa da Cidade de São Lourenço & Bairro Ramon:
 * Exclusivamente dedicada ao mapa externo da cidade, pontos de interesse,
 * rotas turísticas e transporte, sem a planta interna da casa.
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

    val filteredLocations = remember(locations, activeCategory) {
        if (activeCategory == null) locations
        else locations.filter { it.category == activeCategory }
    }

    fun openInGoogleMaps(lat: Double, lng: Double, address: String) {
        try {
            val gmmIntentUri = Uri.parse("geo:$lat,$lng?q=${Uri.encode(address)}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            context.startActivity(mapIntent)
        } catch (e: Exception) {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
            )
            context.startActivity(webIntent)
        }
    }

    fun copyToClipboard(label: String, content: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, content)
        clipboard.setPrimaryClip(clip)
        onFeedback("$label copiado com sucesso!")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("interactive_map_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Cabeçalho e Filtros do Mapa da Cidade
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Mapa de São Lourenço",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Bairro Ramon & Principais Pontos da Cidade",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Badge indicando total de locais mapeados
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${filteredLocations.size} locais",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Chips de Categorias da Cidade
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

                    // Carrossel de seleção rápida de locais da cidade
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(filteredLocations) { loc ->
                            val isLocSelected = selectedLocation?.id == loc.id
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isLocSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onSelectLocation(loc) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = when (loc.category) {
                                            LocationCategory.HOUSE -> Icons.Default.Home
                                            LocationCategory.SERVICES -> Icons.Default.Delete
                                            LocationCategory.TRANSPORT -> Icons.Default.DirectionsBus
                                            LocationCategory.FOOD -> Icons.Default.Restaurant
                                            LocationCategory.ATTRACTION -> Icons.Default.Explore
                                        },
                                        contentDescription = null,
                                        tint = if (isLocSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = loc.title,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = if (isLocSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isLocSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Área do Mapa Interativo da Cidade
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

                // Legenda de Pontos Principais
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        LegendRow(color = Color(0xFFC85A32), label = "Sua Casa (Castelo Branco, 95)")
                        LegendRow(color = Color(0xFF3B6955), label = "Le Sapé (Ponto Referência)")
                        LegendRow(color = Color(0xFF1098AD), label = "Parada de Ônibus (110m)")
                        LegendRow(color = Color(0xFF1C7ED6), label = "Turismo & Águas")
                    }
                }
            }
        }

        // Card Inferior com Detalhes do Local Selecionado
        AnimatedVisibility(
            visible = selectedLocation != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(14.dp)
        ) {
            if (selectedLocation != null) {
                LocationBottomCard(
                    location = selectedLocation,
                    onClose = { onSelectLocation(null) },
                    onCopy = { copyToClipboard("Endereço", selectedLocation.address) },
                    onNavigate = { openInGoogleMaps(selectedLocation.latitude, selectedLocation.longitude, selectedLocation.address) }
                )
            }
        }
    }
}

@Composable
private fun LocationBottomCard(
    location: MapLocation,
    onClose: () -> Unit,
    onCopy: () -> Unit,
    onNavigate: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("location_detail_card"),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
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
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = location.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = location.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Fechar")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = location.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (location.tip != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
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

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Directions,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Distância da casa: ${location.distanceEstimate}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCopy,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copiar", fontSize = 13.sp)
                }

                Button(
                    onClick = onNavigate,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.weight(1.3f)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Como Chegar", fontSize = 13.sp)
                }
            }
        }
    }
}

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
            "parque_aguas" to Pair(0.70f, 0.70f),
            "quinta_cedro" to Pair(0.20f, 0.22f),
            "trem_aguas" to Pair(0.78f, 0.58f),
            "balonismo" to Pair(0.85f, 0.30f),
            "morro_cruzeiro" to Pair(0.28f, 0.82f),
            "rodoviaria" to Pair(0.84f, 0.76f)
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
            drawStylizedNeighborhood(pinOffsets)
        }

        locations.forEach { loc ->
            val coords = pinOffsets[loc.id] ?: Pair(0.5f, 0.5f)
            val isSelected = selectedLocation?.id == loc.id
            val pinColor = when (loc.id) {
                "house" -> Color(0xFFC85A32)
                "trash_bin" -> Color(0xFF2B8A3E)
                "sape" -> Color(0xFF3B6955)
                "bus_stop" -> Color(0xFF1098AD)
                "parque_aguas" -> Color(0xFF1C7ED6)
                "quinta_cedro" -> Color(0xFFD9480F)
                "trem_aguas" -> Color(0xFF7048E8)
                "balonismo" -> Color(0xFFF59F00)
                "morro_cruzeiro" -> Color(0xFF37B24D)
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
                            imageVector = when (location.category) {
                                LocationCategory.HOUSE -> Icons.Default.Home
                                LocationCategory.SERVICES -> Icons.Default.Delete
                                LocationCategory.TRANSPORT -> Icons.Default.DirectionsBus
                                LocationCategory.FOOD -> Icons.Default.Restaurant
                                LocationCategory.ATTRACTION -> Icons.Default.Explore
                            },
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
                        text = if (location.id == "house") "★ A CASA" else location.title.take(15),
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

private fun DrawScope.drawStylizedNeighborhood(pinOffsets: Map<String, Pair<Float, Float>>) {
    val w = size.width
    val h = size.height

    drawRect(color = Color(0xFFE9EFE8), size = size)

    // Colinas verdes / Parque das Águas
    val parkBrush = Brush.radialGradient(
        colors = listOf(Color(0xFFC7E2C9), Color(0xFFE2EDE2)),
        center = Offset(w * 0.70f, h * 0.70f),
        radius = w * 0.28f
    )
    drawCircle(
        brush = parkBrush,
        radius = w * 0.28f,
        center = Offset(w * 0.70f, h * 0.70f)
    )

    // Lago Mineral no Parque das Águas
    drawCircle(
        color = Color(0xFFA5D8F3),
        radius = w * 0.10f,
        center = Offset(w * 0.74f, h * 0.73f)
    )

    // Quinta do Cedro e colinas ao norte
    drawRoundRect(
        color = Color(0xFFD6EAD8),
        topLeft = Offset(w * 0.05f, h * 0.08f),
        size = Size(w * 0.35f, h * 0.22f),
        cornerRadius = CornerRadius(40f, 40f)
    )

    val streetColor = Color(0xFFFFFFFF)
    val streetBorderColor = Color(0xFFD4DAD4)

    // Avenida Principal
    val mainRoad = Path().apply {
        moveTo(w * 0.10f, h * 0.44f)
        lineTo(w * 0.90f, h * 0.42f)
    }
    drawPath(mainRoad, color = streetBorderColor, style = Stroke(width = 32f, cap = StrokeCap.Round))
    drawPath(mainRoad, color = streetColor, style = Stroke(width = 24f, cap = StrokeCap.Round))

    // Rua Pres. Castelo Branco
    val houseStreet = Path().apply {
        moveTo(w * 0.25f, h * 0.44f)
        lineTo(w * 0.45f, h * 0.44f)
        lineTo(w * 0.50f, h * 0.34f)
    }
    drawPath(houseStreet, color = streetBorderColor, style = Stroke(width = 24f, cap = StrokeCap.Round))
    drawPath(houseStreet, color = streetColor, style = Stroke(width = 18f, cap = StrokeCap.Round))

    // Rua do Le Sapé
    val sapeStreet = Path().apply {
        moveTo(w * 0.45f, h * 0.44f)
        lineTo(w * 0.68f, h * 0.42f)
    }
    drawPath(sapeStreet, color = Color(0xFFFFE3D6), style = Stroke(width = 20f, cap = StrokeCap.Round))

    // Rota a pé da casa até a parada de ônibus
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
            modifier = Modifier.size(10.dp)
        ) {}
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
