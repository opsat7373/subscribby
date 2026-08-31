package com.opsat.subscribity.presentation.addsubscription

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opsat.subscribity.domain.model.BillingPeriod
import com.opsat.subscribity.domain.model.CurrencyCode
import com.opsat.subscribity.domain.model.CustomPeriodUnit
import com.opsat.subscribity.domain.model.Subscription
import com.opsat.subscribity.domain.usecase.AddSubscriptionUseCase
import com.opsat.subscribity.domain.usecase.DeleteSubscriptionUseCase
import com.opsat.subscribity.domain.usecase.EditSubscriptionUseCase
import com.opsat.subscribity.domain.usecase.ObserveSubscriptionsUseCase
import com.opsat.subscribity.presentation.navigation.SUBSCRIPTION_ID_ARG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AddSubscriptionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addSubscription: AddSubscriptionUseCase,
    private val editSubscription: EditSubscriptionUseCase,
    private val deleteSubscription: DeleteSubscriptionUseCase,
    private val observeSubscriptions: ObserveSubscriptionsUseCase,
) : ViewModel() {

    private val subscriptionId: Long = savedStateHandle[SUBSCRIPTION_ID_ARG] ?: 0L

    private val _state = MutableStateFlow(createInitialState())
    val state: StateFlow<AddSubscriptionState> = _state.asStateFlow()

    private val _effects = Channel<AddSubscriptionEffect>(Channel.BUFFERED)
    val effects: Flow<AddSubscriptionEffect> = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            val subscriptions = observeSubscriptions().first()
            val usedCodes = subscriptions.map { it.currency.code }
            val options = buildCurrencyOptions(usedCodes)
            // Not re-filtered by the current currencyQuery: that query is pre-filled with the
            // default selected currency's code, and filtering by it here would immediately hide
            // every other currency before the user has actually opened/searched the picker.
            _state.update { it.copy(allCurrencyOptions = options, filteredCurrencyOptions = options) }

            if (subscriptionId != 0L) {
                val existing = subscriptions.firstOrNull { it.id == subscriptionId }
                if (existing == null) {
                    _effects.send(AddSubscriptionEffect.NavigateBack)
                } else {
                    _state.update {
                        it.copy(
                            mode = AddSubscriptionMode.Edit(subscriptionId, originalName = existing.name),
                            name = existing.name,
                            priceText = existing.price.toPlainString(),
                            selectedCurrency = options.firstOrNull { opt -> opt.code == existing.currency.code },
                            currencyQuery = existing.currency.code,
                            periodOption = existing.period.toPeriodOption(),
                            customPeriodCountText = (existing.period as? BillingPeriod.Custom)?.count?.toString().orEmpty(),
                            customPeriodUnit = (existing.period as? BillingPeriod.Custom)?.unit ?: CustomPeriodUnit.DAYS,
                            nextPaymentDate = existing.nextPaymentDate,
                            isTrial = existing.isTrial,
                            trialPeriodCountText = (existing.trialPeriod as? BillingPeriod.Custom)?.count?.toString().orEmpty(),
                            trialPeriodUnit = (existing.trialPeriod as? BillingPeriod.Custom)?.unit ?: CustomPeriodUnit.DAYS,
                            trialPeriodError = if (existing.isTrial) {
                                trialPeriodErrorFor((existing.trialPeriod as? BillingPeriod.Custom)?.count?.toString().orEmpty())
                            } else {
                                null
                            },
                            trialPriceText = existing.trialPrice?.toPlainString() ?: "0",
                            trialPriceError = if (existing.isTrial) trialPriceErrorFor(existing.trialPrice?.toPlainString() ?: "0") else null,
                            notificationsEnabled = existing.notificationsEnabled,
                        )
                    }
                }
            }
        }
    }

    fun onIntent(intent: AddSubscriptionIntent) {
        when (intent) {
            is AddSubscriptionIntent.NameChanged ->
                _state.update { it.copy(name = intent.value, nameError = null) }

            is AddSubscriptionIntent.PriceChanged ->
                _state.update { it.copy(priceText = sanitizePriceInput(intent.value), priceError = null) }

            is AddSubscriptionIntent.CurrencyQueryChanged -> _state.update {
                it.copy(
                    currencyQuery = intent.value,
                    filteredCurrencyOptions = filterCurrencyOptions(it.allCurrencyOptions, intent.value),
                    isCurrencyMenuExpanded = true,
                )
            }

            is AddSubscriptionIntent.CurrencySelected -> _state.update {
                it.copy(
                    selectedCurrency = intent.option,
                    currencyQuery = intent.option.code,
                    filteredCurrencyOptions = it.allCurrencyOptions,
                    isCurrencyMenuExpanded = false,
                )
            }

            is AddSubscriptionIntent.CurrencyMenuExpandedChanged ->
                _state.update { it.copy(isCurrencyMenuExpanded = intent.expanded) }

            is AddSubscriptionIntent.PeriodOptionSelected -> _state.update {
                it.copy(
                    periodOption = intent.option,
                    customPeriodError = customPeriodErrorFor(intent.option, it.customPeriodCountText),
                )
            }

            is AddSubscriptionIntent.CustomPeriodCountChanged -> _state.update {
                val filtered = intent.value.filter(Char::isDigit)
                it.copy(
                    customPeriodCountText = filtered,
                    customPeriodError = customPeriodErrorFor(it.periodOption, filtered),
                )
            }

            is AddSubscriptionIntent.CustomPeriodUnitSelected ->
                _state.update { it.copy(customPeriodUnit = intent.unit) }

            is AddSubscriptionIntent.DateSelected ->
                _state.update { it.copy(nextPaymentDate = intent.date, isDatePickerVisible = false) }

            is AddSubscriptionIntent.DatePickerVisibilityChanged ->
                _state.update { it.copy(isDatePickerVisible = intent.visible) }

            is AddSubscriptionIntent.TrialToggled -> _state.update {
                it.copy(
                    isTrial = intent.enabled,
                    trialPeriodError = if (intent.enabled) trialPeriodErrorFor(it.trialPeriodCountText) else null,
                    trialPriceError = if (intent.enabled) trialPriceErrorFor(it.trialPriceText) else null,
                )
            }

            is AddSubscriptionIntent.TrialPeriodCountChanged -> _state.update {
                val filtered = intent.value.filter(Char::isDigit)
                it.copy(
                    trialPeriodCountText = filtered,
                    trialPeriodError = if (it.isTrial) trialPeriodErrorFor(filtered) else null,
                )
            }

            is AddSubscriptionIntent.TrialPeriodUnitSelected ->
                _state.update { it.copy(trialPeriodUnit = intent.unit) }

            is AddSubscriptionIntent.TrialPriceChanged -> _state.update {
                val sanitized = sanitizePriceInput(intent.value)
                it.copy(
                    trialPriceText = sanitized,
                    trialPriceError = if (it.isTrial) trialPriceErrorFor(sanitized) else null,
                )
            }

            is AddSubscriptionIntent.NotificationsEnabledToggled ->
                _state.update { it.copy(notificationsEnabled = intent.enabled) }

            AddSubscriptionIntent.Cancel -> navigateBack()

            AddSubscriptionIntent.Save -> onPrimaryActionClicked()

            AddSubscriptionIntent.ConfirmUpdate -> confirmUpdate()

            AddSubscriptionIntent.DismissUpdateConfirmation ->
                _state.update { it.copy(isUpdateConfirmationVisible = false) }

            AddSubscriptionIntent.DeleteClicked ->
                _state.update { it.copy(isDeleteConfirmationVisible = true) }

            AddSubscriptionIntent.ConfirmDelete -> confirmDelete()

            AddSubscriptionIntent.DismissDeleteConfirmation ->
                _state.update { it.copy(isDeleteConfirmationVisible = false) }
        }
    }

    private fun onPrimaryActionClicked() {
        val subscription = buildValidSubscriptionOrNull() ?: return
        when (_state.value.mode) {
            AddSubscriptionMode.Create -> {
                _state.update { it.copy(isSaving = true) }
                viewModelScope.launch {
                    addSubscription(subscription)
                    _effects.send(AddSubscriptionEffect.NavigateBack)
                }
            }
            is AddSubscriptionMode.Edit -> _state.update { it.copy(isUpdateConfirmationVisible = true) }
        }
    }

    private fun confirmUpdate() {
        val subscription = buildValidSubscriptionOrNull() ?: return
        _state.update { it.copy(isUpdateConfirmationVisible = false, isSaving = true) }
        viewModelScope.launch {
            editSubscription(subscription)
            _effects.send(AddSubscriptionEffect.NavigateBack)
        }
    }

    private fun confirmDelete() {
        val id = (_state.value.mode as? AddSubscriptionMode.Edit)?.subscriptionId ?: return
        _state.update { it.copy(isDeleteConfirmationVisible = false, isSaving = true) }
        viewModelScope.launch {
            deleteSubscription(id)
            _effects.send(AddSubscriptionEffect.NavigateBack)
        }
    }

    private fun buildValidSubscriptionOrNull(): Subscription? {
        val current = _state.value
        val price = current.priceText.toBigDecimalOrNull()
        val customCount = current.customPeriodCountText.toIntOrNull()

        val nameError = if (current.name.isBlank()) "Name is required" else null
        val priceError = if (price == null || price < BigDecimal.ZERO) "Enter a valid price" else null
        val customPeriodError = customPeriodErrorFor(current.periodOption, current.customPeriodCountText)
        val trialPeriodError = if (current.isTrial) trialPeriodErrorFor(current.trialPeriodCountText) else null
        val trialPriceError = if (current.isTrial) trialPriceErrorFor(current.trialPriceText) else null

        if (nameError != null || priceError != null || customPeriodError != null ||
            trialPeriodError != null || trialPriceError != null
        ) {
            _state.update {
                it.copy(
                    nameError = nameError,
                    priceError = priceError,
                    customPeriodError = customPeriodError,
                    trialPeriodError = trialPeriodError,
                    trialPriceError = trialPriceError,
                )
            }
            return null
        }

        val currency = current.selectedCurrency ?: return null
        val existingId = (current.mode as? AddSubscriptionMode.Edit)?.subscriptionId ?: 0L
        return Subscription(
            id = existingId,
            name = current.name.trim(),
            icon = current.name.trim(),
            period = current.periodOption.toBillingPeriod(customCount, current.customPeriodUnit),
            price = requireNotNull(price),
            currency = CurrencyCode(currency.code),
            nextPaymentDate = current.nextPaymentDate,
            isTrial = current.isTrial,
            trialPeriod = if (current.isTrial) {
                BillingPeriod.Custom(count = requireNotNull(current.trialPeriodCountText.toIntOrNull()), unit = current.trialPeriodUnit)
            } else {
                null
            },
            trialPrice = if (current.isTrial) current.trialPriceText.toBigDecimalOrNull() else null,
            notificationsEnabled = current.notificationsEnabled,
        )
    }

    private fun customPeriodErrorFor(period: PeriodOption, countText: String): String? {
        if (period != PeriodOption.CUSTOM) return null
        val count = countText.toIntOrNull()
        return if (count == null || count <= 0) "Enter a number greater than 0" else null
    }

    private fun trialPeriodErrorFor(countText: String): String? {
        val count = countText.toIntOrNull()
        return if (count == null || count <= 0) "Enter a number greater than 0" else null
    }

    private fun trialPriceErrorFor(priceText: String): String? {
        val price = priceText.toBigDecimalOrNull()
        return if (price == null || price < BigDecimal.ZERO) "Enter a valid price" else null
    }

    private fun navigateBack() {
        viewModelScope.launch { _effects.send(AddSubscriptionEffect.NavigateBack) }
    }

    companion object {
        private fun createInitialState(): AddSubscriptionState {
            val catalog = buildCurrencyOptions(emptyList())
            val systemCode = systemCurrencyCodeOrNull() ?: "USD"
            val selected = catalog.firstOrNull { it.code == systemCode } ?: catalog.first()
            return AddSubscriptionState(
                selectedCurrency = selected,
                currencyQuery = selected.code,
                allCurrencyOptions = catalog,
                filteredCurrencyOptions = catalog,
            )
        }
    }
}

private fun PeriodOption.toBillingPeriod(customCount: Int?, customUnit: CustomPeriodUnit): BillingPeriod = when (this) {
    PeriodOption.WEEKLY -> BillingPeriod.Weekly
    PeriodOption.MONTHLY -> BillingPeriod.Monthly
    PeriodOption.QUARTERLY -> BillingPeriod.Quarterly
    PeriodOption.YEARLY -> BillingPeriod.Yearly
    PeriodOption.CUSTOM -> BillingPeriod.Custom(count = requireNotNull(customCount), unit = customUnit)
}

private fun BillingPeriod.toPeriodOption(): PeriodOption = when (this) {
    BillingPeriod.Weekly -> PeriodOption.WEEKLY
    BillingPeriod.Monthly -> PeriodOption.MONTHLY
    BillingPeriod.Quarterly -> PeriodOption.QUARTERLY
    BillingPeriod.Yearly -> PeriodOption.YEARLY
    is BillingPeriod.Custom -> PeriodOption.CUSTOM
}
