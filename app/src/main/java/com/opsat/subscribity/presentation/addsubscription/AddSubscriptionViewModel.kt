package com.opsat.subscribity.presentation.addsubscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.opsat.subscribity.domain.model.BillingPeriod
import com.opsat.subscribity.domain.model.CurrencyCode
import com.opsat.subscribity.domain.model.Subscription
import com.opsat.subscribity.domain.usecase.AddSubscriptionUseCase
import com.opsat.subscribity.domain.usecase.ObserveSubscriptionsUseCase
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
    private val addSubscription: AddSubscriptionUseCase,
    private val observeSubscriptions: ObserveSubscriptionsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(createInitialState())
    val state: StateFlow<AddSubscriptionState> = _state.asStateFlow()

    private val _effects = Channel<AddSubscriptionEffect>(Channel.BUFFERED)
    val effects: Flow<AddSubscriptionEffect> = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            val usedCodes = observeSubscriptions().first().map { it.currency.code }
            val options = buildCurrencyOptions(usedCodes)
            // Not re-filtered by the current currencyQuery: that query is pre-filled with the
            // default selected currency's code, and filtering by it here would immediately hide
            // every other currency before the user has actually opened/searched the picker.
            _state.update { it.copy(allCurrencyOptions = options, filteredCurrencyOptions = options) }
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

            is AddSubscriptionIntent.PeriodOptionSelected ->
                _state.update { it.copy(periodOption = intent.option, customPeriodError = null) }

            is AddSubscriptionIntent.CustomPeriodDaysChanged -> _state.update {
                it.copy(customPeriodDaysText = intent.value.filter(Char::isDigit), customPeriodError = null)
            }

            is AddSubscriptionIntent.DateSelected ->
                _state.update { it.copy(nextPaymentDate = intent.date, isDatePickerVisible = false) }

            is AddSubscriptionIntent.DatePickerVisibilityChanged ->
                _state.update { it.copy(isDatePickerVisible = intent.visible) }

            AddSubscriptionIntent.Cancel -> navigateBack()

            AddSubscriptionIntent.Save -> save()
        }
    }

    private fun save() {
        val current = _state.value
        val price = current.priceText.toBigDecimalOrNull()
        val customDays = current.customPeriodDaysText.toIntOrNull()

        val nameError = if (current.name.isBlank()) "Name is required" else null
        val priceError = if (price == null || price < BigDecimal.ZERO) "Enter a valid price" else null
        val customPeriodError = if (current.periodOption == PeriodOption.CUSTOM && (customDays == null || customDays <= 0)) {
            "Enter a number of days"
        } else {
            null
        }

        if (nameError != null || priceError != null || customPeriodError != null) {
            _state.update { it.copy(nameError = nameError, priceError = priceError, customPeriodError = customPeriodError) }
            return
        }

        val currency = current.selectedCurrency ?: return
        val subscription = Subscription(
            name = current.name.trim(),
            icon = current.name.trim(),
            period = current.periodOption.toBillingPeriod(customDays),
            price = requireNotNull(price),
            currency = CurrencyCode(currency.code),
            nextPaymentDate = current.nextPaymentDate,
        )

        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            addSubscription(subscription)
            _effects.send(AddSubscriptionEffect.NavigateBack)
        }
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

private fun PeriodOption.toBillingPeriod(customDays: Int?): BillingPeriod = when (this) {
    PeriodOption.WEEKLY -> BillingPeriod.Weekly
    PeriodOption.MONTHLY -> BillingPeriod.Monthly
    PeriodOption.QUARTERLY -> BillingPeriod.Quarterly
    PeriodOption.YEARLY -> BillingPeriod.Yearly
    PeriodOption.CUSTOM -> BillingPeriod.Custom(requireNotNull(customDays))
}
