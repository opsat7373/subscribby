package com.opsat.subscribity.presentation.addsubscription

import com.opsat.subscribity.domain.model.AvatarColors
import com.opsat.subscribity.domain.model.CustomPeriodUnit
import com.opsat.subscribity.domain.model.SubscriptionIconType
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
    val isTrial: Boolean = false,
    val trialPeriodCountText: String = "",
    val trialPeriodUnit: CustomPeriodUnit = CustomPeriodUnit.DAYS,
    val trialPeriodError: String? = null,
    val trialPriceText: String = "0",
    val trialPriceError: String? = null,
    val notificationsEnabled: Boolean = true,
    val iconType: SubscriptionIconType = SubscriptionIconType.LETTER,
    val iconValue: String? = null,
    val iconColor: Int = AvatarColors.random(),
    val isIconOptionsDialogVisible: Boolean = false,
    val isBrandIconPickerVisible: Boolean = false,
    val brandIconQuery: String = "",
    val filteredBrandIcons: List<SimpleIconOption> = SimpleIconsCatalog.allIcons,
    val isNameSuggestionsExpanded: Boolean = false,
    val filteredNameSuggestions: List<SimpleIconOption> = SimpleIconsCatalog.allIcons,
    val isSaving: Boolean = false,
    val isUpdateConfirmationVisible: Boolean = false,
    val isDeleteConfirmationVisible: Boolean = false,
)
