package com.example.dosagecalc.presentation.calculator.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dosagecalc.domain.model.FormulaType
import com.example.dosagecalc.presentation.calculator.CalculatorViewModel

@Composable
fun PatientInputScreen(
    viewModel: CalculatorViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToResult: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.dosageResult) {
        if (uiState.dosageResult != null) onNavigateToResult()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
                    .statusBarsPadding()
                    .padding(bottom = 28.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 36.dp, y = (-20).dp)
                        .background(Color.White.copy(alpha = 0.07f), CircleShape)
                )

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Indietro",
                                tint               = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Text(
                            text  = "Dati del Paziente",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }

                    uiState.selectedDrug?.let { drug ->
                        Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 4.dp)) {
                            Text(
                                text  = drug.name,
                                style = MaterialTheme.typography.headlineMedium.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Serif),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text  = drug.indication,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 100.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors    = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text  = "Dati Antropometrici",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text  = "Valori validati in tempo reale",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        val weightVal = uiState.weightInput.toFloatOrNull() ?: 0f
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Peso",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            OutlinedTextField(
                                value = uiState.weightInput.ifEmpty { "" },
                                onValueChange = viewModel::onWeightChanged,
                                modifier = Modifier.width(80.dp).height(50.dp),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = androidx.compose.ui.text.style.TextAlign.Center),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                                singleLine = true,
                                suffix = { Text("kg") },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                )
                            )
                        }
                        WavySlider(
                            value = weightVal,
                            onValueChange = { viewModel.onWeightChanged(String.format(java.util.Locale.US, "%.1f", it)) },
                            valueRange = 1f..150f,
                            activeColor = MaterialTheme.colorScheme.primary,
                            inactiveColor = MaterialTheme.colorScheme.primaryContainer
                        )
                        uiState.weightError?.let {
                            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        } ?: uiState.selectedDrug?.minWeightKg?.let {
                            Text("Minimo richiesto: $it kg", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                        }

                        if (uiState.selectedDrug?.formulaType == FormulaType.PER_M2) {
                            Spacer(modifier = Modifier.height(24.dp))
                            val heightVal = uiState.heightInput.toFloatOrNull() ?: 0f
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Altezza",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                OutlinedTextField(
                                    value = uiState.heightInput.ifEmpty { "" },
                                    onValueChange = viewModel::onHeightChanged,
                                    modifier = Modifier.width(80.dp).height(50.dp),
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = androidx.compose.ui.text.style.TextAlign.Center),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                                    singleLine = true,
                                    suffix = { Text("cm") },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent
                                    )
                                )
                            }
                            WavySlider(
                                value = heightVal,
                                onValueChange = { viewModel.onHeightChanged(it.toInt().toString()) },
                                valueRange = 10f..250f,
                                activeColor = MaterialTheme.colorScheme.secondary,
                                inactiveColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                            uiState.heightError?.let {
                                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                            } ?: Text("Necessaria per il calcolo BSA (Mosteller)", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        val ageVal = uiState.ageInput.toFloatOrNull() ?: 0f
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Età",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            OutlinedTextField(
                                value = uiState.ageInput.ifEmpty { "" },
                                onValueChange = viewModel::onAgeChanged,
                                modifier = Modifier.width(80.dp).height(50.dp),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = androidx.compose.ui.text.style.TextAlign.Center),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                                singleLine = true,
                                suffix = { Text("anni") },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                )
                            )
                        }
                        WavySlider(
                            value = ageVal,
                            onValueChange = { viewModel.onAgeChanged(it.toInt().toString()) },
                            valueRange = 0f..120f,
                            activeColor = MaterialTheme.colorScheme.tertiary,
                            inactiveColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                        uiState.ageError?.let {
                            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        } ?: uiState.selectedDrug?.minAgeYears?.let {
                            Text("Età minima richiesta: $it anni", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background.copy(alpha = 0f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Button(
                onClick  = viewModel::calculateDosage,
                enabled  = uiState.canCalculate && !uiState.isCalculating,
                shape    = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (uiState.isCalculating) {
                    CircularProgressIndicator(
                        modifier  = Modifier.size(24.dp),
                        color     = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text  = "Calcola Dose",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
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

                // Inactive track (straight)
                if (activeWidth < size.width) {
                    drawLine(
                        color = inactiveColor,
                        start = Offset(activeWidth, midY),
                        end = Offset(size.width, midY),
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // Active track (wavy)
                if (activeWidth > 0) {
                    val path = Path()
                    path.moveTo(0f, midY)
                    var x = 0f
                    while (x <= activeWidth) {
                        val phase = (x / waveLength) * 2 * Math.PI
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
