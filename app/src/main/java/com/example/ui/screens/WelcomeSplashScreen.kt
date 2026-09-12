package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SolariumTheme
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

/**
 * Launchpage Oficial Animada da Estância Solarium:
 * - 100% em cores claras (fundo linho/marfim acolhedor, sem dark mode).
 * - Fase 1 (0 a 1.6s): O ícone emblemático (squircle terracota, casa, porta em arco e coração dourado)
 *   surge com scale suave e pulsação do coração acolhedor.
 * - Fase 2 (1.6s em diante): Transição cinematográfica para o Sol Radiante animado,
 *   com raios girando suavemente, o sol dourado pulsante, tipografia elegante "S O L A R I U M",
 *   o subtítulo "ESTÂNCIA & REFÚGIO • SÃO LOURENÇO, MG", e a mensagem "Bem-vindos à sua casa".
 * - Botão animado "Entrar no Guia" e toque em qualquer ponto da tela para avançar imediatamente.
 * - Transição automática suave após 4.2s.
 */
@Composable
fun WelcomeSplashScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = SolariumTheme.colors
    // 0 = Fase Ícone da Casa, 1 = Fase Sol Radiante & Marca Solarium
    var currentPhase by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        // Fase 1: Ícone da casa pulsando (1600ms)
        delay(1600)
        currentPhase = 1
        // Fase 2: Apresentação animada do Solarium (2600ms adicionais para contemplar)
        delay(2600)
        onFinish()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F5F0))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onFinish
            )
            .testTag("welcome_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Halo quente solar de fundo sutil
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFF0D4).copy(alpha = 0.7f),
                        Color(0xFFFCEFE3).copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy * 0.9f),
                    radius = size.width * 0.85f
                )
            )
        }

        // =====================================================================
        // FASE 1: Ícone da Casa com Porta em Arco e Coração Dourado Animado
        // =====================================================================
        AnimatedVisibility(
            visible = currentPhase == 0,
            enter = fadeIn(animationSpec = tween(400)) + scaleIn(initialScale = 0.85f, animationSpec = tween(500, easing = FastOutSlowInEasing)),
            exit = fadeOut(animationSpec = tween(400)) + scaleOut(targetScale = 1.15f, animationSpec = tween(400))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedLaunchHouseIcon()
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "S O L A R I U M",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 4.sp,
                        fontFamily = FontFamily.Serif
                    ),
                    color = Color(0xFFB84C26).copy(alpha = 0.75f)
                )
            }
        }

        // =====================================================================
        // FASE 2: O Sol Radiante Animado e Apresentação da Marca Solarium
        // =====================================================================
        AnimatedVisibility(
            visible = currentPhase == 1,
            enter = fadeIn(animationSpec = tween(600)) + scaleIn(initialScale = 0.92f, animationSpec = tween(600, easing = FastOutSlowInEasing)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp)
            ) {
                // Sol Radiante com Raios Giratórios e Pulsação
                AnimatedRadiantSunLogo()

                Spacer(modifier = Modifier.height(28.dp))

                // Nome da Estância em Tipografia Display Serifada Espaçada
                Text(
                    text = "S O L A R I U M",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 7.sp,
                        fontFamily = FontFamily.Serif
                    ),
                    color = Color(0xFF231B15),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Subtítulo Oficial da Estância
                Text(
                    text = "ESTÂNCIA & REFÚGIO • SÃO LOURENÇO, MG",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.2.sp
                    ),
                    color = Color(0xFFB84C26),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Frase Aconchegante de Boas-Vindas
                Text(
                    text = "Bem-vindos à sua casa",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Serif
                    ),
                    color = Color(0xFF6E5E52),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Botão Elegante para Entrar Imediatamente
                Button(
                    onClick = onFinish,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDC5B16),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("launch_btn_enter")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "Entrar no Guia da Casa",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Entrar",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Toque em qualquer lugar para pular",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = Color(0xFF8F7E70).copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * Ícone da Casa Solarium com animação suave de pulsação e brilho (Fase 1).
 */
@Composable
private fun AnimatedLaunchHouseIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "house_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "house_scale"
    )

    val heartPulse by infiniteTransition.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heart_pulse"
    )

    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        // Halo quente de fundo
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFFFFE2CF).copy(alpha = 0.7f),
                radius = size.width * 0.48f
            )
        }

        // Squircle Terracota com a casa branca e coração dourado
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xFFB84C26)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(60.dp)) {
                val w = size.width
                val h = size.height

                // Chaminé
                val chimneyPath = Path().apply {
                    moveTo(w * 0.68f, h * 0.30f)
                    lineTo(w * 0.68f, h * 0.44f)
                    lineTo(w * 0.76f, h * 0.49f)
                    lineTo(w * 0.76f, h * 0.30f)
                    close()
                }
                drawPath(chimneyPath, Color.White, style = Fill)

                // Telhado da casa
                val roofPath = Path().apply {
                    moveTo(w * 0.50f, h * 0.20f)
                    lineTo(w * 0.14f, h * 0.50f)
                    lineTo(w * 0.22f, h * 0.56f)
                    lineTo(w * 0.50f, h * 0.32f)
                    lineTo(w * 0.78f, h * 0.56f)
                    lineTo(w * 0.86f, h * 0.50f)
                    close()
                }
                drawPath(roofPath, Color.White, style = Fill)

                // Corpo da casa
                val houseBody = Path().apply {
                    moveTo(w * 0.24f, h * 0.52f)
                    lineTo(w * 0.24f, h * 0.86f)
                    lineTo(w * 0.76f, h * 0.86f)
                    lineTo(w * 0.76f, h * 0.52f)
                    lineTo(w * 0.50f, h * 0.30f)
                    close()
                }
                drawPath(houseBody, Color.White, style = Fill)

                // Porta em arco terracota
                val doorPath = Path().apply {
                    moveTo(w * 0.42f, h * 0.86f)
                    lineTo(w * 0.42f, h * 0.66f)
                    cubicTo(
                        w * 0.42f, h * 0.58f,
                        w * 0.58f, h * 0.58f,
                        w * 0.58f, h * 0.66f
                    )
                    lineTo(w * 0.58f, h * 0.86f)
                    close()
                }
                drawPath(doorPath, Color(0xFFB84C26), style = Fill)

                // Coração dourado no topo com pulsação suave
                val heartCenterX = w * 0.50f
                val heartCenterY = h * 0.46f
                val heartSize = w * 0.13f * heartPulse

                val heartPath = Path().apply {
                    moveTo(heartCenterX, heartCenterY + heartSize * 0.55f)
                    cubicTo(
                        heartCenterX - heartSize * 0.75f, heartCenterY + heartSize * 0.15f,
                        heartCenterX - heartSize * 0.75f, heartCenterY - heartSize * 0.45f,
                        heartCenterX - heartSize * 0.20f, heartCenterY - heartSize * 0.45f
                    )
                    cubicTo(
                        heartCenterX, heartCenterY - heartSize * 0.35f,
                        heartCenterX, heartCenterY - heartSize * 0.25f,
                        heartCenterX, heartCenterY - heartSize * 0.15f
                    )
                    cubicTo(
                        heartCenterX, heartCenterY - heartSize * 0.25f,
                        heartCenterX, heartCenterY - heartSize * 0.35f,
                        heartCenterX + heartSize * 0.20f, heartCenterY - heartSize * 0.45f
                    )
                    cubicTo(
                        heartCenterX + heartSize * 0.75f, heartCenterY - heartSize * 0.45f,
                        heartCenterX + heartSize * 0.75f, heartCenterY + heartSize * 0.15f,
                        heartCenterX, heartCenterY + heartSize * 0.55f
                    )
                    close()
                }
                drawPath(heartPath, Color(0xFFFFD166), style = Fill)
            }
        }
    }
}

