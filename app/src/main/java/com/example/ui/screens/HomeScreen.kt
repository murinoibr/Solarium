package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ManualSection
import com.example.data.repository.HouseRepository
import com.example.ui.theme.SolariumTheme
import kotlin.math.cos
import kotlin.math.sin

/**
 * Tela Inicial Independente (Início) com visual acolhedor,
 * logomarca minimalista e clean do Solarium e acesso direto aos 10 itens do manual.
 */
@Composable
fun HomeScreen(
    sections: List<ManualSection>,
    onSectionClick: (ManualSection) -> Unit,
    onNavigateToManual: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToRecommendations: () -> Unit,
    onNavigateToChat: () -> Unit,
    onFeedback: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colors = SolariumTheme.colors
    val chunkedSections = androidx.compose.runtime.remember(sections) { sections.chunked(2) }
    var showWifiDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(colors.creamBackground)
            .testTag("home_screen_root"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // 1. Logomarca Solarium Clean e Minimalista
        item(key = "solarium_brand_header") {
            SolariumMinimalistBrandHeader(
                onCopyAddress = {
                    copyToClipboard(context, HouseRepository.HOUSE_ADDRESS, "Endereço da casa copiado!")
                    onFeedback("Endereço copiado: ${HouseRepository.HOUSE_ADDRESS}")
                }
            )
        }

        // 2. Atalho do Wi-Fi com todas as funções integradas (cópia rápida, detalhes e configurações)
        item(key = "quick_wifi_action") {
            QuickWifiActionButton(
                onClick = {
                    copyToClipboard(context, HouseRepository.WIFI_PASSWORD, "Senha do Wi-Fi copiada!")
                    onFeedback("Wi-Fi: ${HouseRepository.WIFI_SSID} • Senha copiada: ${HouseRepository.WIFI_PASSWORD}")
                    showWifiDialog = true
                }
            )
        }

        // 3. Cabeçalho dos 10 Itens do Manual
        item(key = "ten_items_header") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Manual da Casa",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                letterSpacing = (-0.2).sp
                            ),
                            color = colors.textPrimary
                        )
                        Text(
                            text = "10 guias essenciais para sua estadia",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = colors.softYellowContainer,
                        onClick = onNavigateToManual
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Ver Tudo",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = colors.onSoftYellowContainer
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = colors.onSoftYellowContainer,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }

        // 4. Grade de 2 Colunas com os 10 Capítulos do Manual (idêntica ao vídeo)
        items(
            count = chunkedSections.size,
            key = { rowIndex -> "manual_grid_row_$rowIndex" }
        ) { rowIndex ->
            val rowPair = chunkedSections[rowIndex]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ManualGridCard(
                    section = rowPair[0],
                    onClick = { onSectionClick(rowPair[0]) },
                    modifier = Modifier.weight(1f)
                )
                if (rowPair.size > 1) {
                    ManualGridCard(
                        section = rowPair[1],
                        onClick = { onSectionClick(rowPair[1]) },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // 5. Atalhos Rápidos para Outras Seções (Explorar a Casa e Arredores)
        item(key = "quick_shortcuts_footer") {
            QuickNavigationShortcuts(
                onNavigateToMap = onNavigateToMap,
                onNavigateToRecommendations = onNavigateToRecommendations,
                onNavigateToChat = onNavigateToChat
            )
        }
    }

    // Modal com todos os dados e funções do Wi-Fi
    if (showWifiDialog) {
        AlertDialog(
            onDismissRequest = { showWifiDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(colors.sunOrangeContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "Wi-Fi",
                        tint = colors.warmTerracotta,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Wi-Fi da Casa",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    ),
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Nome da Rede (SSID)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = colors.creamSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Nome da Rede (SSID)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.textSecondary
                                )
                                Text(
                                    text = HouseRepository.WIFI_SSID,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = colors.textPrimary
                                )
                            }
                            IconButton(
                                onClick = {
                                    copyToClipboard(context, HouseRepository.WIFI_SSID, "Nome da rede copiado!")
                                    onFeedback("Nome da rede copiado: ${HouseRepository.WIFI_SSID}")
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copiar nome da rede",
                                    tint = colors.warmTerracotta,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Senha do Wi-Fi
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = colors.creamSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Senha do Wi-Fi",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.textSecondary
                                )
                                Text(
                                    text = HouseRepository.WIFI_PASSWORD,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = colors.warmTerracotta
                                )
                            }
                            IconButton(
                                onClick = {
                                    copyToClipboard(context, HouseRepository.WIFI_PASSWORD, "Senha copiada!")
                                    onFeedback("Senha copiada: ${HouseRepository.WIFI_PASSWORD}")
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copiar senha",
                                    tint = colors.warmTerracotta,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "A senha já foi copiada para sua área de transferência. Basta colar para conectar!",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            onFeedback("Abra as configurações de Wi-Fi do seu aparelho.")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.warmTerracotta,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Abrir Wi-Fi")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showWifiDialog = false }
                ) {
                    Text(
                        text = "Fechar",
                        color = colors.textSecondary
                    )
                }
            }
        )
    }
}

/**
 * Logomarca minimalista e clean do Solarium com estética ensolarada e acolhedora.
 */
@Composable
private fun SolariumMinimalistBrandHeader(
    onCopyAddress: () -> Unit
) {
    val colors = SolariumTheme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 12.dp, start = 20.dp, end = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Ícone Minimalista de Sol com Raios Geométricos
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                colors.softYellowContainer.copy(alpha = 0.9f),
                                colors.sunOrangeContainer.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                MinimalistSunLogoCanvas(
                    sunColor = colors.sunOrange,
                    rayColor = colors.softYellow,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Nome da Marca com Tipografia Elegante e Amplo Espaçamento
            Text(
                text = "S O L A R I U M",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 4.5.sp,
                    fontFamily = FontFamily.Serif
                ),
                color = colors.textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Slogan com tipografia elegante e acolhedora
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(22.dp)
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, colors.warmTerracotta.copy(alpha = 0.55f))
                            )
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Hospedagem Familiar",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Serif,
                        letterSpacing = 0.6.sp
                    ),
                    color = colors.warmTerracotta,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .width(22.dp)
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(colors.warmTerracotta.copy(alpha = 0.55f), Color.Transparent)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Frase de Boas-Vindas e Endereço Copiável
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.linenBorder),
                shadowElevation = 1.dp,
                onClick = onCopyAddress,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_copy_address_header")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = colors.sunOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = HouseRepository.HOUSE_ADDRESS,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copiar endereço",
                        tint = colors.warmTerracotta,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

