package com.tasbeeh.digital.presentation.themes.skins

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TallyCounterSkin(
    count: Int,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val countString = count.toString().padStart(4, '0')

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFDCD6CD))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onTap
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(310.dp, 360.dp)
                .shadow(24.dp, RoundedCornerShape(40.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFEDEDED), Color(0xFFB0B0B0), Color(0xFF7A7A7A))
                    ),
                    shape = RoundedCornerShape(40.dp)
                )
                .border(4.dp, Color(0xFFFFFFFF), RoundedCornerShape(40.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF1F1F1F), RoundedCornerShape(12.dp))
                        .border(3.dp, Color(0xFF4A4A4A), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        countString.forEach { digitChar ->
                            RollingDialWheel(digit = digitChar.digitToInt())
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Brush.radialGradient(listOf(Color(0xFFEEEEEE), Color(0xFF888888))),
                            CircleShape
                        )
                        .border(2.dp, Color(0xFFFFFFFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "TAP",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun RollingDialWheel(digit: Int) {
    val animatedOffset = remember { Animatable(0f) }

    LaunchedEffect(digit) {
        animatedOffset.animateTo(
            targetValue = digit.toFloat(),
            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = Modifier
            .size(width = 46.dp, height = 70.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White)
            .border(1.dp, Color.Black.copy(alpha = 0.2f), RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        val currentNumber = (animatedOffset.value.toInt()) % 10
        val fractionalPart = animatedOffset.value - animatedOffset.value.toInt()

        Column(
            modifier = Modifier.graphicsLayer {
                translationY = -fractionalPart * 70.dp.toPx()
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentNumber.toString(),
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                color = Color.Black
            )
            Text(
                text = ((currentNumber + 1) % 10).toString(),
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                color = Color.Black
            )
        }
    }
}
