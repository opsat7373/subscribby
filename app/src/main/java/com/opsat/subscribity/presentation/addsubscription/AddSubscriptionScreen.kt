package com.opsat.subscribity.presentation.addsubscription

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mr0xf00.easycrop.CropError
import com.mr0xf00.easycrop.CropResult
import com.mr0xf00.easycrop.crop
import com.mr0xf00.easycrop.rememberImageCropper
import com.mr0xf00.easycrop.ui.ImageCropperDialog
import com.opsat.subscribity.domain.model.BillingPeriod
import com.opsat.subscribity.domain.model.CustomPeriodUnit
import com.opsat.subscribity.domain.model.SubscriptionIconType
import com.opsat.subscribity.domain.model.plus
import com.opsat.subscribity.presentation.common.LocalFileImage
import com.opsat.subscribity.presentation.theme.contrastingTextColor
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
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

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val imageCropper = rememberImageCropper()
    var cropError by remember { mutableStateOf<CropError?>(null) }
    val pickPhotoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            scope.launch {
                when (val result = imageCropper.crop(uri, context)) {
                    CropResult.Cancelled -> {}
                    is CropError -> cropError = result
                    is CropResult.Success -> {
                        val bytes = ByteArrayOutputStream().use { stream ->
                            result.bitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.JPEG, 90, stream)
                            stream.toByteArray()
                        }
                        onIntent(AddSubscriptionIntent.PhotoIconCropped(bytes))
                    }
                }
            }
        }
    }
    imageCropper.cropState?.let { cropState -> ImageCropperDialog(state = cropState) }
    cropError?.let {
        AlertDialog(
            onDismissRequest = { cropError = null },
            title = { Text("Couldn't load image") },
            text = { Text("Please try a different photo.") },
            confirmButton = { TextButton(onClick = { cropError = null }) { Text("OK") } },
        )
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {}
    LaunchedEffect(Unit) {
        if (mode is AddSubscriptionMode.Create &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        modifier = modifier,
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
                    enabled = !state.isSaving && state.customPeriodError == null &&
                        state.trialPeriodError == null && state.trialPriceError == null,
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
            Text(
                text = when (mode) {
                    AddSubscriptionMode.Create -> "New Subscription"
                    is AddSubscriptionMode.Edit -> mode.originalName
                },
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
                SubscriptionIconPreview(
                    state = state,
                    onClick = { onIntent(AddSubscriptionIntent.IconPreviewClicked) },
                )
                Spacer(modifier = Modifier.width(12.dp))
                NameField(state = state, onIntent = onIntent, modifier = Modifier.weight(1f))
            }
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
            Spacer(modifier = Modifier.height(16.dp))
            TrialSection(state = state, onIntent = onIntent)
            Spacer(modifier = Modifier.height(16.dp))
            NotificationToggleRow(state = state, onIntent = onIntent)
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

    if (state.isIconOptionsDialogVisible) {
        IconOptionsDialog(
            onDismissRequest = { onIntent(AddSubscriptionIntent.IconOptionsDialogDismissed) },
            onLetterClicked = { onIntent(AddSubscriptionIntent.LetterIconSelected) },
            onIconClicked = { onIntent(AddSubscriptionIntent.BrandIconPickerOpened) },
            onGetImageClicked = {
                onIntent(AddSubscriptionIntent.IconOptionsDialogDismissed)
                pickPhotoLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
        )
    }

    if (state.isBrandIconPickerVisible) {
        BrandIconPickerDialog(state = state, onIntent = onIntent)
    }
}

@Composable
private fun SubscriptionIconPreview(
    state: AddSubscriptionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        when (state.iconType) {
            SubscriptionIconType.LETTER -> {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color(state.iconColor)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = state.name.take(1).uppercase().ifBlank { "?" },
                        style = MaterialTheme.typography.titleLarge,
                        color = contrastingTextColor(state.iconColor),
                    )
                }
            }

            SubscriptionIconType.BRAND -> {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    val resId = state.iconValue?.let { SimpleIconsCatalog.drawableResFor(it) }
                    if (resId != null) {
                        Image(
                            painter = painterResource(resId),
                            contentDescription = null,
                            modifier = Modifier
                                .size(56.dp)
                                .padding(10.dp),
                        )
                    }
                }
            }

            SubscriptionIconType.PHOTO -> {
                val context = LocalContext.current
                state.iconValue?.let { relativePath ->
                    LocalFileImage(
                        file = File(context.filesDir, relativePath),
                        contentDescription = null,
                        modifier = Modifier.matchParentSize(),
                    )
                }
            }
        }
    }
}

