package com.example.dosagecalc.presentation.patient.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.dosagecalc.domain.model.HepaticStage
import com.example.dosagecalc.domain.model.Patient
import com.example.dosagecalc.domain.model.RenalStage
import com.example.dosagecalc.presentation.calculator.components.AnthropometricInputsGroup
import com.example.dosagecalc.presentation.ui.components.ImpairmentChipsRow
import com.example.dosagecalc.presentation.ui.util.isCompactHeight

@Composable
fun PatientAddSheet(
    onDismiss: () -> Unit,
    onSave: (name: String, surname: String, weight: String, height: String?, age: String, renalImpairment: Boolean, hepaticImpairment: Boolean) -> Unit,
    patientToEdit: Patient? = null
) {
    val isEditMode = patientToEdit != null
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf(patientToEdit?.name ?: "") }
    var surname by remember { mutableStateOf(patientToEdit?.surname ?: "") }
    var weight by remember { mutableStateOf(patientToEdit?.weightKg?.toString() ?: "") }
    var height by remember { mutableStateOf(patientToEdit?.heightCm?.toString() ?: "") }
    var age by remember { mutableStateOf(patientToEdit?.ageYears?.takeIf { it > 0 }?.toString() ?: "") }
    var renalStage by remember { mutableStateOf(
        if (patientToEdit?.hasRenalImpairment == true) RenalStage.G3 else RenalStage.NONE
    ) }
    var hepaticStage by remember { mutableStateOf(
        if (patientToEdit?.hasHepaticImpairment == true) HepaticStage.CHILD_B else HepaticStage.NONE
    ) }
    var weightError by remember { mutableStateOf<String?>(null) }
    var heightError by remember { mutableStateOf<String?>(null) }

    val canSave = name.isNotBlank() && surname.isNotBlank() && weight.isNotBlank()
        && weightError == null && heightError == null
    val isCompact = isCompactHeight()

    @Composable
    fun FormContent() {
        Text(
            text = if (isEditMode) "Modifica Cartella" else "Nuova Cartella",
            style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Serif),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (isEditMode) "Aggiorna i dati di ${patientToEdit?.name} ${patientToEdit?.surname}"
                   else "Inserisci i dati per velocizzare i futuri calcoli",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (isCompact) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
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
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    AnthropometricInputsGroup(
                        weightValue = weight,
                        heightValue = height,
                        ageValue = age,
                        onWeightChanged = {
                            weight = it
                            weightError = if ((it.toFloatOrNull() ?: 0f) > 300f) "Dati inesatti: peso max 300 kg" else null
                        },
                        onHeightChanged = {
                            height = it
                            heightError = if ((it.toFloatOrNull() ?: 0f) > 300f) "Dati inesatti: altezza max 300 cm" else null
                        },
                        onAgeChanged = { age = it },
                        heightLabel = "Altezza (opz)",
                        verticalSpacing = 12.dp,
                        weightError = weightError,
                        heightError = heightError
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Patologie Concomitanti", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            ImpairmentChipsRow(
                renalStage = renalStage,
                hepaticStage = hepaticStage,
                onRenalStageChanged = { renalStage = it },
                onHepaticStageChanged = { hepaticStage = it }
            )
        } else {
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
                onWeightChanged = {
                    weight = it
                    weightError = if ((it.toFloatOrNull() ?: 0f) > 300f) "Dati inesatti: peso max 300 kg" else null
                },
                onHeightChanged = {
                    height = it
                    heightError = if ((it.toFloatOrNull() ?: 0f) > 300f) "Dati inesatti: altezza max 300 cm" else null
                },
                onAgeChanged = { age = it },
                heightLabel = "Altezza (opz)",
                verticalSpacing = 12.dp,
                weightError = weightError,
                heightError = heightError
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
    }

    if (isCompact) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    FormContent()
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (canSave) {
                                val hasRenal = renalStage != RenalStage.NONE
                                val hasHepatic = hepaticStage != HepaticStage.NONE
                                onSave(name, surname, weight, height.ifBlank { null }, age, hasRenal, hasHepatic)
                            }
                        },
                        enabled = canSave,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(if (isEditMode) "Aggiorna" else "Salva Paziente", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    } else {
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
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    FormContent()
                }
                Button(
                    onClick = {
                        if (canSave) {
                            val hasRenal = renalStage != RenalStage.NONE
                            val hasHepatic = hepaticStage != HepaticStage.NONE
                            onSave(name, surname, weight, height.ifBlank { null }, age, hasRenal, hasHepatic)
                        }
                    },
                    enabled = canSave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .navigationBarsPadding()
                        .height(56.dp),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(if (isEditMode) "Aggiorna" else "Salva Paziente", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