/**
 * Sol Radiante Animado (Fase 2):
 * - Disco solar dourado com pulsação suave
 * - 12 Raios solares com rotação sutil e expansão rítmica
 */
@Composable
private fun AnimatedRadiantSunLogo() {
    val infiniteTransition = rememberInfiniteTransition(label = "radiant_sun")

    // Rotação suave e lenta dos raios
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(28000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sun_rotation"
    )

    // Pulsação suave do brilho do sol
    val sunPulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sun_pulse"
    )

    Box(
        modifier = Modifier.size(130.dp),
        contentAlignment = Alignment.Center
    ) {
        // 12 Raios solares com pontas arredondadas e rotação
        Canvas(
            modifier = Modifier
                .size(126.dp)
                .rotate(rotationAngle)
                .scale(sunPulse)
        ) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val innerRadius = size.width * 0.28f
            val outerRadius = size.width * 0.46f

            for (i in 0 until 12) {
                val angle = Math.toRadians((i * 30.0)).toFloat()
                val startX = cx + innerRadius * cos(angle)
                val startY = cy + innerRadius * sin(angle)
                val endX = cx + outerRadius * cos(angle)
                val endY = cy + outerRadius * sin(angle)

                drawLine(
                    color = Color(0xFFE89E62),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 3.2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        // Disco solar central dourado/laranja com gradiente
        Canvas(
            modifier = Modifier
                .size(62.dp)
                .scale(sunPulse)
        ) {
            val cx = size.width / 2f
            val cy = size.height / 2f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFF97316),
                        Color(0xFFDC5B16)
                    ),
                    center = Offset(cx, cy),
                    radius = size.width / 2f
                )
            )

            // Aro interno dourado suave
            drawCircle(
                color = Color(0xFFFFD166).copy(alpha = 0.5f),
                radius = (size.width / 2f) - 2.5.dp.toPx(),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx())
            )
        }
    }
}
