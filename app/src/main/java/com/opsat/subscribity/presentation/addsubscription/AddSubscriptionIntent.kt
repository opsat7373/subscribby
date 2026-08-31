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
    data class TrialToggled(val enabled: Boolean) : AddSubscriptionIntent
    data class TrialPeriodCountChanged(val value: String) : AddSubscriptionIntent
    data class TrialPeriodUnitSelected(val unit: CustomPeriodUnit) : AddSubscriptionIntent
    data class TrialPriceChanged(val value: String) : AddSubscriptionIntent
    data class NotificationsEnabledToggled(val enabled: Boolean) : AddSubscriptionIntent
    data class NameSuggestionSelected(val option: SimpleIconOption) : AddSubscriptionIntent
    data class NameSuggestionsExpandedChanged(val expanded: Boolean) : AddSubscriptionIntent
    data object IconPreviewClicked : AddSubscriptionIntent
    data object IconOptionsDialogDismissed : AddSubscriptionIntent
    data object LetterIconSelected : AddSubscriptionIntent
    data object BrandIconPickerOpened : AddSubscriptionIntent
    data object BrandIconPickerDismissed : AddSubscriptionIntent
    data class BrandIconQueryChanged(val value: String) : AddSubscriptionIntent
    data class BrandIconSelected(val option: SimpleIconOption) : AddSubscriptionIntent
    data class PhotoIconCropped(val bytes: ByteArray) : AddSubscriptionIntent {
        override fun equals(other: Any?): Boolean =
            this === other || (other is PhotoIconCropped && bytes.contentEquals(other.bytes))
        override fun hashCode(): Int = bytes.contentHashCode()
    }
    data object Save : AddSubscriptionIntent
    data object Cancel : AddSubscriptionIntent
    data object ConfirmUpdate : AddSubscriptionIntent
    data object DismissUpdateConfirmation : AddSubscriptionIntent
    data object DeleteClicked : AddSubscriptionIntent
    data object ConfirmDelete : AddSubscriptionIntent
    data object DismissDeleteConfirmation : AddSubscriptionIntent
}
