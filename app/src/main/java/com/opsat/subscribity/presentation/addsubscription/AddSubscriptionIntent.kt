package com.opsat.subscribity.presentation.addsubscription

import com.opsat.subscribity.domain.model.CustomPeriodUnit
import java.time.LocalDate

sealed interface AddSubscriptionIntent {
    data class NameChanged(val value: String) : AddSubscriptionIntent
    data class PriceChanged(val value: String) : AddSubscriptionIntent
    data class CurrencyQueryChanged(val value: String) : AddSubscriptionIntent
    data class CurrencySelected(val option: CurrencyOption) : AddSubscriptionIntent
    data class CurrencyMenuExpandedChanged(val expanded: Boolean) : AddSubscriptionIntent
    data class PeriodOptionSelected(val option: PeriodOption) : AddSubscriptionIntent
    data class CustomPeriodCountChanged(val value: String) : AddSubscriptionIntent
    data class CustomPeriodUnitSelected(val unit: CustomPeriodUnit) : AddSubscriptionIntent
    data class DateSelected(val date: LocalDate) : AddSubscriptionIntent
    data class DatePickerVisibilityChanged(val visible: Boolean) : AddSubscriptionIntent
    data object Save : AddSubscriptionIntent
    data object Cancel : AddSubscriptionIntent
    data object ConfirmUpdate : AddSubscriptionIntent
    data object DismissUpdateConfirmation : AddSubscriptionIntent
    data object DeleteClicked : AddSubscriptionIntent
    data object ConfirmDelete : AddSubscriptionIntent
    data object DismissDeleteConfirmation : AddSubscriptionIntent
}
