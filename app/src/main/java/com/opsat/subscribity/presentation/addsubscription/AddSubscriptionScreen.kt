package com.opsat.subscribity.presentation.addsubscription

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.opsat.subscribity.domain.model.CustomPeriodUnit
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

@Composable
fun AddSubscriptionRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddSubscriptionViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                AddSubscriptionEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    AddSubscriptionScreen(state = state, onIntent = viewModel::onIntent, modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubscriptionScreen(
    state: AddSubscriptionState,
    onIntent: (AddSubscriptionIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val mode = state.mode
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (mode) {
                            AddSubscriptionMode.Create -> "New Subscription"
                            is AddSubscriptionMode.Edit -> mode.originalName
                        },
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onIntent(AddSubscriptionIntent.Cancel) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = { onIntent(AddSubscriptionIntent.Cancel) },
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = { onIntent(AddSubscriptionIntent.Save) },
                    enabled = !state.isSaving && state.customPeriodError == null,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(if (mode is AddSubscriptionMode.Edit) "Update" else "Save")
                }
            }
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = { onIntent(AddSubscriptionIntent.NameChanged(it)) },
                label = { Text("Name") },
                isError = state.nameError != null,
                supportingText = { state.nameError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                CurrencyField(state = state, onIntent = onIntent, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedTextField(
                    value = state.priceText,
                    onValueChange = { onIntent(AddSubscriptionIntent.PriceChanged(it)) },
                    label = { Text("Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = state.priceError != null,
                    supportingText = { state.priceError?.let { Text(it) } },
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            PeriodField(state = state, onIntent = onIntent)
            Spacer(modifier = Modifier.height(16.dp))
            DateField(state = state, onIntent = onIntent)
            if (mode is AddSubscriptionMode.Edit) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onIntent(AddSubscriptionIntent.DeleteClicked) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Delete")
                }
            }
        }
    }

    if (state.isUpdateConfirmationVisible && mode is AddSubscriptionMode.Edit) {
        AlertDialog(
            onDismissRequest = { onIntent(AddSubscriptionIntent.DismissUpdateConfirmation) },
            title = { Text("Update subscription?") },
            text = { Text("Save changes to \"${mode.originalName}\"?") },
            confirmButton = {
                TextButton(onClick = { onIntent(AddSubscriptionIntent.ConfirmUpdate) }) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { onIntent(AddSubscriptionIntent.DismissUpdateConfirmation) }) {
                    Text("Cancel")
                }
            },
        )
    }

    if (state.isDeleteConfirmationVisible && mode is AddSubscriptionMode.Edit) {
        AlertDialog(
            onDismissRequest = { onIntent(AddSubscriptionIntent.DismissDeleteConfirmation) },
            title = { Text("Delete subscription?") },
            text = { Text("This will permanently delete \"${mode.originalName}\". This can't be undone.") },
            confirmButton = {
                TextButton(
                    onClick = { onIntent(AddSubscriptionIntent.ConfirmDelete) },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { onIntent(AddSubscriptionIntent.DismissDeleteConfirmation) }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyField(
    state: AddSubscriptionState,
    onIntent: (AddSubscriptionIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    ExposedDropdownMenuBox(
        expanded = state.isCurrencyMenuExpanded,
        onExpandedChange = { onIntent(AddSubscriptionIntent.CurrencyMenuExpandedChanged(it)) },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = state.currencyQuery,
            onValueChange = { onIntent(AddSubscriptionIntent.CurrencyQueryChanged(it)) },
            label = { Text("Currency") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.isCurrencyMenuExpanded) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable).fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = state.isCurrencyMenuExpanded,
            onDismissRequest = { onIntent(AddSubscriptionIntent.CurrencyMenuExpandedChanged(false)) },
        ) {
            state.filteredCurrencyOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text("${option.code} — ${option.displayName}") },
                    onClick = { onIntent(AddSubscriptionIntent.CurrencySelected(option)) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodField(
    state: AddSubscriptionState,
    onIntent: (AddSubscriptionIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = state.periodOption.label(),
                onValueChange = {},
                readOnly = true,
                label = { Text("Billing period") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                PeriodOption.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.label()) },
                        onClick = {
                            onIntent(AddSubscriptionIntent.PeriodOptionSelected(option))
                            expanded = false
                        },
                    )
                }
            }
        }
        if (state.periodOption == PeriodOption.CUSTOM) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = state.customPeriodCountText,
                    onValueChange = { onIntent(AddSubscriptionIntent.CustomPeriodCountChanged(it)) },
                    label = { Text("Every") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = state.customPeriodError != null,
                    supportingText = { state.customPeriodError?.let { Text(it) } },
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(12.dp))
                CustomPeriodUnitField(state = state, onIntent = onIntent, modifier = Modifier.weight(1f))
            }
        }
    }
}

private fun PeriodOption.label(): String = when (this) {
    PeriodOption.WEEKLY -> "Weekly"
    PeriodOption.MONTHLY -> "Monthly"
    PeriodOption.QUARTERLY -> "Quarterly"
    PeriodOption.YEARLY -> "Yearly"
    PeriodOption.CUSTOM -> "Custom"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomPeriodUnitField(
    state: AddSubscriptionState,
    onIntent: (AddSubscriptionIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
        OutlinedTextField(
            value = state.customPeriodUnit.label(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Unit") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            CustomPeriodUnit.entries.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(unit.label()) },
                    onClick = {
                        onIntent(AddSubscriptionIntent.CustomPeriodUnitSelected(unit))
                        expanded = false
                    },
                )
            }
        }
    }
}

private fun CustomPeriodUnit.label(): String = when (this) {
    CustomPeriodUnit.DAYS -> "Days"
    CustomPeriodUnit.WEEKS -> "Weeks"
    CustomPeriodUnit.MONTHS -> "Months"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateField(
    state: AddSubscriptionState,
    onIntent: (AddSubscriptionIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            if (interaction is PressInteraction.Release) {
                onIntent(AddSubscriptionIntent.DatePickerVisibilityChanged(true))
            }
        }
    }

    OutlinedTextField(
        value = state.nextPaymentDate.format(dateFormatter),
        onValueChange = {},
        readOnly = true,
        label = { Text("Next payment date") },
        interactionSource = interactionSource,
        modifier = modifier.fillMaxWidth(),
    )

    if (state.isDatePickerVisible) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.nextPaymentDate.toEpochMilliUtc(),
        )
        DatePickerDialog(
            onDismissRequest = { onIntent(AddSubscriptionIntent.DatePickerVisibilityChanged(false)) },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        onIntent(AddSubscriptionIntent.DateSelected(millis.toLocalDateUtc()))
                    } else {
                        onIntent(AddSubscriptionIntent.DatePickerVisibilityChanged(false))
                    }
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { onIntent(AddSubscriptionIntent.DatePickerVisibilityChanged(false)) }) {
                    Text("Cancel")
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun LocalDate.toEpochMilliUtc(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

private fun Long.toLocalDateUtc(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()
