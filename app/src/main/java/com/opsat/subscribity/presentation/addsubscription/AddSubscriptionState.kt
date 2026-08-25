package com.opsat.subscribity.presentation.addsubscription

import com.opsat.subscribity.domain.model.CustomPeriodUnit
import java.time.LocalDate

enum class PeriodOption { WEEKLY, MONTHLY, QUARTERLY, YEARLY, CUSTOM }

data class CurrencyOption(val code: String, val displayName: String)

sealed interface AddSubscriptionMode {
    data object Create : AddSubscriptionMode
    data class Edit(val subscriptionId: Long, val originalName: String) : AddSubscriptionMode
}

data class AddSubscriptionState(
    val mode: AddSubscriptionMode = AddSubscriptionMode.Create,
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
    val customPeriodCountText: String = "",
    val customPeriodUnit: CustomPeriodUnit = CustomPeriodUnit.DAYS,
    val customPeriodError: String? = null,
    val nextPaymentDate: LocalDate = LocalDate.now(),
    val isDatePickerVisible: Boolean = false,
    val isSaving: Boolean = false,
    val isUpdateConfirmationVisible: Boolean = false,
    val isDeleteConfirmationVisible: Boolean = false,
)
