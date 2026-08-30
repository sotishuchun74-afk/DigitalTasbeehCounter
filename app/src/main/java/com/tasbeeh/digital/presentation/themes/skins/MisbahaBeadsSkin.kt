package com.tasbeeh.digital.presentation.themes.skins

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val NightSky = Color(0xFF0F172A)
private val PearlGlow = Color(0xFFE2E8F0)
private val GoldenBead = Color(0xFFD97706)
private val AmberCore = Color(0xFFFBBF24)

@Composable
fun MisbahaBeadsSkin(
    count: Int,
    target: Int,
    rounds: Int,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val beadTranslation by animateFloatAsState(
        targetValue = count * 64f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 400f),
        label = "MisbahaBeadSpring"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NightSky)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onTap
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Count:  / ",
                color = PearlGlow,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Rounds: ",
                color = AmberCore,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { translationX = -beadTranslation }
                ) {
                    val centerY = size.height / 2
                    val beadSpacing = 64.dp.toPx()
                    val totalVisibleBeads = target + 20

                    drawLine(
                        color = Color(0x66FFFFFF),
                        start = Offset(-1000f, centerY),
                        end = Offset(totalVisibleBeads * beadSpacing + 1000f, centerY),
                        strokeWidth = 3.dp.toPx()
                    )

                    for (i in 0..totalVisibleBeads) {
                        val beadCenterX = (i + 1) * beadSpacing
                        val isSpecial = (i + 1) % 33 == 0

                        val brush = if (isSpecial) {
                            Brush.radialGradient(
                                colors = listOf(AmberCore, GoldenBead, Color(0xFF78350F)),
                                center = Offset(beadCenterX - 4, centerY - 4),
                                radius = 26.dp.toPx()
                            )
                        } else {
                            Brush.radialGradient(
                                colors = listOf(Color.White, PearlGlow, Color(0xFF64748B)),
                                center = Offset(beadCenterX - 4, centerY - 4),
                                radius = 20.dp.toPx()
                            )
                        }

                        drawCircle(
                            brush = brush,
                            radius = if (isSpecial) 24.dp.toPx() else 18.dp.toPx(),
                            center = Offset(beadCenterX, centerY)
                        )
                    }
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centerX = size.width / 2
                    drawCircle(
                        color = AmberCore.copy(alpha = 0.25f),
                        radius = 32.dp.toPx(),
                        center = Offset(centerX, size.height / 2)
                    )
                }
            }
        }
    }
}
