package com.example.ui.components

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.BuildConfig
import com.example.data.model.LocationCategory
import com.example.data.model.MapLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

/**
 * Coordenadas de referência de São Lourenço - MG
 */
val SAO_LOURENCO_CENTER = LatLng(-22.1158, -45.0535)
val CASA_SOLARIUM_COORDS = LatLng(-22.112678, -45.056412)

/**
 * Verifica se a chave configurada é uma chave do Google Cloud Maps válida.
 * Chaves do AI Studio (Gemini) começam com "AQ." e não possuem autorização para o Maps SDK for Android.
 * Chaves de template iniciam com "DEFAULT_" ou estão vazias.
 * Chaves oficiais do Google Cloud Platform iniciam com "AIza".
 */
fun isGoogleMapsSdkConfigured(): Boolean {
    val key = BuildConfig.MAPS_API_KEY
    return key.isNotBlank() && !key.startsWith("DEFAULT_") && !key.startsWith("AQ.") && key.startsWith("AIza")
}

@Composable
fun GoogleMapView(
    locations: List<MapLocation>,
    selectedLocation: MapLocation?,
    onSelectLocation: (MapLocation?) -> Unit,
    onOpenGoogleMaps: (MapLocation) -> Unit,
    onNavigateGps: (MapLocation) -> Unit,
    onNavigateWalk: (MapLocation) -> Unit,
    onCallPhone: (String) -> Unit,
    onSwitchToCroqui: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isKeyConfigured = remember { isGoogleMapsSdkConfigured() }
    var forceLoadSdk by remember { mutableStateOf(false) }

    if (!isKeyConfigured && !forceLoadSdk) {
        InteractiveOnlineMapView(
            locations = locations,
            selectedLocation = selectedLocation,
            onSelectLocation = onSelectLocation,
            onOpenGoogleMaps = onOpenGoogleMaps,
            onNavigateGps = onNavigateGps,
            onNavigateWalk = onNavigateWalk,
            onCallPhone = onCallPhone,
            onSwitchToCroqui = onSwitchToCroqui,
            modifier = modifier
        )
        return
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(SAO_LOURENCO_CENTER, 14.2f)
    }

    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    var isMapControlsExpanded by remember { mutableStateOf(false) }

    // Anima a câmera para a localização selecionada
    LaunchedEffect(selectedLocation) {
        selectedLocation?.let { loc ->
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(
                    LatLng(loc.latitude, loc.longitude),
                    16.2f
                ),
                durationMs = 650
            )
        }
    }

    val uiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
            compassEnabled = true,
            myLocationButtonEnabled = false,
            mapToolbarEnabled = false
        )
    }

    val mapProperties = remember(mapType) {
        MapProperties(
            mapType = mapType,
            isMyLocationEnabled = false
        )
    }

    Box(modifier = modifier.fillMaxSize().testTag("google_map_view_container")) {
        // Componente oficial do Google Maps SDK
        GoogleMap(
            modifier = Modifier.fillMaxSize().testTag("google_map_sdk_surface"),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = uiSettings,
            onMapClick = { onSelectLocation(null) }
        ) {
            // Marcadores personalizados categorizados
            locations.forEach { loc ->
                val isSelected = selectedLocation?.id == loc.id
                val markerState = rememberMarkerState(
                    key = loc.id,
                    position = LatLng(loc.latitude, loc.longitude)
                )

                MarkerComposable(
                    state = markerState,
                    title = loc.title,
                    snippet = loc.category.label,
                    onClick = {
                        onSelectLocation(loc)
                        true
                    }
                ) {
                    CategorizedCustomMarker(
                        location = loc,
                        isSelected = isSelected
                    )
                }
            }
        }

        // Controles Rápidos Flutuantes do Google Maps (Tipo de Mapa, Centralizar)
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            // Botão para alternar visualização do mapa (Normal, Satélite, Terreno)
            SmallFloatingActionButton(
                onClick = { isMapControlsExpanded = !isMapControlsExpanded },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                modifier = Modifier
                    .size(40.dp)
                    .shadow(3.dp, CircleShape)
                    .testTag("map_layer_toggle_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = "Tipo de mapa",
                    modifier = Modifier.size(20.dp)
                )
            }

            if (isMapControlsExpanded) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    shadowElevation = 4.dp,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        MapTypeOptionItem(
                            title = "Padrão",
                            selected = mapType == MapType.NORMAL,
                            onClick = {
                                mapType = MapType.NORMAL
                                isMapControlsExpanded = false
                            }
                        )
                        MapTypeOptionItem(
                            title = "Satélite",
                            selected = mapType == MapType.SATELLITE,
                            onClick = {
                                mapType = MapType.SATELLITE
                                isMapControlsExpanded = false
                            }
                        )
                        MapTypeOptionItem(
                            title = "Terreno",
                            selected = mapType == MapType.TERRAIN,
                            onClick = {
                                mapType = MapType.TERRAIN
                                isMapControlsExpanded = false
                            }
                        )
                    }
                }
            }

            // Botão para recentralizar na Casa (Estância Solarium)
            SmallFloatingActionButton(
                onClick = {
                    val houseLoc = locations.find { it.category == LocationCategory.HOUSE }
                    if (houseLoc != null) {
                        onSelectLocation(houseLoc)
                    } else {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(CASA_SOLARIUM_COORDS, 15.5f)
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Color(0xFFC85A32),
                shape = CircleShape,
                modifier = Modifier
                    .size(40.dp)
                    .shadow(3.dp, CircleShape)
                    .testTag("map_recenter_house_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Centralizar na Casa",
                    modifier = Modifier.size(20.dp)
                )
            }

            // Botão para ver toda a cidade de São Lourenço
            SmallFloatingActionButton(
                onClick = {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(SAO_LOURENCO_CENTER, 14.2f)
                    onSelectLocation(null)
                },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                modifier = Modifier
                    .size(40.dp)
                    .shadow(3.dp, CircleShape)
                    .testTag("map_recenter_city_button")
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Visão Geral de São Lourenço",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Card Flutuante de Detalhes do Local Selecionado no Google Maps
        AnimatedVisibility(
            visible = selectedLocation != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            selectedLocation?.let { loc ->
                GoogleMapLocationDetailCard(
                    location = loc,
                    onClose = { onSelectLocation(null) },
                    onOpenMaps = { onOpenGoogleMaps(loc) },
                    onNavigateGps = { onNavigateGps(loc) },
                    onNavigateWalk = { onNavigateWalk(loc) },
                    onCallPhone = { loc.phone?.let { onCallPhone(it) } }
                )
            }
        }
    }
}

/**
 * Marcador Personalizado no Google Maps categorizado por 'Restaurantes', 'Pontos Turísticos' e 'Emergência'.
 */
@Composable
fun CategorizedCustomMarker(
    location: MapLocation,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val categoryColor = getMarkerCategoryColor(location.category)
    val categoryIcon = getMarkerCategoryIcon(location.category, location.id)
    val markerSize = if (isSelected) 46.dp else 36.dp
    val iconSize = if (isSelected) 24.dp else 18.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Rótulo flutuante quando o marcador está selecionado
        if (isSelected) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 5.dp,
                border = BorderStroke(1.5.dp, categoryColor),
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = categoryColor,
                        modifier = Modifier.size(8.dp)
                    ) {}
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = location.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Pino do marcador com cabeça circular e base cônica
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .shadow(elevation = if (isSelected) 8.dp else 4.dp, shape = CircleShape)
                .size(markerSize)
                .background(color = categoryColor, shape = CircleShape)
                .border(
                    width = if (isSelected) 2.5.dp else 1.5.dp,
                    color = Color.White,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = categoryIcon,
                contentDescription = location.title,
                tint = Color.White,
                modifier = Modifier.size(iconSize)
            )
        }

        // Triângulo / ponta inferior do pino
        Box(
            modifier = Modifier
                .width(10.dp)
                .height(6.dp)
                .background(
                    color = categoryColor,
                    shape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)
                )
                .border(
                    width = 0.5.dp,
                    color = Color.White.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)
                )
        )
    }
}

