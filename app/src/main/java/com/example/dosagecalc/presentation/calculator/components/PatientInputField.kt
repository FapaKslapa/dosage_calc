package com.example.dosagecalc.presentation.calculator.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dosagecalc.presentation.ui.components.WavySlider
import com.example.dosagecalc.presentation.ui.theme.LocalDosageShapes
import com.example.dosagecalc.presentation.ui.theme.spacing

@Composable
fun PatientInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    sliderValue: Float,
    onSliderChange: (Float) -> Unit,
    sliderRange: ClosedFloatingPointRange<Float>,
    suffix: String,
    keyboardType: KeyboardType = KeyboardType.Decimal,
    imeAction: ImeAction = ImeAction.Next,
    activeColor: Color,
    inactiveColor: Color,
    errorMessage: String?,
    hintMessage: String?
) {
    val haptic = LocalHapticFeedback.current
    var lastHapticValue by remember { mutableFloatStateOf(sliderValue) }
    val sp = MaterialTheme.spacing
    val shapes = LocalDosageShapes.current

    Card(
        shape = shapes.tile,
        colors = CardDefaults.cardColors(containerColor = inactiveColor.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = sp.base, end = sp.base, top = sp.md, bottom = sp.sm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = activeColor,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.width(100.dp),
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        textAlign = TextAlign.Center
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        imeAction = imeAction
                    ),
                    singleLine = true,
                    suffix = { Text(suffix, color = activeColor) },
                    shape = shapes.field,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = activeColor,
                        unfocusedIndicatorColor = activeColor.copy(alpha = 0.45f),
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
            WavySlider(
                value = sliderValue,
                onValueChange = { newValue ->
                    if (kotlin.math.abs(newValue - lastHapticValue) >= 1f) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        lastHapticValue = newValue
                    }
                    onSliderChange(newValue)
                },
                valueRange = sliderRange,
                activeColor = activeColor,
                inactiveColor = inactiveColor
            )
        }
    }
    if (errorMessage != null) {
        Text(
            errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = sp.xs, top = 2.dp)
        )
    } else if (hintMessage != null) {
        Text(
            hintMessage,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = sp.xs, top = 2.dp)
        )
    }
}
