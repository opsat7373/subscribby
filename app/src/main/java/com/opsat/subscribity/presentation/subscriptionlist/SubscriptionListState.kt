package com.opsat.subscribity.presentation.subscriptionlist

import com.opsat.subscribity.domain.model.SubscriptionIconType

data class SubscriptionListState(
    val isLoading: Boolean = true,
    val subscriptions: List<SubscriptionListItemUiModel> = emptyList(),
    val monthlySpending: List<SpendingSummaryItemUiModel> = emptyList(),
)

data class SubscriptionListItemUiModel(
    val id: Long,
    val name: String,
    val iconType: SubscriptionIconType,
    val iconValue: String?,
    val iconColor: Int,
    val nextPaymentDateLabel: String,
    val priceLabel: String,
    val periodLabel: String,
    val isDueSoon: Boolean,
    val isCustomCycle: Boolean,
)

data class SpendingSummaryItemUiModel(val amount: String, val currencyCode: String)
