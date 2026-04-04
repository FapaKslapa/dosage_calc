package com.example.dosagecalc.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun WavySlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    activeColor: Color,
    inactiveColor: Color
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        valueRange = valueRange,
        colors = SliderDefaults.colors(
            thumbColor = activeColor,
            activeTrackColor = Color.Transparent,
            inactiveTrackColor = Color.Transparent
        ),
        track = { sliderState ->
            val fraction = (value - valueRange.start) /
                    (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.01f)

            Canvas(modifier = Modifier.fillMaxWidth().height(48.dp)) {
                val waveLength = 30.dp.toPx()
                val amplitude = 4.dp.toPx()
                val midY = size.height / 2f
                val activeWidth = size.width * fraction

                if (activeWidth < size.width) {
                    drawLine(
                        color = inactiveColor,
                        start = Offset(activeWidth, midY),
                        end = Offset(size.width, midY),
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                if (activeWidth > 0) {
                    val path = Path()
                    path.moveTo(0f, midY)
                    var x = 0f
                    while (x <= activeWidth) {
                        val phase = (x / waveLength) * 2 * kotlin.math.PI
                        val y = midY + kotlin.math.sin(phase).toFloat() * amplitude
                        path.lineTo(x, y)
                        x += 2f
                    }
                    drawPath(
                        path = path,
                        color = activeColor,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }
    )
}

