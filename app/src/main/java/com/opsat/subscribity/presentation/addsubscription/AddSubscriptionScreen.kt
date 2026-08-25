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
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("New Subscription") },
                navigationIcon = {
                    IconButton(onClick = { onIntent(AddSubscriptionIntent.Cancel) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
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
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                OutlinedButton(onClick = { onIntent(AddSubscriptionIntent.Cancel) }) {
                    Text("Cancel")
                }
                Button(onClick = { onIntent(AddSubscriptionIntent.Save) }, enabled = !state.isSaving) {
                    Text("Save")
                }
            }
        }
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
            OutlinedTextField(
                value = state.customPeriodDaysText,
                onValueChange = { onIntent(AddSubscriptionIntent.CustomPeriodDaysChanged(it)) },
                label = { Text("Every N days") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = state.customPeriodError != null,
                supportingText = { state.customPeriodError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )
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