@Composable
private fun IconOptionsDialog(
    onDismissRequest: () -> Unit,
    onLetterClicked: () -> Unit,
    onIconClicked: () -> Unit,
    onGetImageClicked: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "Choose icon",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )
                IconOptionRow("Letter", onClick = onLetterClicked)
                IconOptionRow("Icon", onClick = onIconClicked)
                IconOptionRow("Get Image", onClick = onGetImageClicked)
            }
        }
    }
}

@Composable
private fun IconOptionRow(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BrandIconPickerDialog(
    state: AddSubscriptionState,
    onIntent: (AddSubscriptionIntent) -> Unit,
) {
    Dialog(onDismissRequest = { onIntent(AddSubscriptionIntent.BrandIconPickerDismissed) }) {
        Card(modifier = Modifier.height(480.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = state.brandIconQuery,
                    onValueChange = { onIntent(AddSubscriptionIntent.BrandIconQueryChanged(it)) },
                    label = { Text("Search") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyVerticalGrid(columns = GridCells.Adaptive(64.dp)) {
                    items(state.filteredBrandIcons, key = { it.slug }) { option ->
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .clickable { onIntent(AddSubscriptionIntent.BrandIconSelected(option)) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = painterResource(option.drawableResId),
                                contentDescription = option.title,
                                modifier = Modifier
                                    .size(56.dp)
                                    .padding(10.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NameField(
    state: AddSubscriptionState,
    onIntent: (AddSubscriptionIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    ExposedDropdownMenuBox(
        expanded = state.isNameSuggestionsExpanded,
        onExpandedChange = { onIntent(AddSubscriptionIntent.NameSuggestionsExpandedChanged(it)) },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = state.name,
            onValueChange = { onIntent(AddSubscriptionIntent.NameChanged(it)) },
            label = { Text("Name") },
            isError = state.nameError != null,
            supportingText = { state.nameError?.let { Text(it) } },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable).fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = state.isNameSuggestionsExpanded,
            onDismissRequest = { onIntent(AddSubscriptionIntent.NameSuggestionsExpandedChanged(false)) },
        ) {
            state.filteredNameSuggestions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.title) },
                    leadingIcon = {
                        Image(
                            painter = painterResource(option.drawableResId),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                    },
                    onClick = { onIntent(AddSubscriptionIntent.NameSuggestionSelected(option)) },
                )
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
                PeriodUnitField(
                    selectedUnit = state.customPeriodUnit,
                    onUnitSelected = { onIntent(AddSubscriptionIntent.CustomPeriodUnitSelected(it)) },
                    modifier = Modifier.weight(1f),
                )
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
private fun PeriodUnitField(
    selectedUnit: CustomPeriodUnit,
    onUnitSelected: (CustomPeriodUnit) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
        OutlinedTextField(
            value = selectedUnit.label(),
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
                        onUnitSelected(unit)
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
        label = { Text(if (state.isTrial) "Trial start date" else "Next payment date") },
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

@Composable
private fun TrialSection(
    state: AddSubscriptionState,
    onIntent: (AddSubscriptionIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Free trial", modifier = Modifier.weight(1f))
            Switch(
                checked = state.isTrial,
                onCheckedChange = { onIntent(AddSubscriptionIntent.TrialToggled(it)) },
            )
        }
        if (state.isTrial) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = state.trialPeriodCountText,
                    onValueChange = { onIntent(AddSubscriptionIntent.TrialPeriodCountChanged(it)) },
                    label = { Text("Every") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = state.trialPeriodError != null,
                    supportingText = { state.trialPeriodError?.let { Text(it) } },
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(12.dp))
                PeriodUnitField(
                    selectedUnit = state.trialPeriodUnit,
                    onUnitSelected = { onIntent(AddSubscriptionIntent.TrialPeriodUnitSelected(it)) },
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.trialPriceText,
                onValueChange = { onIntent(AddSubscriptionIntent.TrialPriceChanged(it)) },
                label = { Text("Trial price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = state.trialPriceError != null,
                supportingText = { state.trialPriceError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )
            val trialCount = state.trialPeriodCountText.toIntOrNull()
            if (trialCount != null && trialCount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                val firstCharge = state.nextPaymentDate.plus(BillingPeriod.Custom(trialCount, state.trialPeriodUnit))
                Text(
                    "First charge on ${firstCharge.format(dateFormatter)}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun NotificationToggleRow(
    state: AddSubscriptionState,
    onIntent: (AddSubscriptionIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text("Notify me before payment", modifier = Modifier.weight(1f))
        Switch(
            checked = state.notificationsEnabled,
            onCheckedChange = { onIntent(AddSubscriptionIntent.NotificationsEnabledToggled(it)) },
        )
    }
}
