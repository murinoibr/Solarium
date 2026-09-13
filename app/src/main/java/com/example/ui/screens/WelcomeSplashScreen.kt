package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sin

/**
 * LaunchPage Oficial Solarium - Abertura Cinematográfica Ouro & Mandala
 *
 * Reprodução 1:1 rigorosa do vídeo da marca SOLARIUM:
 * 1. Fundo neutro de estúdio com vinheta suave em tons de cinza-linho aquecido (#E6E4DE a #B8B2A6).
 * 2. Partículas e fragmentos triangulares flutuantes de poeira dourada antiga (#B89C5D).
 * 3. Desabrochar da Mandala Solar em ourivesaria de ouro acetinado (#B59A58 e #8F7638).
 * 4. Fita de luz luminosa em órbita elíptica 3D (#FFFFFF com halo #E5C270 e cauda de faíscas).
 * 5. Wordmark "SOLARIUM" fiel ao vídeo:
 *    - Início com letras vazadas em fio de ouro translúcido.
 *    - Revelação em tipografia geométrica moderna Sans-Serif, branco puro (#FFFFFF) com sombra 3D projetada (#483F31).
 * 6. Explosão de raios solares dourados radiantes (Sunburst) emanando por trás da mandala.
 * 7. Tela limpa e minimalista sem poluição visual, exatamente como no vídeo de abertura.
 */
