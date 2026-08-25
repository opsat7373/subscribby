package com.opsat.subscribity.presentation.addsubscription

sealed interface AddSubscriptionEffect {
    data object NavigateBack : AddSubscriptionEffect
}
