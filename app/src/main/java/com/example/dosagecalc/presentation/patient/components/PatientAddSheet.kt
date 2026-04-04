package com.example.dosagecalc.presentation.patient.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.dosagecalc.presentation.calculator.components.AnthropometricInputsGroup
import com.example.dosagecalc.presentation.ui.components.ImpairmentChipsRow

@Composable
fun PatientAddSheet(
    onDismiss: () -> Unit,
    onSave: (name: String, surname: String, weight: String, height: String?, age: String, renalImpairment: Boolean, hepaticImpairment: Boolean) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var renalStage by remember { mutableStateOf(com.example.dosagecalc.domain.model.RenalStage.NONE) }
    var hepaticStage by remember { mutableStateOf(com.example.dosagecalc.domain.model.HepaticStage.NONE) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp)
        ) {
            Text(
                text = "Nuova Cartella",
                style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Serif),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Inserisci i dati per velocizzare i futuri calcoli",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = { Text("Cognome") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                AnthropometricInputsGroup(
                    weightValue = weight,
                    heightValue = height,
                    ageValue = age,
                    onWeightChanged = { weight = it },
                    onHeightChanged = { height = it },
                    onAgeChanged = { age = it },
                    heightLabel = "Altezza (opz)",
                    verticalSpacing = 12.dp
                )

                Spacer(modifier = Modifier.height(24.dp))
                Text("Patologie Concomitanti", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                ImpairmentChipsRow(
                    renalStage = renalStage,
                    hepaticStage = hepaticStage,
                    onRenalStageChanged = { renalStage = it },
                    onHepaticStageChanged = { hepaticStage = it }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            Button(
                onClick = {
                    if (name.isNotBlank() && surname.isNotBlank() && weight.isNotBlank()) {
                        val hasRenal = renalStage != com.example.dosagecalc.domain.model.RenalStage.NONE
                        val hasHepatic = hepaticStage != com.example.dosagecalc.domain.model.HepaticStage.NONE
                        onSave(name, surname, weight, height.ifBlank { null }, age, hasRenal, hasHepatic)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .navigationBarsPadding()
                    .height(56.dp),
                shape = RoundedCornerShape(50)
            ) {
                Text("Salva Paziente", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
