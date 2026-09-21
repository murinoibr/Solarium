package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * Launch Screen Oficial Solarium — Fidelidade Exata ao Vídeo da Marca.
 *
 * Fases da Animação (9 segundos):
 * 1. 0.0s - 2.0s: Surgimento da Mandala Dourada em camadas concêntricas e texto "SOLARIUM" em contorno dourado metálico.
 * 2. 2.0s - 4.5s: Fita de luz dourada luminosa em órbita 3D circundando a mandala com partículas e triângulos flutuantes.
 * 3. 4.5s - 6.5s: Dissipação da fita de luz e transição das letras vazadas para o texto "SOLARIUM" em BRANCO SÓLIDO 3D de alto relevo.
 * 4. 6.5s - 9.0s: CLÍMAX — Explosão de Raios Solares Dourados Triunfais (Sunburst) irradiando por trás do texto e da mandala!
 */
@Composable
fun WelcomeSplashScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isDismissed by remember { mutableStateOf(false) }

    val triggerFinish: () -> Unit = remember(onFinish) {
        {
            if (!isDismissed) {
                isDismissed = true
                onFinish()
            }
        }
    }

    // Progresso mestre de 0f a 1f representando 9 segundos
    val timelineProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        timelineProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 9000, easing = LinearEasing)
        )
        delay(600)
        triggerFinish()
    }

    val p = timelineProgress.value

    // Sub-fases da animação
    val mandalaAlpha = min(1f, p / 0.20f)
    val mandalaScale = 0.82f + 0.18f * min(1f, p / 0.35f)

    // Fita de luz 3D (ativa entre 18% e 55%)
    val ribbonProgress = when {
        p < 0.18f -> 0f
        p > 0.55f -> 1f
        else -> (p - 0.18f) / 0.37f
    }
    val ribbonAlpha = when {
        p < 0.18f -> 0f
        p < 0.25f -> (p - 0.18f) / 0.07f
        p < 0.48f -> 1f
        p < 0.58f -> 1f - (p - 0.48f) / 0.10f
        else -> 0f
    }

    // Transição das letras contornadas douradas para o texto branco sólido 3D
    val outlineTextAlpha = when {
        p < 0.10f -> p / 0.10f
        p < 0.45f -> 1f
        p < 0.55f -> 1f - (p - 0.45f) / 0.10f
        else -> 0f
    }
    val solidTextAlpha = when {
        p < 0.48f -> 0f
        p < 0.58f -> (p - 0.48f) / 0.10f
        else -> 1f
    }
    val solidTextScale = when {
        p < 0.48f -> 0.94f
        p < 0.58f -> 0.94f + 0.06f * ((p - 0.48f) / 0.10f)
        else -> 1f
    }

    // Clímax dos Raios Solares Dourados (Sunburst - ativa a partir de 60%)
    val sunburstAlpha = when {
        p < 0.60f -> 0f
        p < 0.72f -> (p - 0.60f) / 0.12f
        p < 0.90f -> 1f
        else -> 1f - 0.15f * ((p - 0.90f) / 0.10f)
    }
    val sunburstScale = when {
        p < 0.60f -> 0.50f
        p < 0.75f -> 0.50f + 0.50f * ((p - 0.60f) / 0.15f)
        else -> 1f + 0.05f * sin((p - 0.75f) * 4f * PI.toFloat())
    }

    // Botão de entrada surge no clímax final
    val enterButtonAlpha = when {
        p < 0.72f -> 0f
        else -> min(1f, (p - 0.72f) / 0.12f)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            // Fundo de estúdio exatamente como no vídeo: gradiente radial suave bege/cinza quente
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFECE9E2), // Centro marfim suave
                        Color(0xFFE2DED6),
                        Color(0xFFD4CEC3),
                        Color(0xFFBFB8AB)  // Vinheta sutil nas bordas
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = triggerFinish
            )
            .testTag("welcome_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // 1. Poeira de Ouro e Fragmentos Triangulares Flutuantes (como no vídeo)
        GoldenParticlesAndFloatingShapes(
            progress = p,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Botão "Pular ›" no canto superior direito
        Surface(
            onClick = triggerFinish,
            shape = RoundedCornerShape(20.dp),
            color = Color.White.copy(alpha = 0.6f),
            contentColor = Color(0xFF3D362A),
            border = BorderStroke(1.dp, Color(0x66B59A58)),
            shadowElevation = 2.dp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = 16.dp, end = 20.dp)
                .testTag("btn_skip_launchpage")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "Pular",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Pular introdução",
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        // 3. Composição Central: Sunburst + Mandala + Fita de Luz + Tipografia SOLARIUM
        Box(
            modifier = Modifier
                .size(380.dp)
                .graphicsLayer {
                    alpha = mandalaAlpha
                    scaleX = mandalaScale
                    scaleY = mandalaScale
                },
            contentAlignment = Alignment.Center
        ) {
            // A. Clímax dos Raios de Sol Dourados Radiantes (Sunburst)
            if (sunburstAlpha > 0f) {
                GoldenSunburstCanvas(
                    alpha = sunburstAlpha,
                    scale = sunburstScale,
                    time = p * 9f,
                    modifier = Modifier.size(380.dp)
                )
            }

            // B. Mandala Sagrada Dourada com todas as camadas ricas do vídeo
            SolariumAuthenticMandalaCanvas(
                progress = min(1f, p / 0.35f),
                modifier = Modifier.size(340.dp)
            )

            // C. Fita de Luz Dourada Fluida em Órbita 3D com rastro brilhante
            if (ribbonAlpha > 0f) {
                GoldenRibbonOrbitalCanvas(
                    progress = ribbonProgress,
                    alpha = ribbonAlpha,
                    modifier = Modifier.size(360.dp)
                )
            }

            // D. Halo de luz suave no centro
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFFDF5).copy(alpha = 0.85f * (if (solidTextAlpha > 0f) 0.95f else 0.45f)),
                                Color(0xFFF7EAC7).copy(alpha = 0.40f * (if (solidTextAlpha > 0f) 0.80f else 0.30f)),
                                Color.Transparent
                            )
                        )
                    )
            )

            // E. Tipografia "SOLARIUM"
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // E1. Texto em Contorno Dourado Metálico (Fase Inicial 0s a ~4.5s)
                if (outlineTextAlpha > 0.01f) {
                    Text(
                        text = "SOLARIUM",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 9.sp,
                            fontFamily = FontFamily.SansSerif
                        ),
                        color = Color(0xFFB8964E).copy(alpha = outlineTextAlpha),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.graphicsLayer {
                            alpha = outlineTextAlpha
                        }
                    )
                }

                // E2. Texto em BRANCO SÓLIDO 3D com Alto Relevo e Sombra (Fase Final 4.5s a 9s)
                if (solidTextAlpha > 0.01f) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.graphicsLayer {
                            alpha = solidTextAlpha
                            scaleX = solidTextScale
                            scaleY = solidTextScale
                        }
                    ) {
                        // Sombra projetada profunda 3D
                        Text(
                            text = "SOLARIUM",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 35.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 9.sp,
                                fontFamily = FontFamily.SansSerif
                            ),
                            color = Color(0x6B2D2619),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.offset(x = 3.dp, y = 4.5.dp)
                        )

                        // Sombra de oclusão de contato suave
                        Text(
                            text = "SOLARIUM",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 35.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 9.sp,
                                fontFamily = FontFamily.SansSerif
                            ),
                            color = Color(0x3842361E),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.offset(x = 1.dp, y = 1.5.dp)
                        )

                        // Texto principal em Branco Puro Sólido
                        Text(
                            text = "SOLARIUM",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 35.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 9.sp,
                                fontFamily = FontFamily.SansSerif
                            ),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // 4. Rodapé: Botão Dourado de Entrada (surge majestosamente durante o clímax)
        if (enterButtonAlpha > 0.01f) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 44.dp)
                    .graphicsLayer {
                        alpha = enterButtonAlpha
                        translationY = (1f - enterButtonAlpha) * 30f
                    }
            ) {
                Button(
                    onClick = triggerFinish,
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC2A35D),
                        contentColor = Color(0xFF241C0F)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    ),
                    modifier = Modifier
                        .height(50.dp)
                        .padding(horizontal = 24.dp)
                        .testTag("launch_btn_enter")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Entrar no Guia",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Entrar no Guia da Casa",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Mandala Solar Autêntica com linhas nobres em dourado metálico.
 * Reflete rigorosamente a geometria do vídeo:
 * - Centro: flor de lótus de 8 pétalas internas com nervuras.
 * - Anel intermediário: cúspides/arcos e coroa de pétalas pontiagudas entrançadas.
 * - Camada externa: 16 pétalas góticas majestosas encimadas por hastes com pérolas e pequenos ornamentos.
 */