/**
 * Desenho vetorial minimalista do sol em Canvas (Círculo solar + 8 raios finos e elegantes).
 */
@Composable
private fun MinimalistSunLogoCanvas(
    sunColor: Color,
    rayColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val centerRadius = size.width * 0.24f
        val innerRay = size.width * 0.34f
        val outerRay = size.width * 0.46f

        // Círculo Central Solar
        drawCircle(
            color = sunColor,
            radius = centerRadius,
            center = Offset(centerX, centerY)
        )

        // 8 Raios Solares Finos e Elegantes
        for (i in 0 until 8) {
            val angle = Math.toRadians((i * 45.0)).toFloat()
            val startX = centerX + innerRay * cos(angle)
            val startY = centerY + innerRay * sin(angle)
            val endX = centerX + outerRay * cos(angle)
            val endY = centerY + outerRay * sin(angle)

            drawLine(
                color = rayColor,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * Atalho único do Wi-Fi na página principal com design acolhedor e funções completas de rede.
 */
@Composable
private fun QuickWifiActionButton(
    onClick: () -> Unit
) {
    val colors = SolariumTheme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp, bottom = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onClick)
                .padding(vertical = 6.dp, horizontal = 24.dp)
                .testTag("quick_btn_wifi")
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(colors.sunOrangeContainer.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = "Wi-Fi da Casa",
                    tint = colors.warmTerracotta,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Wi-Fi",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                ),
                color = colors.textPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Card da Grade de 2 Colunas para cada um dos 10 itens do manual (idêntico ao vídeo).
 */
@Composable
private fun ManualGridCard(
    section: ManualSection,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SolariumTheme.colors
    val icon = iconForManualSection(section.id, section.iconName)

    // Paleta acolhedora e contrastante para cada um dos 10 tópicos
    val (itemTint, containerTint) = when (section.id) {
        1 -> Pair(Color(0xFFC85A32), Color(0xFFFFECE5)) // Bem-vindos (Coração)
        2 -> Pair(Color(0xFF2D6A4F), Color(0xFFE8F5E9)) // Localização (Pin)
        3 -> Pair(Color(0xFFD97706), Color(0xFFFEF3C7)) // Chegada / Check-in (Chave)
        4 -> Pair(Color(0xFF7C3AED), Color(0xFFEDE9FE)) // Entretenimento (TV)
        5 -> Pair(Color(0xFF0284C7), Color(0xFFE0F2FE)) // Cama, Banho & Conforto (Cama)
        6 -> Pair(Color(0xFFDC2626), Color(0xFFFEE2E2)) // Cozinha Equipada (Restaurante)
        7 -> Pair(Color(0xFFB45309), Color(0xFFFEF3C7)) // Regras & Segurança (Escudo)
        8 -> Pair(Color(0xFF15803D), Color(0xFFDCFCE7)) // Lixo & Reciclagem (Lixeira)
        9 -> Pair(Color(0xFF0D9488), Color(0xFFCCFBF1)) // Transporte & Garagem (Carro)
        10 -> Pair(Color(0xFFEA580C), Color(0xFFFFEDD5)) // Atividades & Lazer (Bússola)
        else -> Pair(colors.sunOrange, colors.sunOrangeContainer)
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.linenBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp, pressedElevation = 4.dp),
        modifier = modifier
            .height(116.dp)
            .testTag("manual_grid_card_${section.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(containerTint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = section.title,
                    tint = itemTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = section.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                ),
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Mapeamento dos 10 itens para ícones expressivos do Material Design.
 */
private fun iconForManualSection(id: Int, iconName: String): ImageVector {
    return when (id) {
        1 -> Icons.Default.Favorite
        2 -> Icons.Default.Place
        3 -> Icons.Default.Key
        4 -> Icons.Default.Tv
        5 -> Icons.Default.Bed
        6 -> Icons.Default.Restaurant
        7 -> Icons.Default.Shield
        8 -> Icons.Default.Delete
        9 -> Icons.Default.DirectionsCar
        10 -> Icons.Default.Explore
        else -> when (iconName) {
            "Favorite" -> Icons.Default.Favorite
            "Place" -> Icons.Default.Place
            "Key" -> Icons.Default.Key
            "Tv" -> Icons.Default.Tv
            "Bed" -> Icons.Default.Bed
            "Restaurant" -> Icons.Default.Restaurant
            "Shield" -> Icons.Default.Shield
            "Delete" -> Icons.Default.Delete
            "DirectionsCar" -> Icons.Default.DirectionsCar
            else -> Icons.Default.Explore
        }
    }
}

/**
 * Atalhos no Rodapé "Explorar a Casa e Arredores" (3 Cards horizontais idênticos ao vídeo).
 */
@Composable
private fun QuickNavigationShortcuts(
    onNavigateToMap: () -> Unit,
    onNavigateToRecommendations: () -> Unit,
    onNavigateToChat: () -> Unit
) {
    val colors = SolariumTheme.colors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Text(
            text = "Explorar a Casa e Arredores",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.5.sp
            ),
            color = colors.textPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ShortcutPill(
                title = "Mapa da Cidade",
                icon = Icons.Default.Map,
                tint = Color(0xFFC85A32),
                containerTint = Color(0xFFFFECE5),
                onClick = onNavigateToMap,
                modifier = Modifier.weight(1f)
            )

            ShortcutPill(
                title = "Dicas Locais",
                icon = Icons.Default.Explore,
                tint = Color(0xFF2D6A4F),
                containerTint = Color(0xFFE8F5E9),
                onClick = onNavigateToRecommendations,
                modifier = Modifier.weight(1f)
            )

            ShortcutPill(
                title = "Concierge IA",
                icon = Icons.AutoMirrored.Filled.Chat,
                tint = Color(0xFFB84C26),
                containerTint = Color(0xFFFFDBD0),
                onClick = onNavigateToChat,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ShortcutPill(
    title: String,
    icon: ImageVector,
    tint: Color,
    containerTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SolariumTheme.colors

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.linenBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick,
        modifier = modifier.height(108.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(containerTint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                ),
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

/**
 * Utilitários para área de transferência e WhatsApp.
 */
private fun copyToClipboard(context: Context, text: String, label: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
}

private fun openWhatsApp(context: Context, phoneNumber: String, message: String) {
    try {
        val cleanNumber = phoneNumber.replace(Regex("[^0-9]"), "")
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanNumber&text=${Uri.encode(message)}")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        // WhatsApp ou navegador indisponível: feedback de cópia em área de transferência já realizado
    }
}
