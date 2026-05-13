package com.example.dosagecalc.presentation.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.dosagecalc.presentation.ui.theme.LocalDosageShapes

@Composable
fun RoundedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    OutlinedTextField(
        value           = value,
        onValueChange   = onValueChange,
        modifier        = modifier,
        label           = label,
        placeholder     = placeholder,
        leadingIcon     = leadingIcon,
        trailingIcon    = trailingIcon,
        singleLine      = singleLine,
        maxLines        = maxLines,
        minLines        = minLines,
        readOnly        = readOnly,
        enabled         = enabled,
        isError         = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        shape           = LocalDosageShapes.current.field
    )
}
