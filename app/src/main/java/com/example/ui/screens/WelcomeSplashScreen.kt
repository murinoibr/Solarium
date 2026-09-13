package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * LaunchPage Oficial Solarium - Abertura Cinematográfica Ouro & Mandala
 *
 * Reproduz com precisão a sequência visual do vídeo de abertura da marca SOLARIUM:
 * 1. Fundo Champagne / Marfim Luxuoso (#F6F3ED a #DDD8CE) com vinheta suave e partículas de ouro flutuantes.
 * 2. Desabrochar da Mandala Solar Dourada (Geometria Sagrada floral de 16 pontas com detalhes de ourivesaria).
 * 3. Fita de Luz Dourada em Órbita Elíptica 3D ao redor do centro com rastro cintilante de poeira estelar.
 * 4. Wordmark "SOLARIUM" em tipografia tridimensional branca lapidar, com sombra nobre projetada sobre a mandala e bisel dourado.
 * 5. Explosão de Raios Solares Radiantes (Sunburst Dourado) emanando por trás da mandala.
 * 6. Toque em qualquer lugar ou botão "Entrar no Guia" para avanço imediato, com transição automática graciosa.
 */
@Composable
fun WelcomeSplashScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Linha do tempo da animação (0.0s a 8.5s)
    val animTime = remember { Animatable(0f) }

    // Rotação sutil contínua e pulsação de respiração pós-revelação
    val infiniteTransition = rememberInfiniteTransition(label = "continuous_ambient")
    val ambientRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(75000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ambient_rot"
    )
    val ambientBreathing by infiniteTransition.animateFloat(
        initialValue = 0.985f,
        targetValue = 1.015f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambient_pulse"
    )

    LaunchedEffect(Unit) {
        launch {
            animTime.animateTo(
                targetValue = 8.5f,
                animationSpec = tween(durationMillis = 8500, easing = LinearEasing)
            )
            onFinish()
        }
    }

    val t = animTime.value

    // Cálculos de progresso por fase conforme o vídeo:
    // Fase 1: Desabrochar inicial da mandala e primeiras partículas (0s a 2.5s)
    val mandalaBloom = (t / 2.2f).coerceIn(0f, 1f)
    val mandalaScale = (0.3f + 0.7f * (t / 2.0f).coerceIn(0f, 1f)) * ambientBreathing

    // Fase 2: Fita de luz dourada orbitando em 3D (0.8s a 4.8s)
    val ribbonProgress = ((t - 0.8f) / 3.6f).coerceIn(0f, 1f)
    val ribbonAlpha = when {
        t < 0.8f -> 0f
        t < 1.4f -> (t - 0.8f) / 0.6f
        t > 4.2f -> (1f - (t - 4.2f) / 0.8f).coerceAtLeast(0f)
        else -> 1f
    }

    // Fase 3: Wordmark SOLARIUM 3D branco e sombra (4.0s em diante)
    val titleAlpha = if (t < 3.8f) {
        (t / 3.8f).coerceIn(0f, 0.35f) // contorno dourado translúcido inicial
    } else {
        ((t - 3.8f) / 1.0f).coerceIn(0f, 1f)
    }
    val isTitleSolid = t >= 4.5f

    // Fase 4: Explosão de Raios Solares Dourados (5.4s a 7.5s+)
    val sunburstProgress = ((t - 5.2f) / 1.6f).coerceIn(0f, 1f)
    val sunburstAlpha = ((t - 5.2f) / 0.8f).coerceIn(0f, 1f)

    // Fade-in dos elementos de texto secundários e botão
    val footerAlpha = ((t - 5.8f) / 0.8f).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFAF8F3),
                        Color(0xFFF3EFE7),
                        Color(0xFFE5DFC),
                        Color(0xFFD6CFC1)
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onFinish
            )
            .testTag("welcome_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // 1. Partículas douradas e poeira estelar cintilante de fundo
        GoldenDustBackground(animTime = t)

        // 2. Composição da Mandala, Sunburst e Órbita Dourada
        Box(
            modifier = Modifier
                .size(360.dp)
                .scale(mandalaScale),
            contentAlignment = Alignment.Center
        ) {
            // A. Raios Solares Radiantes (Sunburst) disparando por trás da mandala
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

            // B. Mandala Solar Dourada com detalhes de filigrana
            Canvas(
                modifier = Modifier
                    .size(310.dp)
                    .alpha(mandalaBloom)
            ) {
                drawGoldenMandala(
                    bloomProgress = mandalaBloom,
                    rotationDegrees = ambientRotation * 0.15f
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

            // D. Halo de Luz e Brilho Central
            Canvas(modifier = Modifier.size(160.dp)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFF9E6).copy(alpha = 0.85f * titleAlpha),
                            Color(0xFFFFDF88).copy(alpha = 0.40f * titleAlpha),
                            Color.Transparent
                        )
                    )
                )
            }

            // E. Wordmark Central "SOLARIUM"
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (isTitleSolid) {
                    // Tipografia Tridimensional: Camada de Sombra Profunda + Texto Branco + Bisel Dourado
                    Box(contentAlignment = Alignment.Center) {
                        // Sombra projetada sobre a mandala
                        Text(
                            text = "SOLARIUM",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 35.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 8.sp,
                                fontFamily = FontFamily.Serif
                            ),
                            color = Color(0xFF6B5838).copy(alpha = 0.55f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, start = 2.dp)
                        )

                        // Texto Branco Puro em Alto Relevo
                        Text(
                            text = "SOLARIUM",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 35.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 8.sp,
                                fontFamily = FontFamily.Serif
                            ),
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.shadow(
                                elevation = 6.dp,
                                shape = RoundedCornerShape(4.dp),
                                ambientColor = Color(0xFFC59E3F),
                                spotColor = Color(0xFF9E7B28)
                            )
                        )
                    }
                } else {
                    // Estado Inicial: Letras em Fio de Ouro Translúcido
                    Text(
                        text = "SOLARIUM",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 8.sp,
                            fontFamily = FontFamily.Serif
                        ),
                        color = Color(0xFFB8933A).copy(alpha = titleAlpha),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 3. Rodapé Nobre com Subtítulo, Boas-Vindas e Botão de Entrada
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp, start = 24.dp, end = 24.dp)
                .alpha(footerAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Subtítulo Oficial
            Text(
                text = "ESTÂNCIA & REFÚGIO • SÃO LOURENÇO, MG",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.4.sp
                ),
                color = Color(0xFF9C7438),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Frase de Acolhimento
            Text(
                text = "Bem-vindos à sua casa na Mantiqueira",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.Serif
                ),
                color = Color(0xFF5A4D3E),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botão Dourado Elegante para Entrar
            Button(
                onClick = onFinish,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC59E3F),
                    contentColor = Color(0xFF241B0E)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .height(46.dp)
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
                            fontSize = 14.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Entrar",
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Toque em qualquer lugar para pular",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = Color(0xFF8F8271).copy(alpha = 0.85f)
            )
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

    // Cores metálicas nobres de ouro puro
    val goldDeep = Color(0xFF9E7B28)
    val goldMain = Color(0xFFC9A23E)
    val goldLight = Color(0xFFE8C86E)
    val goldBright = Color(0xFFFFF0B8)

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

        // Núcleo brilhante branco-dourado
        drawCircle(
            color = Color(0xFFFFFBE8).copy(alpha = alpha * 0.9f),
            radius = radius * 0.6f,
            center = Offset(px, py)
        )
        // Halo dourado envolvente
        drawCircle(
            color = Color(0xFFFFD54F).copy(alpha = alpha * 0.55f),
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
                Color(0xFFFFF0B0),
                Color(0xFFFFC107).copy(alpha = 0.5f),
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
    val maxRayLength = (size.width * 0.58f * progress * pulse)

    val rayCount = 24
    for (i in 0 until rayCount) {
        val angle = (i * 2 * PI / rayCount).toFloat()
        val isMajor = i % 2 == 0

        val rayLength = if (isMajor) maxRayLength else maxRayLength * 0.70f
        val tipX = cx + (baseRadius + rayLength) * cos(angle)
        val tipY = cy + (baseRadius + rayLength) * sin(angle)

        val halfWidthAngle = (PI / (rayCount * 3.5f)).toFloat()
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
                    Color(0xFFFFDF7A).copy(alpha = 0.85f),
                    Color(0xFFE5B036).copy(alpha = 0.50f),
                    Color(0xFFC59E3F).copy(alpha = 0.15f),
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
                // Fragmento triangular dourado
                val shardSize = p.size.dp.toPx()
                val shardPath = Path().apply {
                    moveTo(px, py - shardSize)
                    lineTo(px + shardSize * 0.8f, py + shardSize * 0.6f)
                    lineTo(px - shardSize * 0.8f, py + shardSize * 0.6f)
                    close()
                }
                drawPath(
                    path = shardPath,
                    color = Color(0xFFE2C26E).copy(alpha = alpha),
                    style = Fill
                )
            } else {
                // Ponto de luz cintilante
                drawCircle(
                    color = Color(0xFFFFF0B8).copy(alpha = alpha),
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
