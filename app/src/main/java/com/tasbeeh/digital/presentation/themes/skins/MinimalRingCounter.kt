package com.tasbeeh.digital.presentation.themes.skins

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val EmeraldBg = Color(0xFF0D3B3E)
private val DeepGold = Color(0xFFF5A623)
private val BrightAmber = Color(0xFFFFD166)
private val TrackColor = Color(0x26FFFFFF)

@Composable
fun MinimalRingCounter(
    count: Int,
    target: Int,
    progress: Float,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedSweep by animateFloatAsState(
        targetValue = progress * 360f,
        animationSpec = tween(durationMillis = 350),
        label = "SweepAnimation"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EmeraldBg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onTap
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(300.dp)) {
            val strokeWidth = 14.dp.toPx()
            val arcSize = size.width - strokeWidth
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

            drawArc(
                color = TrackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(arcSize, arcSize),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(DeepGold, BrightAmber, DeepGold),
                    center = center
                ),
                startAngle = -90f,
                sweepAngle = animatedSweep,
                useCenter = false,
                topLeft = topLeft,
                size = Size(arcSize, arcSize),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 260.dp)
                .background(DeepGold, shape = CircleShape)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "%",
                color = EmeraldBg,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = count.toString(),
                color = BrightAmber,
                fontSize = 72.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = "GOAL: ",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