/**
 * Card de visualização rápida do ponto clicado no Google Maps
 */
@Composable
private fun GoogleMapLocationDetailCard(
    location: MapLocation,
    onClose: () -> Unit,
    onOpenMaps: () -> Unit,
    onNavigateGps: () -> Unit,
    onNavigateWalk: () -> Unit,
    onCallPhone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = getMarkerCategoryColor(location.category)
    val categoryIcon = getMarkerCategoryIcon(location.category, location.id)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke(1.dp, categoryColor.copy(alpha = 0.35f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("google_map_detail_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Linha superior: Categoria e Botão Fechar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = categoryColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, categoryColor.copy(alpha = 0.3f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = null,
                            tint = categoryColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = location.category.label,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = categoryColor
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (location.rating != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFF3CD)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFF59F00),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${location.rating}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = Color(0xFF7A4A00)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar detalhes",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Título e Endereço
            Text(
                text = location.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = location.address,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (!location.distanceEstimate.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Da Casa: ${location.distanceEstimate}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Botões de Ação Rápida no Google Maps
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Rota de Carro
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onNavigateGps)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 9.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Directions,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Rota Carro",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }

                // Rota a Pé
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onNavigateWalk)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 9.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "A Pé",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Abrir no Google Maps App
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF4285F4).copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Color(0xFF4285F4).copy(alpha = 0.35f)),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onOpenMaps)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 9.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = Color(0xFF1A73E8),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "No Maps",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF1A73E8)
                        )
                    }
                }

                // Botão de ligar se tiver telefone
                if (!location.phone.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF2B8A3E).copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, Color(0xFF2B8A3E).copy(alpha = 0.35f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = onCallPhone)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Ligar",
                                tint = Color(0xFF2B8A3E),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MapTypeOptionItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
            border = BorderStroke(1.5.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.size(12.dp)
        ) {}
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Cores dos marcadores personalizadas por categoria:
 * - Restaurantes (FOOD): Laranja Vibrante (#E8590C)
 * - Pontos Turísticos (ATTRACTION): Azul Turístico Real (#1C7ED6)
 * - Emergência (EMERGENCY): Vermelho de Emergência (#E03131)
 * - A Casa (HOUSE): Terracota da Estância (#C85A32)
 * - Mercados e Serviços (SERVICES): Verde Serviços (#2B8A3E)
 * - Transporte (TRANSPORT): Ciano Transporte (#1098AD)
 */
fun getMarkerCategoryColor(category: LocationCategory): Color {
    return when (category) {
        LocationCategory.FOOD -> Color(0xFFE8590C)       // 'Restaurantes'
        LocationCategory.ATTRACTION -> Color(0xFF1C7ED6) // 'Pontos Turísticos'
        LocationCategory.EMERGENCY -> Color(0xFFE03131)  // 'Emergência'
        LocationCategory.HOUSE -> Color(0xFFC85A32)      // Acomodação
        LocationCategory.SERVICES -> Color(0xFF2B8A3E)   // Serviços e Comércio
        LocationCategory.TRANSPORT -> Color(0xFF1098AD)  // Transporte
    }
}

fun getMarkerCategoryIcon(category: LocationCategory, locationId: String): ImageVector {
    return when {
        locationId == "trem_aguas" -> Icons.Default.DirectionsBus
        category == LocationCategory.FOOD -> Icons.Default.Restaurant
        category == LocationCategory.ATTRACTION -> Icons.Default.Explore
        category == LocationCategory.EMERGENCY -> Icons.Default.LocalHospital
        category == LocationCategory.HOUSE -> Icons.Default.Home
        category == LocationCategory.SERVICES -> Icons.Default.ShoppingBag
        category == LocationCategory.TRANSPORT -> Icons.Default.DirectionsBus
        else -> Icons.Default.Place
    }
}

/**
 * Interface JavaScript para comunicação segura bidirecional entre o mapa Leaflet/WebView
 * e os componentes de UI nativos do Jetpack Compose.
 */
class WebAppInterface(
    private val onSelect: (MapLocation?) -> Unit,
    private val getLocations: () -> List<MapLocation>
) {
    private val handler = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun onSelectLocation(locationId: String) {
        val loc = if (locationId.isBlank()) null else getLocations().find { it.id == locationId }
        handler.post {
            onSelect(loc)
        }
    }
}

/**
 * Converte a lista de locais para formato JSON seguro para injeção no JavaScript do Leaflet.
 */
private fun buildLocationsJson(locations: List<MapLocation>): String {
    return locations.joinToString(",") { loc ->
        val safeTitle = loc.title.replace("\\", "\\\\").replace("\"", "\\\"").replace("'", "\\'")
        val isSolarium = loc.id == "casa_solarium" || loc.category == LocationCategory.HOUSE
        """{"id":"${loc.id}","title":"$safeTitle","lat":${loc.latitude},"lng":${loc.longitude},"cat":"${loc.category.name}","isSolarium":$isSolarium}"""
    }
}

/**
 * Gera o documento HTML5 autocontido com Leaflet.js para renderização de mapa interativo de alta
 * fidelidade com camada de ruas (CartoDB Voyager) e fotos de satélite (Esri World Imagery).
 */
private fun generateLeafletMapHtml(initialLocations: List<MapLocation>): String {
    val initialLocationsJson = buildLocationsJson(initialLocations)

    return """
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=5.0, user-scalable=yes" />
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    <style>
        * { box-sizing: border-box; -webkit-tap-highlight-color: transparent; }
        html, body, #map {
            width: 100%;
            height: 100%;
            margin: 0;
            padding: 0;
            background: #F5EFEB;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            overflow: hidden;
        }
        .leaflet-container { font: inherit; }
        .leaflet-control-attribution { font-size: 8px !important; opacity: 0.6; }
        
        .pin-wrapper { background: transparent; border: none; }
        .pin-container {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            pointer-events: auto;
            position: relative;
        }
        .pin-label {
            background: rgba(255, 255, 255, 0.96);
            color: #212121;
            font-size: 10px;
            font-weight: 700;
            padding: 2px 6px;
            border-radius: 6px;
            white-space: nowrap;
            box-shadow: 0 2px 5px rgba(0,0,0,0.25);
            border: 1px solid rgba(0,0,0,0.1);
            margin-bottom: 2px;
            max-width: 120px;
            overflow: hidden;
            text-overflow: ellipsis;
            pointer-events: none;
            transition: all 0.2s ease;
        }
        .pin-badge {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 15px;
            color: #fff;
            border: 2px solid #FFFFFF;
            box-shadow: 0 3px 8px rgba(0,0,0,0.35);
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }
        .pin-point {
            width: 0;
            height: 0;
            border-left: 5px solid transparent;
            border-right: 5px solid transparent;
            border-top: 6px solid #333;
            margin-top: -1px;
        }
        
        /* Marcador Especial Casa Solarium */
        .solarium-pin .pin-label {
            background: #FFF8E7;
            color: #8D4004;
            border: 1.5px solid #E67E22;
            font-weight: 800;
        }
        .solarium-pin .pin-badge {
            width: 40px;
            height: 40px;
            font-size: 20px;
            background: linear-gradient(135deg, #FFB703, #D4A373) !important;
            border: 2.5px solid #FFFFFF;
            box-shadow: 0 0 16px rgba(255, 183, 3, 0.8), 0 4px 10px rgba(0,0,0,0.35);
            animation: pulse-glow 2.5s infinite;
        }
        @keyframes pulse-glow {
            0% { transform: scale(1); box-shadow: 0 0 0 0 rgba(230, 126, 34, 0.7); }
            70% { transform: scale(1.06); box-shadow: 0 0 0 12px rgba(230, 126, 34, 0); }
            100% { transform: scale(1); box-shadow: 0 0 0 0 rgba(230, 126, 34, 0); }
        }

        .selected .pin-badge {
            transform: scale(1.22);
            border-color: #FFD166;
            box-shadow: 0 0 14px rgba(255, 209, 102, 0.9), 0 6px 12px rgba(0,0,0,0.4);
        }
        .selected .pin-label {
            background: #212121;
            color: #FFFFFF;
            font-size: 11px;
        }
    </style>
</head>
<body>
    <div id="map"></div>
    <script>
        const initialLocationsData = [$initialLocationsJson];
        
        const streetLayer = L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            attribution: '&copy; OpenStreetMap'
        });

        const satelliteLayer = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
            maxZoom: 19,
            attribution: '&copy; Esri World Imagery'
        });

        const map = L.map('map', {
            center: [-22.112678, -45.056412],
            zoom: 15,
            zoomControl: false,
            layers: [streetLayer]
        });

        let currentActivePinId = null;
        let markersMap = {};

        function getMeta(cat, isSolarium) {
            if (isSolarium) return { color: '#C85A32', emoji: '🏠' };
            switch(cat) {
                case 'FOOD': return { color: '#E65100', emoji: '🍽️' };
                case 'RESTAURANT': return { color: '#E65100', emoji: '🍽️' };
                case 'ATTRACTION': return { color: '#2E7D32', emoji: '🏞️' };
                case 'CAFE_BAR': return { color: '#795548', emoji: '☕' };
                case 'HEALTH': return { color: '#C62828', emoji: '🏥' };
                case 'EMERGENCY': return { color: '#C62828', emoji: '🏥' };
                case 'HOUSE': return { color: '#C85A32', emoji: '🏠' };
                case 'SERVICES': return { color: '#1565C0', emoji: '🛍️' };
                case 'TRANSPORT': return { color: '#00838F', emoji: '🚌' };
                default: return { color: '#D97706', emoji: '📍' };
            }
        }

        function renderMarkers(data, shouldAutoCenter) {
            // Remove marcadores antigos do mapa
            for (let id in markersMap) {
                if (markersMap.hasOwnProperty(id)) {
                    map.removeLayer(markersMap[id]);
                }
            }
            markersMap = {};

            const bounds = [];

            data.forEach(loc => {
                const meta = getMeta(loc.cat, loc.isSolarium);
                const isSol = loc.isSolarium;
                const pinClass = isSol ? 'pin-container solarium-pin' : 'pin-container';
                const iconHtml = '<div class="' + pinClass + '" id="pin-' + loc.id + '" onclick="handlePinTap(\'' + loc.id + '\')">' +
                    '<div class="pin-label">' + loc.title + '</div>' +
                    '<div class="pin-badge" style="background-color: ' + meta.color + ';">' + meta.emoji + '</div>' +
                    '<div class="pin-point" style="border-top-color: ' + meta.color + ';"></div>' +
                    '</div>';
                const icon = L.divIcon({
                    className: 'pin-wrapper',
                    html: iconHtml,
                    iconSize: [40, isSol ? 60 : 54],
                    iconAnchor: [20, isSol ? 60 : 54]
                });
                const marker = L.marker([loc.lat, loc.lng], {
                    icon: icon,
                    zIndexOffset: isSol ? 1000 : 100
                }).addTo(map);

                marker.on('click', function(e) {
                    if (e.originalEvent) e.originalEvent.stopPropagation();
                    handlePinTap(loc.id);
                });

                markersMap[loc.id] = marker;
                bounds.push([loc.lat, loc.lng]);
            });

            if (currentActivePinId && !markersMap[currentActivePinId]) {
                currentActivePinId = null;
            } else if (currentActivePinId && markersMap[currentActivePinId]) {
                highlightPin(currentActivePinId);
            }

            // Enquadramento automático suave de acordo com os pontos filtrados
            if (shouldAutoCenter && bounds.length > 0) {
                if (bounds.length === 1) {
                    map.flyTo(bounds[0], 16.2, { duration: 0.55 });
                } else {
                    map.flyToBounds(bounds, { padding: [55, 55], maxZoom: 16.5, duration: 0.55 });
                }
            }
        }

        renderMarkers(initialLocationsData, false);

        window.updateLocations = function(newData) {
            renderMarkers(newData, true);
        };

        function handlePinTap(id) {
            highlightPin(id);
            if (window.AndroidInterface && window.AndroidInterface.onSelectLocation) {
                window.AndroidInterface.onSelectLocation(id);
            }
        }

        function highlightPin(id) {
            if (currentActivePinId) {
                const prev = document.getElementById('pin-' + currentActivePinId);
                if (prev) prev.classList.remove('selected');
            }
            currentActivePinId = id;
            if (id) {
                const cur = document.getElementById('pin-' + id);
                if (cur) cur.classList.add('selected');
            }
        }

        map.on('click', function() {
            highlightPin(null);
            if (window.AndroidInterface && window.AndroidInterface.onSelectLocation) {
                window.AndroidInterface.onSelectLocation('');
            }
        });

        window.setMapLayer = function(type) {
            if (type === 'satellite') {
                if (map.hasLayer(streetLayer)) map.removeLayer(streetLayer);
                if (!map.hasLayer(satelliteLayer)) map.addLayer(satelliteLayer);
            } else {
                if (map.hasLayer(satelliteLayer)) map.removeLayer(satelliteLayer);
                if (!map.hasLayer(streetLayer)) map.addLayer(streetLayer);
            }
        };

        window.recenterCasa = function() {
            map.flyTo([-22.112678, -45.056412], 16, { duration: 0.8 });
            highlightPin('casa_solarium');
        };

        window.recenterCity = function() {
            map.flyTo([-22.1158, -45.0535], 14, { duration: 0.8 });
            highlightPin(null);
        };

        window.panToLocation = function(id, lat, lng) {
            map.flyTo([lat, lng], 16.5, { duration: 0.6 });
            highlightPin(id);
        };

        window.zoomIn = function() { map.zoomIn(); };
        window.zoomOut = function() { map.zoomOut(); };
    </script>
</body>
</html>
    """.trimIndent()
}

/**
 * Mapa Interativo Online de Alta Resolução baseado em Leaflet, CartoDB Voyager e Esri Satélite HD.
 * Funciona imediatamente e com total fluidez, com suporte a filtragem reativa de categorias.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun InteractiveOnlineMapView(
    locations: List<MapLocation>,
    selectedLocation: MapLocation?,
    onSelectLocation: (MapLocation?) -> Unit,
    onOpenGoogleMaps: (MapLocation) -> Unit,
    onNavigateGps: (MapLocation) -> Unit,
    onNavigateWalk: (MapLocation) -> Unit,
    onCallPhone: (String) -> Unit,
    onSwitchToCroqui: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var isSatelliteLayer by remember { mutableStateOf(false) }
    var isPageLoaded by remember { mutableStateOf(false) }

    val currentLocationsRef = rememberUpdatedState(locations)

    val locationsJson = remember(locations) {
        buildLocationsJson(locations)
    }

    // Reage instantaneamente à filtragem de pontos por categoria ou busca
    LaunchedEffect(locationsJson, isPageLoaded) {
        if (isPageLoaded) {
            webViewInstance?.evaluateJavascript(
                "if (window.updateLocations) { window.updateLocations([$locationsJson]); }",
                null
            )
        }
    }

    // Reage à mudança de local selecionado em Compose
    LaunchedEffect(selectedLocation, isPageLoaded) {
        if (isPageLoaded) {
            selectedLocation?.let { loc ->
                webViewInstance?.evaluateJavascript(
                    "panToLocation('${loc.id}', ${loc.latitude}, ${loc.longitude});",
                    null
                )
            } ?: run {
                webViewInstance?.evaluateJavascript(
                    "highlightPin(null);",
                    null
                )
            }
        }
    }

    val htmlData = remember {
        generateLeafletMapHtml(locations)
    }

    Box(modifier = modifier.fillMaxSize().testTag("interactive_online_map_view_container")) {
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isPageLoaded = true
                            view?.evaluateJavascript(
                                "if (window.updateLocations) { window.updateLocations([$locationsJson]); }",
                                null
                            )
                            selectedLocation?.let { loc ->
                                view?.evaluateJavascript(
                                    "panToLocation('${loc.id}', ${loc.latitude}, ${loc.longitude});",
                                    null
                                )
                            }
                        }
                    }
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        setSupportZoom(true)
                        builtInZoomControls = false
                        displayZoomControls = false
                        useWideViewPort = true
                        loadWithOverviewMode = true
                        userAgentString = "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36 EstanciaSolarium/1.0"
                    }
                    addJavascriptInterface(
                        WebAppInterface(
                            onSelect = onSelectLocation,
                            getLocations = { currentLocationsRef.value }
                        ),
                        "AndroidInterface"
                    )
                    loadDataWithBaseURL(
                        "https://solarium.app/",
                        htmlData,
                        "text/html",
                        "UTF-8",
                        null
                    )
                    webViewInstance = this
                }
            },
            update = { webView ->
                webViewInstance = webView
                if (isPageLoaded) {
                    webView.evaluateJavascript(
                        "if (window.updateLocations) { window.updateLocations([$locationsJson]); }",
                        null
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Controles Rápidos Flutuantes
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            // Botão para alternar visualização do mapa (Ruas vs Satélite)
            SmallFloatingActionButton(
                onClick = {
                    val newIsSat = !isSatelliteLayer
                    isSatelliteLayer = newIsSat
                    webViewInstance?.evaluateJavascript(
                        "setMapLayer('${if (newIsSat) "satellite" else "street"}');",
                        null
                    )
                },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = if (isSatelliteLayer) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                shape = CircleShape,
                modifier = Modifier
                    .size(40.dp)
                    .shadow(3.dp, CircleShape)
                    .testTag("online_map_layer_toggle_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = if (isSatelliteLayer) "Modo Ruas" else "Modo Satélite",
                    modifier = Modifier.size(20.dp)
                )
            }

            // Botão para recentralizar na Casa (Estância Solarium)
            SmallFloatingActionButton(
                onClick = {
                    webViewInstance?.evaluateJavascript("recenterCasa();", null)
                    val houseLoc = locations.find { it.category == LocationCategory.HOUSE || it.id == "casa_solarium" }
                    if (houseLoc != null) {
                        onSelectLocation(houseLoc)
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Color(0xFFC85A32),
                shape = CircleShape,
                modifier = Modifier
                    .size(40.dp)
                    .shadow(3.dp, CircleShape)
                    .testTag("online_map_recenter_house_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Centralizar na Casa Solarium",
                    modifier = Modifier.size(20.dp)
                )
            }

            // Botão para visão geral de São Lourenço
            SmallFloatingActionButton(
                onClick = {
                    webViewInstance?.evaluateJavascript("recenterCity();", null)
                    onSelectLocation(null)
                },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                modifier = Modifier
                    .size(40.dp)
                    .shadow(3.dp, CircleShape)
                    .testTag("online_map_recenter_city_button")
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Visão Geral de São Lourenço",
                    modifier = Modifier.size(20.dp)
                )
            }

            // Botão de Zoom +
            SmallFloatingActionButton(
                onClick = {
                    webViewInstance?.evaluateJavascript("zoomIn();", null)
                },
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = CircleShape,
                modifier = Modifier
                    .size(36.dp)
                    .shadow(2.dp, CircleShape)
            ) {
                Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            // Botão de Zoom -
            SmallFloatingActionButton(
                onClick = {
                    webViewInstance?.evaluateJavascript("zoomOut();", null)
                },
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = CircleShape,
                modifier = Modifier
                    .size(36.dp)
                    .shadow(2.dp, CircleShape)
            ) {
                Text("−", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Badge indicador do modo do mapa (Ruas / Satélite) no topo esquerdo
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            shadowElevation = 2.dp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 12.dp, top = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isSatelliteLayer) Color(0xFF4CAF50) else Color(0xFF2196F3),
                    modifier = Modifier.size(8.dp)
                ) {}
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isSatelliteLayer) "Satélite HD" else "Mapa de Ruas",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Card Flutuante de Detalhes do Local Selecionado
        AnimatedVisibility(
            visible = selectedLocation != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            selectedLocation?.let { loc ->
                GoogleMapLocationDetailCard(
                    location = loc,
                    onClose = {
                        onSelectLocation(null)
                        webViewInstance?.evaluateJavascript("highlightPin(null);", null)
                    },
                    onOpenMaps = { onOpenGoogleMaps(loc) },
                    onNavigateGps = { onNavigateGps(loc) },
                    onNavigateWalk = { onNavigateWalk(loc) },
                    onCallPhone = { loc.phone?.let { onCallPhone(it) } }
                )
            }
        }
    }
}