@Composable
private fun SolariumAuthenticMandalaCanvas(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val goldDeep = Color(0xFF9E813D)
    val goldMain = Color(0xFFC2A35D)
    val goldLight = Color(0xFFDFC687)
    val goldBright = Color(0xFFF7E8BA)

    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val baseR = size.width / 2f * 0.94f * progress

        if (baseR <= 5f) return@Canvas

        val strokeFine = 1.1.dp.toPx()
        val strokeBold = 1.6.dp.toPx()

        // 1. Círculos concêntricos de estrutura
        drawCircle(
            color = goldMain,
            radius = baseR * 0.22f,
            center = Offset(cx, cy),
            style = Stroke(width = strokeFine)
        )
        drawCircle(
            color = goldDeep,
            radius = baseR * 0.44f,
            center = Offset(cx, cy),
            style = Stroke(width = strokeFine)
        )
        drawCircle(
            color = goldMain,
            radius = baseR * 0.65f,
            center = Offset(cx, cy),
            style = Stroke(width = strokeFine)
        )
        drawCircle(
            color = goldLight,
            radius = baseR * 0.88f,
            center = Offset(cx, cy),
            style = Stroke(width = strokeFine)
        )

        // 2. Núcleo central: Ponto e 8 pétalas de lótus
        drawCircle(
            color = goldBright,
            radius = 2.5.dp.toPx(),
            center = Offset(cx, cy),
            style = Fill
        )

        val innerPetals = 8
        val innerBaseR = baseR * 0.08f
        val innerTipR = baseR * 0.22f
        for (i in 0 until innerPetals) {
            val angle = (i * 2.0 * PI / innerPetals).toFloat()
            val step = (PI / innerPetals).toFloat()

            val bx1 = cx + innerBaseR * cos(angle - step)
            val by1 = cy + innerBaseR * sin(angle - step)
            val bx2 = cx + innerBaseR * cos(angle + step)
            val by2 = cy + innerBaseR * sin(angle + step)
            val tx = cx + innerTipR * cos(angle)
            val ty = cy + innerTipR * sin(angle)

            // Pétala pontiaguda
            val path = Path().apply {
                moveTo(bx1, by1)
                quadraticTo(
                    cx + (innerTipR * 0.65f) * cos(angle - step * 0.5f),
                    cy + (innerTipR * 0.65f) * sin(angle - step * 0.5f),
                    tx, ty
                )
                quadraticTo(
                    cx + (innerTipR * 0.65f) * cos(angle + step * 0.5f),
                    cy + (innerTipR * 0.65f) * sin(angle + step * 0.5f),
                    bx2, by2
                )
            }
            drawPath(path, color = goldMain, style = Stroke(width = strokeFine, cap = StrokeCap.Round))

            // Nervura central da pétala
            drawLine(
                color = goldLight,
                start = Offset(cx + innerBaseR * cos(angle), cy + innerBaseR * sin(angle)),
                end = Offset(tx, ty),
                strokeWidth = strokeFine * 0.8f
            )
        }

        // 3. Anel de 16 pequenas cúspides e pérolas intermediárias
        val count16 = 16
        val ringR = baseR * 0.44f
        for (i in 0 until count16) {
            val angle = (i * 2.0 * PI / count16).toFloat()
            val px = cx + ringR * cos(angle)
            val py = cy + ringR * sin(angle)
            drawCircle(
                color = goldBright,
                radius = 1.8.dp.toPx(),
                center = Offset(px, py),
                style = Fill
            )
        }

        // 4. Camada intermediária de 16 pétalas de lótus entrelaçadas
        val midBaseR = baseR * 0.44f
        val midTipR = baseR * 0.65f
        for (i in 0 until count16) {
            val angle = (i * 2.0 * PI / count16).toFloat()
            val step = (PI / count16).toFloat()

            val bx1 = cx + midBaseR * cos(angle - step)
            val by1 = cy + midBaseR * sin(angle - step)
            val bx2 = cx + midBaseR * cos(angle + step)
            val by2 = cy + midBaseR * sin(angle + step)
            val tx = cx + midTipR * cos(angle)
            val ty = cy + midTipR * sin(angle)

            val path = Path().apply {
                moveTo(bx1, by1)
                quadraticTo(
                    cx + (midTipR * 0.72f) * cos(angle - step * 0.5f),
                    cy + (midTipR * 0.72f) * sin(angle - step * 0.5f),
                    tx, ty
                )
                quadraticTo(
                    cx + (midTipR * 0.72f) * cos(angle + step * 0.5f),
                    cy + (midTipR * 0.72f) * sin(angle + step * 0.5f),
                    bx2, by2
                )
            }
            drawPath(path, color = goldMain, style = Stroke(width = strokeFine, cap = StrokeCap.Round))

            // Detalhe interno da pétala intermediária
            val subTipX = cx + (midTipR * 0.85f) * cos(angle)
            val subTipY = cy + (midTipR * 0.85f) * sin(angle)
            drawLine(
                color = goldLight,
                start = Offset(cx + (midBaseR * 1.05f) * cos(angle), cy + (midBaseR * 1.05f) * sin(angle)),
                end = Offset(subTipX, subTipY),
                strokeWidth = strokeFine * 0.8f
            )
        }

        // 5. Camada externa majestosa: 16 grandes pétalas góticas com ornamentos pontiagudos
        val outerBaseR = baseR * 0.65f
        val outerTipR = baseR * 0.91f
        for (i in 0 until count16) {
            val angle = (i * 2.0 * PI / count16).toFloat()
            val step = (PI / count16).toFloat()

            val bx1 = cx + outerBaseR * cos(angle - step)
            val by1 = cy + outerBaseR * sin(angle - step)
            val bx2 = cx + outerBaseR * cos(angle + step)
            val by2 = cy + outerBaseR * sin(angle + step)
            val tx = cx + outerTipR * cos(angle)
            val ty = cy + outerTipR * sin(angle)

            // Pétala gótica com dupla curvatura elegante
            val petalPath = Path().apply {
                moveTo(bx1, by1)
                cubicTo(
                    cx + (outerBaseR + (outerTipR - outerBaseR) * 0.35f) * cos(angle - step * 0.85f),
                    cy + (outerBaseR + (outerTipR - outerBaseR) * 0.35f) * sin(angle - step * 0.85f),
                    cx + (outerBaseR + (outerTipR - outerBaseR) * 0.75f) * cos(angle - step * 0.25f),
                    cy + (outerBaseR + (outerTipR - outerBaseR) * 0.75f) * sin(angle - step * 0.25f),
                    tx, ty
                )
                cubicTo(
                    cx + (outerBaseR + (outerTipR - outerBaseR) * 0.75f) * cos(angle + step * 0.25f),
                    cy + (outerBaseR + (outerTipR - outerBaseR) * 0.75f) * sin(angle + step * 0.25f),
                    cx + (outerBaseR + (outerTipR - outerBaseR) * 0.35f) * cos(angle + step * 0.85f),
                    cy + (outerBaseR + (outerTipR - outerBaseR) * 0.35f) * sin(angle + step * 0.85f),
                    bx2, by2
                )
            }
            drawPath(
                path = petalPath,
                color = goldMain,
                style = Stroke(width = strokeBold, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Arco interno decorativo dentro da grande pétala
            val innerTx = cx + (outerTipR * 0.82f) * cos(angle)
            val innerTy = cy + (outerTipR * 0.82f) * sin(angle)
            val innerP = Path().apply {
                moveTo(
                    cx + (outerBaseR * 1.05f) * cos(angle - step * 0.6f),
                    cy + (outerBaseR * 1.05f) * sin(angle - step * 0.6f)
                )
                quadraticTo(
                    cx + (outerBaseR * 1.15f) * cos(angle),
                    cy + (outerBaseR * 1.15f) * sin(angle),
                    innerTx, innerTy
                )
                quadraticTo(
                    cx + (outerBaseR * 1.15f) * cos(angle),
                    cy + (outerBaseR * 1.15f) * sin(angle),
                    cx + (outerBaseR * 1.05f) * cos(angle + step * 0.6f),
                    cy + (outerBaseR * 1.05f) * sin(angle + step * 0.6f)
                )
            }
            drawPath(innerP, color = goldLight, style = Stroke(width = strokeFine * 0.8f))

            // Haste radial no topo de cada ápice com 3 pérolas douradas (exatamente como no vídeo)
            val dot1R = outerTipR + baseR * 0.025f
            val dot2R = outerTipR + baseR * 0.055f
            val dot3R = outerTipR + baseR * 0.082f

            drawCircle(color = goldMain, radius = 1.8.dp.toPx(), center = Offset(cx + dot1R * cos(angle), cy + dot1R * sin(angle)))
            drawCircle(color = goldBright, radius = 1.4.dp.toPx(), center = Offset(cx + dot2R * cos(angle), cy + dot2R * sin(angle)))
            drawCircle(color = goldDeep, radius = 1.0.dp.toPx(), center = Offset(cx + dot3R * cos(angle), cy + dot3R * sin(angle)))

            // Ponta intermediária suave entre as 16 pétalas grandes
            val midAngle = angle + step
            val interTipR = outerBaseR + (outerTipR - outerBaseR) * 0.45f
            drawLine(
                color = goldDeep,
                start = Offset(cx + outerBaseR * cos(midAngle), cy + outerBaseR * sin(midAngle)),
                end = Offset(cx + interTipR * cos(midAngle), cy + interTipR * sin(midAngle)),
                strokeWidth = strokeFine
            )
            drawCircle(
                color = goldBright,
                radius = 1.2.dp.toPx(),
                center = Offset(cx + interTipR * cos(midAngle), cy + interTipR * sin(midAngle))
            )
        }
    }
}

/**
 * Fita de Luz Dourada Fluida (Golden Ribbon Orbit) com cauda e rastro de luz.
 * Circunda a mandala em um movimento espiral 3D elíptico idêntico ao do vídeo (00:02 a 00:04).
 */
@Composable
private fun GoldenRibbonOrbitalCanvas(
    progress: Float,
    alpha: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val rx = size.width * 0.46f
        val ry = size.height * 0.28f

        // Ângulo da cabeça da fita: dá ~2 voltas completas em torno da mandala
        val totalRotation = 2.2f * 2f * PI.toFloat()
        val currentHeadAngle = -PI.toFloat() * 0.5f + totalRotation * progress
        val tailLength = 1.4f * PI.toFloat() // Extensão do rastro luminoso

        val steps = 30
        for (i in 0 until steps) {
            val ratio = i.toFloat() / steps
            val pointAngle = currentHeadAngle - tailLength * (1f - ratio)

            // Inclinação 3D da órbita elíptica (rotacionada ~25 graus)
            val tilt = -0.42f
            val rawX = rx * cos(pointAngle)
            val rawY = ry * sin(pointAngle)

            val rotX = rawX * cos(tilt) - rawY * sin(tilt)
            val rotY = rawX * sin(tilt) + rawY * cos(tilt)

            val px = cx + rotX
            val py = cy + rotY

            val pointAlpha = alpha * (ratio * ratio) * 0.95f
            val pointRadius = (0.8.dp.toPx() + 3.2.dp.toPx() * ratio)

            if (pointAlpha > 0.02f) {
                // Brilho exterior suave
                drawCircle(
                    color = Color(0xFFFDE68A).copy(alpha = pointAlpha * 0.4f),
                    radius = pointRadius * 2.2f,
                    center = Offset(px, py)
                )
                // Núcleo de ouro branco brilhante
                drawCircle(
                    color = Color(0xFFFFFBEB).copy(alpha = pointAlpha),
                    radius = pointRadius,
                    center = Offset(px, py)
                )
            }
        }

        // Cabeça da fita luminosa com faísca intensa
        val headRawX = rx * cos(currentHeadAngle)
        val headRawY = ry * sin(currentHeadAngle)
        val headTilt = -0.42f
        val headX = cx + (headRawX * cos(headTilt) - headRawY * sin(headTilt))
        val headY = cy + (headRawX * sin(headTilt) + headRawY * cos(headTilt))

        // Aura de luz na cabeça
        drawCircle(
            color = Color(0xFFFFFDF5).copy(alpha = alpha),
            radius = 6.dp.toPx(),
            center = Offset(headX, headY)
        )
        drawCircle(
            color = Color(0xFFFFE082).copy(alpha = alpha * 0.7f),
            radius = 12.dp.toPx(),
            center = Offset(headX, headY)
        )
    }
}

