package com.opsat.subscribity.presentation.addsubscription

import java.time.LocalDate

enum class PeriodOption { WEEKLY, MONTHLY, QUARTERLY, YEARLY, CUSTOM }

data class CurrencyOption(val code: String, val displayName: String)

data class AddSubscriptionState(
    val name: String = "",
    val nameError: String? = null,
    val priceText: String = "",
    val priceError: String? = null,
    val currencyQuery: String = "",
    val selectedCurrency: CurrencyOption? = null,
    val isCurrencyMenuExpanded: Boolean = false,
    val allCurrencyOptions: List<CurrencyOption> = emptyList(),
    val filteredCurrencyOptions: List<CurrencyOption> = emptyList(),
    val periodOption: PeriodOption = PeriodOption.MONTHLY,
    val customPeriodDaysText: String = "",
    val customPeriodError: String? = null,
    val nextPaymentDate: LocalDate = LocalDate.now(),
    val isDatePickerVisible: Boolean = false,
    val isSaving: Boolean = false,
)