@Composable
fun WelcomeSplashScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Linha do tempo da animação (0.0s a 4.0s - dinâmica e fluida)
    val animTime = remember { Animatable(0f) }

    // Respiração suave e rotação sutil pós-revelação
    val infiniteTransition = rememberInfiniteTransition(label = "continuous_ambient")
    val ambientRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(90000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ambient_rot"
    )
    val ambientBreathing by infiniteTransition.animateFloat(
        initialValue = 0.99f,
        targetValue = 1.01f,
        animationSpec = infiniteRepeatable(
            animation = tween(3600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambient_pulse"
    )

    LaunchedEffect(Unit) {
        launch {
            animTime.animateTo(
                targetValue = 4.0f,
                animationSpec = tween(durationMillis = 3800, easing = LinearEasing)
            )
            onFinish()
        }
    }

    val t = animTime.value

    // Progresso das fases correspondendo aos frames do vídeo (escala ajustada para 4.0s):
    // Fase 1: Desabrochar da mandala e primeiras partículas (0s a 1.4s)
    val mandalaBloom = (t / 1.3f).coerceIn(0f, 1f)
    val mandalaScale = (0.35f + 0.65f * (t / 1.2f).coerceIn(0f, 1f)) * ambientBreathing

    // Fase 2: Fita de luz dourada em órbita elíptica 3D (0.4s a 2.4s)
    val ribbonProgress = ((t - 0.4f) / 1.8f).coerceIn(0f, 1f)
    val ribbonAlpha = when {
        t < 0.4f -> 0f
        t < 0.8f -> (t - 0.4f) / 0.4f
        t > 2.2f -> (1f - (t - 2.2f) / 0.4f).coerceAtLeast(0f)
        else -> 1f
    }

    // Fase 3: Wordmark SOLARIUM (Letras vazadas -> Sólido 3D Branco)
    val outlineAlpha = if (t < 2.0f) {
        (t / 1.0f).coerceIn(0f, 0.45f)
    } else {
        (1f - (t - 2.0f) / 0.3f).coerceAtLeast(0f)
    }
    val solidAlpha = ((t - 2.0f) / 0.4f).coerceIn(0f, 1f)

    // Fase 4: Explosão de Raios Solares (Sunburst) (2.5s a 3.8s)
    val sunburstProgress = ((t - 2.4f) / 0.9f).coerceIn(0f, 1f)
    val sunburstAlpha = ((t - 2.4f) / 0.5f).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxSize()
            // Fundo de estúdio exatamente como no vídeo: gradiente radial com vinheta suave
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFE8E6E0), // Centro neutro claro
                        Color(0xFFE2DFD9), // Meio linho suave
                        Color(0xFFD1CDC4), // Transição
                        Color(0xFFBDB7AC)  // Borda externa vinhetada
                    )
                )
            )
            .pointerInput(Unit) {
                detectTapGestures {
                    onFinish()
                }
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onFinish
            )
            .testTag("welcome_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // 1. Partículas douradas e pequenos fragmentos triangulares flutuantes
        GoldenDustBackground(animTime = t)

        // 2. Botão "Pular ›" no canto superior direito para avanço instantâneo
        Surface(
            onClick = onFinish,
            shape = RoundedCornerShape(20.dp),
            color = Color(0x22FFFFFF),
            contentColor = Color(0xFF3D362A),
            border = BorderStroke(1.dp, Color(0x33B59A58)),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = 16.dp, end = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "Pular",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.5.sp,
                        letterSpacing = 0.5.sp
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Pular introdução",
                    modifier = Modifier.size(13.dp)
                )
            }
        }

        // 3. Composição Central: Mandala, Raios Solares, Órbita e Wordmark SOLARIUM
        Box(
            modifier = Modifier
                .size(360.dp)
                .scale(mandalaScale),
            contentAlignment = Alignment.Center
        ) {
            // A. Raios Solares Radiantes (Sunburst) emanando por trás da mandala
            if (sunburstProgress > 0f) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(sunburstAlpha)
                ) {
                    drawSunburstRays(
                        progress = sunburstProgress,
                        pulse = ambientBreathing
                    )
                }
            }

            // B. Mandala Solar em Ouro Acetinado Antigo
            Canvas(
                modifier = Modifier
                    .size(310.dp)
                    .alpha(mandalaBloom)
            ) {
                drawGoldenMandala(
                    bloomProgress = mandalaBloom,
                    rotationDegrees = ambientRotation * 0.12f
                )
            }

            // C. Fita de Luz Dourada em Órbita Elíptica 3D
            if (ribbonAlpha > 0f) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(ribbonAlpha)
                ) {
                    drawGoldenOrbitRibbon(
                        progress = ribbonProgress
                    )
                }
            }

            // D. Halo de Luz suave no centro
            if (t >= 3.5f) {
                Canvas(modifier = Modifier.size(170.dp)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFAF7EE).copy(alpha = 0.70f * solidAlpha),
                                Color(0xFFE8D7A8).copy(alpha = 0.30f * solidAlpha),
                                Color.Transparent
                            )
                        )
                    )
                }
            }

            // E. Wordmark Central "SOLARIUM" (Fiel à tipografia e 3D do vídeo)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                // Estado 1: Letras vazadas em fio de ouro fino (0s a ~4.5s)
                if (outlineAlpha > 0f) {
                    Text(
                        text = "SOLARIUM",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 36.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 8.sp,
                            fontFamily = FontFamily.SansSerif
                        ),
                        color = Color(0xFFB59A58).copy(alpha = outlineAlpha),
                        textAlign = TextAlign.Center
                    )
                }

                // Estado 2: Letras brancas 3D sólidas com sombra projetada (4.2s em diante)
                if (solidAlpha > 0f) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.alpha(solidAlpha)
                    ) {
                        // Sombra projetada para baixo e direita sobre a mandala
                        Text(
                            text = "SOLARIUM",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 8.sp,
                                fontFamily = FontFamily.SansSerif
                            ),
                            color = Color(0xFF433B2E).copy(alpha = 0.65f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, start = 2.5.dp)
                        )

                        // Sombra secundária difusa
                        Text(
                            text = "SOLARIUM",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 8.sp,
                                fontFamily = FontFamily.SansSerif
                            ),
                            color = Color(0xFF635845).copy(alpha = 0.35f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 2.dp, start = 1.dp)
                        )

                        // Letras Brancas Puras em Alto Relevo
                        Text(
                            text = "SOLARIUM",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 8.sp,
                                fontFamily = FontFamily.SansSerif
                            ),
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // 4. Botão Inferior "Entrar no Guia" (Interativo, com feedback tátil e touch target de 48dp)
        Surface(
            onClick = onFinish,
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFB59A58),
            contentColor = Color(0xFF221A0E),
            shadowElevation = 4.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
                .testTag("launch_btn_enter")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 13.dp)
            ) {
                Text(
                    text = "Entrar no Guia",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 0.5.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Entrar no Guia da Casa",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Desenha a Mandala Solar Sagrada de 16 Pontas com precisão de ourivesaria geométrica.
 */
private fun DrawScope.drawGoldenMandala(
    bloomProgress: Float,
    rotationDegrees: Float
) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val maxRadius = (size.width / 2f) * 0.95f * bloomProgress

    // Cores metálicas nobres de ouro acetinado antigo (extraídas fielmente do vídeo)
    val goldDeep = Color(0xFF8C7338)
    val goldMain = Color(0xFFB59A58)
    val goldLight = Color(0xFFCCB477)
    val goldBright = Color(0xFFEADAA4)

    val baseAngleRad = Math.toRadians(rotationDegrees.toDouble()).toFloat()

    // 1. Centro: Pequeno ponto solar e aro interno
    drawCircle(
        color = goldLight,
        radius = 4.dp.toPx() * bloomProgress,
        center = Offset(cx, cy),
        style = Fill
    )
    drawCircle(
        color = goldMain,
        radius = 14.dp.toPx() * bloomProgress,
        center = Offset(cx, cy),
        style = Stroke(width = 1.2.dp.toPx())
    )

    // 2. Camada de 8 Pétalas Internas de Lótus
    val innerPetals = 8
    val innerRadius = maxRadius * 0.28f
    for (i in 0 until innerPetals) {
        val angle = baseAngleRad + (i * 2 * PI / innerPetals).toFloat()
        val halfAngle = (PI / innerPetals).toFloat()

        val startX = cx + (maxRadius * 0.08f) * cos(angle - halfAngle)
        val startY = cy + (maxRadius * 0.08f) * sin(angle - halfAngle)
        val endX = cx + (maxRadius * 0.08f) * cos(angle + halfAngle)
        val endY = cy + (maxRadius * 0.08f) * sin(angle + halfAngle)
        val tipX = cx + innerRadius * cos(angle)
        val tipY = cy + innerRadius * sin(angle)

        val ctrlX1 = cx + (innerRadius * 0.7f) * cos(angle - halfAngle * 0.55f)
        val ctrlY1 = cy + (innerRadius * 0.7f) * sin(angle - halfAngle * 0.55f)
        val ctrlX2 = cx + (innerRadius * 0.7f) * cos(angle + halfAngle * 0.55f)
        val ctrlY2 = cy + (innerRadius * 0.7f) * sin(angle + halfAngle * 0.55f)

        val petalPath = Path().apply {
            moveTo(startX, startY)
            quadraticTo(ctrlX1, ctrlY1, tipX, tipY)
            quadraticTo(ctrlX2, ctrlY2, endX, endY)
            close()
        }
        drawPath(
            path = petalPath,
            color = goldLight,
            style = Stroke(width = 1.1.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }

    // 3. Aro Circular Intermediário com 16 Pérolas Douradas
    val ring1Radius = maxRadius * 0.32f
    drawCircle(
        color = goldMain,
        radius = ring1Radius,
        center = Offset(cx, cy),
        style = Stroke(width = 1.0.dp.toPx())
    )

    for (i in 0 until 16) {
        val dotAngle = baseAngleRad + (i * 2 * PI / 16).toFloat()
        val dotX = cx + ring1Radius * cos(dotAngle)
        val dotY = cy + ring1Radius * sin(dotAngle)
        drawCircle(
            color = goldBright,
            radius = 1.4.dp.toPx() * bloomProgress,
            center = Offset(dotX, dotY),
            style = Fill
        )
    }

    // 4. Camada de 16 Pétalas Intermediárias com nervura central
    val midPetals = 16
    val midRadius = maxRadius * 0.58f
    val baseMidRadius = maxRadius * 0.33f
    for (i in 0 until midPetals) {
        val angle = baseAngleRad + (i * 2 * PI / midPetals).toFloat()
        val step = (PI / midPetals).toFloat()

        val pLeftX = cx + baseMidRadius * cos(angle - step)
        val pLeftY = cy + baseMidRadius * sin(angle - step)
        val pRightX = cx + baseMidRadius * cos(angle + step)
        val pRightY = cy + baseMidRadius * sin(angle + step)
        val tipX = cx + midRadius * cos(angle)
        val tipY = cy + midRadius * sin(angle)

        val c1X = cx + (midRadius * 0.75f) * cos(angle - step * 0.5f)
        val c1Y = cy + (midRadius * 0.75f) * sin(angle - step * 0.5f)
        val c2X = cx + (midRadius * 0.75f) * cos(angle + step * 0.5f)
        val c2Y = cy + (midRadius * 0.75f) * sin(angle + step * 0.5f)

        val path = Path().apply {
            moveTo(pLeftX, pLeftY)
            quadraticTo(c1X, c1Y, tipX, tipY)
            quadraticTo(c2X, c2Y, pRightX, pRightY)
        }
        drawPath(
            path = path,
            color = goldMain,
            style = Stroke(width = 1.2.dp.toPx(), cap = StrokeCap.Round)
        )

        // Nervura central da pétala
        val innerVeinX = cx + baseMidRadius * cos(angle)
        val innerVeinY = cy + baseMidRadius * sin(angle)
        drawLine(
            color = goldLight,
            start = Offset(innerVeinX, innerVeinY),
            end = Offset(tipX * 0.95f + cx * 0.05f, tipY * 0.95f + cy * 0.05f),
            strokeWidth = 0.8.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    // 5. Segundo Anel de Delimitação
    val ring2Radius = maxRadius * 0.60f
    drawCircle(
        color = goldDeep,
        radius = ring2Radius,
        center = Offset(cx, cy),
        style = Stroke(width = 1.0.dp.toPx())
    )

    // 6. As 16 Grandes Pétalas Externas da Coroa Solar Solarium (Gothic/Ogee Arches)
    val outerPetals = 16
    val outerTipRadius = maxRadius * 0.98f
    val outerBaseRadius = maxRadius * 0.61f

    for (i in 0 until outerPetals) {
        val angle = baseAngleRad + (i * 2 * PI / outerPetals).toFloat()
        val step = (PI / outerPetals).toFloat()

        val baseLeftX = cx + outerBaseRadius * cos(angle - step)
        val baseLeftY = cy + outerBaseRadius * sin(angle - step)
        val baseRightX = cx + outerBaseRadius * cos(angle + step)
        val baseRightY = cy + outerBaseRadius * sin(angle + step)

        val apexX = cx + outerTipRadius * cos(angle)
        val apexY = cy + outerTipRadius * sin(angle)

        // Curvatura Ogee (Gótica lanceolada)
        val ctrl1X = cx + (outerTipRadius * 0.72f) * cos(angle - step * 0.7f)
        val ctrl1Y = cy + (outerTipRadius * 0.72f) * sin(angle - step * 0.7f)
        val ctrl2X = cx + (outerTipRadius * 0.88f) * cos(angle - step * 0.15f)
        val ctrl2Y = cy + (outerTipRadius * 0.88f) * sin(angle - step * 0.15f)

        val ctrl3X = cx + (outerTipRadius * 0.88f) * cos(angle + step * 0.15f)
        val ctrl3Y = cy + (outerTipRadius * 0.88f) * sin(angle + step * 0.15f)
        val ctrl4X = cx + (outerTipRadius * 0.72f) * cos(angle + step * 0.7f)
        val ctrl4Y = cy + (outerTipRadius * 0.72f) * sin(angle + step * 0.7f)

        val petalPath = Path().apply {
            moveTo(baseLeftX, baseLeftY)
            cubicTo(ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, apexX, apexY)
            cubicTo(ctrl3X, ctrl3Y, ctrl4X, ctrl4Y, baseRightX, baseRightY)
        }

        drawPath(
            path = petalPath,
            color = goldMain,
            style = Stroke(width = 1.3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Filigrana interna ornamental em cada pétala
        val innerApexX = cx + (outerTipRadius * 0.82f) * cos(angle)
        val innerApexY = cy + (outerTipRadius * 0.82f) * sin(angle)
        val innerPath = Path().apply {
            moveTo(
                cx + (outerBaseRadius * 1.08f) * cos(angle - step * 0.6f),
                cy + (outerBaseRadius * 1.08f) * sin(angle - step * 0.6f)
            )
            quadraticTo(
                cx + (outerTipRadius * 0.68f) * cos(angle - step * 0.3f),
                cy + (outerTipRadius * 0.68f) * sin(angle - step * 0.3f),
                innerApexX,
                innerApexY
            )
            quadraticTo(
                cx + (outerTipRadius * 0.68f) * cos(angle + step * 0.3f),
                cy + (outerTipRadius * 0.68f) * sin(angle + step * 0.3f),
                cx + (outerBaseRadius * 1.08f) * cos(angle + step * 0.6f),
                cy + (outerBaseRadius * 1.08f) * sin(angle + step * 0.6f)
            )
        }
        drawPath(
            path = innerPath,
            color = goldLight.copy(alpha = 0.8f),
            style = Stroke(width = 0.9.dp.toPx())
        )

        // Pérola dourada na ponta de cada ápice
        drawCircle(
            color = goldBright,
            radius = 2.0.dp.toPx() * bloomProgress,
            center = Offset(apexX, apexY),
            style = Fill
        )
        drawCircle(
            color = goldDeep,
            radius = 2.0.dp.toPx() * bloomProgress,
            center = Offset(apexX, apexY),
            style = Stroke(width = 0.7.dp.toPx())
        )
    }
}

/**
 * Desenha a Fita de Luz Dourada em Órbita Elíptica 3D ao redor da mandala (Fase 2 do vídeo).
 */
private fun DrawScope.drawGoldenOrbitRibbon(
    progress: Float
) {
    val cx = size.width / 2f
    val cy = size.height / 2f

    // Inclinação da órbita 3D em ~28 graus
    val tiltAngle = Math.toRadians(-28.0).toFloat()
    val rx = size.width * 0.44f
    val ry = size.height * 0.22f

    // Ângulo da cabeça da fita conforme o progresso
    val sweepAngle = progress * 3.2f * (2 * PI).toFloat()
    val tailLength = (PI * 0.75f).toFloat() // Cauda luminosa de ~135 graus

    val steps = 36
    for (i in 0 until steps) {
        val frac = i.toFloat() / steps
        val currentTheta = sweepAngle - (tailLength * frac)

        // Coordenadas elípticas no plano inclinado
        val unrotatedX = rx * cos(currentTheta)
        val unrotatedY = ry * sin(currentTheta)

        val px = cx + (unrotatedX * cos(tiltAngle) - unrotatedY * sin(tiltAngle))
        val py = cy + (unrotatedX * sin(tiltAngle) + unrotatedY * cos(tiltAngle))

        val alpha = (1f - frac) * (1f - frac)
        val radius = (4.5f * (1f - frac * 0.7f)).dp.toPx()

        // Núcleo brilhante branco puro
        drawCircle(
            color = Color.White.copy(alpha = alpha * 0.95f),
            radius = radius * 0.6f,
            center = Offset(px, py)
        )
        // Halo dourado envolvente
        drawCircle(
            color = Color(0xFFE5C46E).copy(alpha = alpha * 0.60f),
            radius = radius * 1.5f,
            center = Offset(px, py)
        )
    }

    // Centelha de luz intensa na cabeça da fita
    val headTheta = sweepAngle
    val headX = cx + (rx * cos(headTheta) * cos(tiltAngle) - ry * sin(headTheta) * sin(tiltAngle))
    val headY = cy + (rx * cos(headTheta) * sin(tiltAngle) + ry * sin(headTheta) * cos(tiltAngle))

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White,
                Color(0xFFFFF2C2),
                Color(0xFFE0BE68).copy(alpha = 0.5f),
                Color.Transparent
            ),
            center = Offset(headX, headY),
            radius = 22.dp.toPx()
        ),
        radius = 22.dp.toPx(),
        center = Offset(headX, headY)
    )
}

/**
 * Desenha a Explosão de Raios Solares Radiantes (Sunburst Dourado) emanando por trás da mandala.
 */
private fun DrawScope.drawSunburstRays(
    progress: Float,
    pulse: Float
) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val baseRadius = size.width * 0.22f
    val maxRayLength = (size.width * 0.72f * progress * pulse)

    val rayCount = 32
    for (i in 0 until rayCount) {
        val angle = (i * 2 * PI / rayCount).toFloat()
        val isMajor = i % 2 == 0

        val rayLength = if (isMajor) maxRayLength else maxRayLength * 0.62f
        val tipX = cx + (baseRadius + rayLength) * cos(angle)
        val tipY = cy + (baseRadius + rayLength) * sin(angle)

        val halfWidthAngle = (PI / (rayCount * 3.8f)).toFloat()
        val b1X = cx + baseRadius * cos(angle - halfWidthAngle)
        val b1Y = cy + baseRadius * sin(angle - halfWidthAngle)
        val b2X = cx + baseRadius * cos(angle + halfWidthAngle)
        val b2Y = cy + baseRadius * sin(angle + halfWidthAngle)

        val rayPath = Path().apply {
            moveTo(b1X, b1Y)
            lineTo(tipX, tipY)
            lineTo(b2X, b2Y)
            close()
        }

        drawPath(
            path = rayPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFF7E298).copy(alpha = 0.85f),
                    Color(0xFFD6AF52).copy(alpha = 0.45f),
                    Color(0xFFB58E3A).copy(alpha = 0.12f),
                    Color.Transparent
                ),
                start = Offset(cx, cy),
                end = Offset(tipX, tipY)
            )
        )
    }
}