/**
 * Clímax dos Raios Solares Dourados (Sunburst Rays).
 * Reflete o ápice espetacular visto no vídeo em 00:07 a 00:09:
 * Feixes afiados de luz dourada intensa que brotam de trás do texto e da mandala.
 */
@Composable
private fun GoldenSunburstCanvas(
    alpha: Float,
    scale: Float,
    time: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val maxLen = size.width * 0.52f * scale

        // 1. Auréola Solar Central Brilhante
        val haloBrush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFFBEA).copy(alpha = alpha * 0.85f),
                Color(0xFFFFEEA8).copy(alpha = alpha * 0.55f),
                Color(0xFFE5BE64).copy(alpha = alpha * 0.25f),
                Color.Transparent
            ),
            center = Offset(cx, cy),
            radius = maxLen * 0.85f
        )
        drawCircle(
            brush = haloBrush,
            radius = maxLen * 0.85f,
            center = Offset(cx, cy)
        )

        // 2. 32 Feixes de Luz Dourada Afiados e Cintilantes (Sunburst)
        val rayCount = 32
        for (i in 0 until rayCount) {
            val angle = (i * 2.0 * PI / rayCount).toFloat()
            // Alternância entre raios longos majestosos e intermediários
            val isMajor = (i % 2 == 0)
            val isCardinal = (i % 4 == 0)

            val baseRayLen = when {
                isCardinal -> maxLen * 1.05f
                isMajor -> maxLen * 0.88f
                else -> maxLen * 0.65f
            }

            // Cintilação sutil nos raios
            val shimmer = 0.92f + 0.08f * sin(time * 3f + i)
            val rayLen = baseRayLen * shimmer

            val innerR = size.width * 0.12f
            val halfWidth = if (isCardinal) (PI / 80f).toFloat() else (PI / 120f).toFloat()

            val p1X = cx + innerR * cos(angle - halfWidth)
            val p1Y = cy + innerR * sin(angle - halfWidth)
            val p2X = cx + innerR * cos(angle + halfWidth)
            val p2Y = cy + innerR * sin(angle + halfWidth)
            val tipX = cx + rayLen * cos(angle)
            val tipY = cy + rayLen * sin(angle)

            val rayBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFFF9DB).copy(alpha = alpha * (if (isMajor) 0.85f else 0.55f)),
                    Color(0xFFE2BE68).copy(alpha = alpha * (if (isMajor) 0.60f else 0.35f)),
                    Color.Transparent
                ),
                start = Offset(cx, cy),
                end = Offset(tipX, tipY)
            )

            val rayPath = Path().apply {
                moveTo(p1X, p1Y)
                lineTo(tipX, tipY)
                lineTo(p2X, p2Y)
                close()
            }
            drawPath(path = rayPath, brush = rayBrush)
        }

        // 3. Estrelas de Brilho Solar Cintilante de 4 pontas nas pontas principais
        val sparkleAngles = listOf(0f, PI.toFloat() * 0.5f, PI.toFloat(), PI.toFloat() * 1.5f)
        for (a in sparkleAngles) {
            val dist = maxLen * 0.78f
            val sx = cx + dist * cos(a)
            val sy = cy + dist * sin(a)
            val sparkLen = 8.dp.toPx()

            drawLine(
                color = Color(0xFFFFFBEB).copy(alpha = alpha * 0.8f),
                start = Offset(sx - sparkLen, sy),
                end = Offset(sx + sparkLen, sy),
                strokeWidth = 1.5.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color(0xFFFFFBEB).copy(alpha = alpha * 0.8f),
                start = Offset(sx, sy - sparkLen),
                end = Offset(sx, sy + sparkLen),
                strokeWidth = 1.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * Poeira de Ouro e Fragmentos Triangulares Flutuantes (vistos claramente nos frames 00:02 e 00:03).
 */
@Composable
private fun GoldenParticlesAndFloatingShapes(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f

        // Pontos de partículas douradas com leve deriva orbital
        val particleOffsets = listOf(
            Offset(-110f, -140f), Offset(130f, -120f), Offset(-140f, 90f),
            Offset(120f, 130f), Offset(-60f, -180f), Offset(80f, -160f),
            Offset(-90f, 170f), Offset(70f, 180f), Offset(-170f, -40f),
            Offset(160f, 40f), Offset(-30f, 140f), Offset(40f, -120f)
        )

        particleOffsets.forEachIndexed { index, base ->
            val driftAngle = (progress * 2f * PI.toFloat() + index).toFloat()
            val px = cx + base.x.dp.toPx() + 6f * cos(driftAngle)
            val py = cy + base.y.dp.toPx() + 6f * sin(driftAngle)

            val pAlpha = (0.35f + 0.45f * sin((progress * 4f + index).toFloat())).coerceIn(0f, 1f)
            val r = if (index % 3 == 0) 1.8.dp.toPx() else 1.1.dp.toPx()

            drawCircle(
                color = Color(0xFFE5CA80).copy(alpha = pAlpha),
                radius = r,
                center = Offset(px, py)
            )
        }

        // Triângulos dourados flutuantes característicos do vídeo (frames 00:02 a 00:04)
        val triangles = listOf(
            Triple(Offset(-130f, 120f), 6.dp.toPx(), 0.6f),
            Triple(Offset(140f, -110f), 5.dp.toPx(), -0.4f),
            Triple(Offset(-90f, -130f), 4.5.dp.toPx(), 1.2f),
            Triple(Offset(110f, 140f), 5.5.dp.toPx(), -1.1f)
        )

        triangles.forEach { (pos, sizePx, rot) ->
            val tx = cx + pos.x.dp.toPx() + 8f * cos(progress * 3f + rot)
            val ty = cy + pos.y.dp.toPx() + 8f * sin(progress * 3f + rot)

            val triPath = Path().apply {
                moveTo(tx, ty - sizePx)
                lineTo(tx - sizePx * 0.86f, ty + sizePx * 0.5f)
                lineTo(tx + sizePx * 0.86f, ty + sizePx * 0.5f)
                close()
            }
            drawPath(
                path = triPath,
                color = Color(0xFFD4B160).copy(alpha = 0.55f)
            )
        }
    }
}
