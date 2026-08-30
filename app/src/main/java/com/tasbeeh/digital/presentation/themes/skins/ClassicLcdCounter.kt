package com.tasbeeh.digital.presentation.themes.skins

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val IndustrialBezel = Color(0xFF1E1E24)
private val MatteLcdScreen = Color(0xFFA3C1AD)
private val LcdGhostSegment = Color(0x1A1B3B22)
private val LcdActiveSegment = Color(0xFF0F381E)

@Composable
fun ClassicLcdCounter(
    count: Int,
    rounds: Int,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isButtonPressed by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121214))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = IndustrialBezel)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .background(MatteLcdScreen, RoundedCornerShape(8.dp))
                    .border(2.dp, Color(0xFF6B8E76), RoundedCornerShape(8.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("ROUNDS: ", color = LcdActiveSegment, fontSize = 14.sp)
                    Text("DIGITAL-77", color = LcdActiveSegment, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                SevenSegmentDisplay(
                    value = count.toString().padStart(4, '0'),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(bottom = 40.dp)
                .size(190.dp)
                .shadow(if (isButtonPressed) 2.dp else 12.dp, CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = if (isButtonPressed) listOf(Color(0xFF3A3D40), Color(0xFF1F2022))
                        else listOf(Color(0xFF5A5E63), Color(0xFF2C2D30))
                    ),
                    shape = CircleShape
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isButtonPressed = true
                            tryAwaitRelease()
                            isButtonPressed = false
                            onTap()
                        }
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = if (isButtonPressed) listOf(Color(0xFF242528), Color(0xFF3A3C40))
                            else listOf(Color(0xFF45484D), Color(0xFF1B1C1E))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "COUNT",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 20.sp,
                    letterSpacing = 3.sp
                )
            }
        }
    }
}

@Composable
fun SevenSegmentDisplay(value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        value.forEach { char ->
            SevenSegmentDigit(
                digit = char.digitToIntOrNull() ?: 0,
                modifier = Modifier
                    .width(44.dp)
                    .height(72.dp)
                    .padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
fun SevenSegmentDigit(digit: Int, modifier: Modifier = Modifier) {
    val segmentPatterns = listOf(
        booleanArrayOf(true, true, true, true, true, true, false),
        booleanArrayOf(false, true, true, false, false, false, false),
        booleanArrayOf(true, true, false, true, true, false, true),
        booleanArrayOf(true, true, true, true, false, false, true),
        booleanArrayOf(false, true, true, false, false, true, true),
        booleanArrayOf(true, false, true, true, false, true, true),
        booleanArrayOf(true, false, true, true, true, true, true),
        booleanArrayOf(true, true, true, false, false, false, false),
        booleanArrayOf(true, true, true, true, true, true, true),
        booleanArrayOf(true, true, true, true, false, true, true)
    )
    val active = segmentPatterns.getOrElse(digit) { segmentPatterns[0] }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val thick = 5.dp.toPx()

        fun drawSeg(on: Boolean, x: Float, y: Float, width: Float, height: Float) {
            drawRect(
                color = if (on) LcdActiveSegment else LcdGhostSegment,
                topLeft = Offset(x, y),
                size = Size(width, height)
            )
        }

        drawSeg(active[0], thick, 0f, w - 2 * thick, thick)
        drawSeg(active[1], w - thick, thick, thick, (h / 2) - thick)
        drawSeg(active[2], w - thick, h / 2, thick, (h / 2) - thick)
        drawSeg(active[3], thick, h - thick, w - 2 * thick, thick)
        drawSeg(active[4], 0f, h / 2, thick, (h / 2) - thick)
        drawSeg(active[5], 0f, thick, thick, (h / 2) - thick)
        drawSeg(active[6], thick, (h - thick) / 2, w - 2 * thick, thick)
    }
}