/**
 * Fundo de poeira estelar dourada, triângulos e partículas flutuantes sutis.
 */
@Composable
private fun GoldenDustBackground(animTime: Float) {
    // Lista fixa determinística de 28 partículas estelares para performance e fluidez
    val particles = remember {
        List(28) { index ->
            val angle = (index * 13.0) * (PI / 180.0)
            val dist = 0.15f + (index % 7) * 0.11f
            val size = 2.0f + (index % 4) * 1.5f
            val isShard = index % 3 == 0
            val speed = 0.2f + (index % 5) * 0.15f
            ParticleDef(angle.toFloat(), dist, size, isShard, speed)
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height / 2f

        particles.forEach { p ->
            val currentDist = (p.baseDist * size.width + sin(animTime * p.speed + p.angle) * 12.dp.toPx())
            val currentAngle = p.angle + animTime * 0.04f * p.speed

            val px = cx + currentDist * cos(currentAngle)
            val py = cy + currentDist * sin(currentAngle)

            val alpha = (0.25f + 0.55f * sin(animTime * 1.8f + p.angle)).coerceIn(0.1f, 0.85f)

            if (p.isShard) {
                // Fragmento triangular dourado metálico acetinado
                val shardSize = p.size.dp.toPx()
                val shardPath = Path().apply {
                    moveTo(px, py - shardSize)
                    lineTo(px + shardSize * 0.8f, py + shardSize * 0.6f)
                    lineTo(px - shardSize * 0.8f, py + shardSize * 0.6f)
                    close()
                }
                drawPath(
                    path = shardPath,
                    color = Color(0xFFBFA567).copy(alpha = alpha * 0.85f),
                    style = Fill
                )
            } else {
                // Ponto de luz cintilante
                drawCircle(
                    color = Color(0xFFE8D6A6).copy(alpha = alpha * 0.85f),
                    radius = p.size.dp.toPx() * 0.6f,
                    center = Offset(px, py)
                )
            }
        }
    }
}

private data class ParticleDef(
    val angle: Float,
    val baseDist: Float,
    val size: Float,
    val isShard: Boolean,
    val speed: Float
)
