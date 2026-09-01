package com.tasbeeh.digital.presentation.themes.skins

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val DeepBackground = Color(0xFF0F172A)
private val CordColor = Color(0xFFD1D5DB)
private val GlowAmber = Color(0xFFFBBF24)

@Composable
fun MisbahaBeadsSkin(
    count: Int,
    target: Int,
    rounds: Int,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedCount = remember { Animatable(count.toFloat()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(count) {
        animatedCount.animateTo(
            targetValue = count.toFloat(),
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBackground)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onTap() }
                )
            }
            .pointerInput(Unit) {
                var accumulatedDrag = 0f
                detectDragGestures(
                    onDragEnd = { accumulatedDrag = 0f },
                    onDragCancel = { accumulatedDrag = 0f },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        accumulatedDrag += dragAmount.y
                        if (accumulatedDrag < -45f || accumulatedDrag > 45f) {
                            onTap()
                            accumulatedDrag = 0f
                        }
                    }
                )
            }
    ) {
        // HUD Overlay on Left
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 28.dp, bottom = 40.dp)
        ) {
            Text(
                text = "$count",
                color = Color(0xFF10B981),
                fontSize = 54.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "/ $target",
                color = Color(0xFF34D399).copy(alpha = 0.8f),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Round: $rounds",
                color = GlowAmber,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Curved 3D Misbaha Beads Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Center of the curved arc (positioned slightly off-screen to the left for a majestic right-side curve)
            val arcCenterX = width * 0.15f
            val arcCenterY = height * 0.52f
            val arcRadius = width * 0.72f

            // Angular spacing between consecutive beads (in radians)
            val angleStep = 0.19f
            val currentOffset = animatedCount.value

            // Draw the fine silk cord running along the curved trajectory
            val cordPath = Path()
            var firstPoint = true
            val sampleSteps = 60
            for (step in 0..sampleSteps) {
                val t = -1.2f + (step.toFloat() / sampleSteps) * 2.4f
                val angle = t
                val px = arcCenterX + arcRadius * cos(angle)
                val py = arcCenterY + arcRadius * sin(angle)
                if (firstPoint) {
                    cordPath.moveTo(px, py)
                    firstPoint = false
                } else {
                    cordPath.lineTo(px, py)
                }
            }

            // Draw Cord Shadow & Cord Line
            drawPath(
                path = cordPath,
                color = Color.Black.copy(alpha = 0.35f),
                style = Stroke(width = 4.5.dp.toPx(), cap = StrokeCap.Round)
            )
            drawPath(
                path = cordPath,
                color = CordColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Render 3D Beads along the Curved Path
            val beadRadius = 26.dp.toPx()
            val visibleSpan = 12

            for (i in -visibleSpan..visibleSpan) {
                val beadIndex = (currentOffset.toInt() + i)
                val relativePosition = i - (currentOffset - currentOffset.toInt())
                val angle = relativePosition * angleStep

                val bx = arcCenterX + arcRadius * cos(angle)
                val by = arcCenterY + arcRadius * sin(angle)

                // Only draw beads currently in visible bounds
                if (bx in -beadRadius..(width + beadRadius) && by in -beadRadius..(height + beadRadius)) {
                    val isSpecialMarker = (beadIndex > 0 && beadIndex % 33 == 0)
                    val isFocalBead = (i == 0)

                    draw3DGlossyBead(
                        center = Offset(bx, by),
                        radius = if (isSpecialMarker) beadRadius * 1.15f else beadRadius,
                        isSpecialMarker = isSpecialMarker,
                        isFocalBead = isFocalBead
                    )
                }
            }
        }
    }
}

/**
 * Renders an ultra-realistic 3D glossy gemstone bead with specular reflections,
 * ambient shadows, depth gradients, and subsurface scattering.
 */
private fun DrawScope.draw3DGlossyBead(
    center: Offset,
    radius: Float,
    isSpecialMarker: Boolean,
    isFocalBead: Boolean
) {
    val lightOffset = Offset(center.x - radius * 0.32f, center.y - radius * 0.32f)

    // 1. Drop Shadow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x99000000), Color.Transparent),
            center = Offset(center.x + radius * 0.18f, center.y + radius * 0.22f),
            radius = radius * 1.25f
        ),
        radius = radius * 1.25f,
        center = Offset(center.x + radius * 0.18f, center.y + radius * 0.22f)
    )

    // 2. Base Sphere 3D Volume Gradient (Emerald / Amber Gemstone)
    val sphereGradientColors = if (isSpecialMarker) {
        listOf(
            Color(0xFFFEF08A), // Light Amber Peak
            Color(0xFFF59E0B), // Vibrant Amber
            Color(0xFFD97706), // Deep Gold
            Color(0xFFB45309), // Burnished Amber
            Color(0xFF78350F), // Dark Amber Shadow
            Color(0xFF451A03)  // Deep Occlusion
        )
    } else {
        listOf(
            Color(0xFF6EE7B7), // Mint/Turquoise Light
            Color(0xFF10B981), // Emerald Highlight
            Color(0xFF059669), // Rich Emerald Body
            Color(0xFF047857), // Deep Jade
            Color(0xFF064E3B), // Dark Shadow Core
            Color(0xFF022C22)  // Deep Occlusion
        )
    }

    drawCircle(
        brush = Brush.radialGradient(
            colors = sphereGradientColors,
            center = lightOffset,
            radius = radius * 1.35f
        ),
        radius = radius,
        center = center
    )

    // 3. Subsurface Bounce Light / Translucency (Bottom-Right Glow)
    val bounceGlowColor = if (isSpecialMarker) Color(0x66FDE68A) else Color(0x5534D399)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(bounceGlowColor, Color.Transparent),
            center = Offset(center.x + radius * 0.35f, center.y + radius * 0.35f),
            radius = radius * 0.75f
        ),
        radius = radius * 0.75f,
        center = Offset(center.x + radius * 0.35f, center.y + radius * 0.35f)
    )

    // 4. Primary Soft Specular Highlight
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = 0.85f), Color.White.copy(alpha = 0.2f), Color.Transparent),
            center = Offset(center.x - radius * 0.35f, center.y - radius * 0.35f),
            radius = radius * 0.45f
        ),
        radius = radius * 0.45f,
        center = Offset(center.x - radius * 0.35f, center.y - radius * 0.35f)
    )

    // 5. Crisp Glossy Glint Point (Glass Reflection)
    drawCircle(
        color = Color.White,
        radius = radius * 0.16f,
        center = Offset(center.x - radius * 0.38f, center.y - radius * 0.38f)
    )

    // Secondary micro-sparkle
    drawCircle(
        color = Color.White.copy(alpha = 0.7f),
        radius = radius * 0.07f,
        center = Offset(center.x - radius * 0.22f, center.y - radius * 0.48f)
    )

    // 6. Subtle Outer Edge Rim Light
    drawCircle(
        color = Color.White.copy(alpha = 0.15f),
        radius = radius,
        center = center,
        style = Stroke(width = 1.dp.toPx())
    )

    // 7. Focal Bead Halo (Active Bead Selection Indicator)
    if (isFocalBead) {
        drawCircle(
            color = GlowAmber.copy(alpha = 0.28f),
            radius = radius * 1.45f,
            center = center,
            style = Stroke(width = 2.5.dp.toPx())
        )
    }
}