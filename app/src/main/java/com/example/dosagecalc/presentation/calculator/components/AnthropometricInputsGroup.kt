package com.example.dosagecalc.presentation.calculator.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.Locale

/**
 * Gruppo riutilizzabile di campi antropometrici (peso, altezza, età) con slider integrati.
 * Usato sia in PatientInputScreen (calcolo) che nel form di aggiunta paziente.
 */
@Composable
fun AnthropometricInputsGroup(
    weightValue: String,
    heightValue: String,
    ageValue: String,
    onWeightChanged: (String) -> Unit,
    onHeightChanged: (String) -> Unit,
    onAgeChanged: (String) -> Unit,
    showHeight: Boolean = true,
    heightLabel: String = "Altezza",
    weightError: String? = null,
    heightError: String? = null,
    ageError: String? = null,
    weightHint: String? = null,
    heightHint: String? = null,
    ageHint: String? = null,
    verticalSpacing: Dp = 24.dp
) {
    PatientInputField(
        label = "Peso",
        value = weightValue,
        onValueChange = onWeightChanged,
        sliderValue = weightValue.toFloatOrNull() ?: 0f,
        onSliderChange = { onWeightChanged(String.format(Locale.US, "%.1f", it)) },
        sliderRange = 1f..150f,
        suffix = "kg",
        activeColor = MaterialTheme.colorScheme.primary,
        inactiveColor = MaterialTheme.colorScheme.primaryContainer,
        errorMessage = weightError,
        hintMessage = weightHint
    )

    if (showHeight) {
        Spacer(modifier = Modifier.height(verticalSpacing))
        PatientInputField(
            label = heightLabel,
            value = heightValue,
            onValueChange = onHeightChanged,
            sliderValue = heightValue.toFloatOrNull() ?: 0f,
            onSliderChange = { onHeightChanged(it.toInt().toString()) },
            sliderRange = 10f..250f,
            suffix = "cm",
            activeColor = MaterialTheme.colorScheme.secondary,
            inactiveColor = MaterialTheme.colorScheme.secondaryContainer,
            errorMessage = heightError,
            hintMessage = heightHint
        )
    }

    Spacer(modifier = Modifier.height(verticalSpacing))

    PatientInputField(
        label = "Età",
        value = ageValue,
        onValueChange = onAgeChanged,
        sliderValue = ageValue.toFloatOrNull() ?: 0f,
        onSliderChange = { onAgeChanged(it.toInt().toString()) },
        sliderRange = 0f..120f,
        suffix = "anni",
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Done,
        activeColor = MaterialTheme.colorScheme.tertiary,
        inactiveColor = MaterialTheme.colorScheme.tertiaryContainer,
        errorMessage = ageError,
        hintMessage = ageHint
    )
}
