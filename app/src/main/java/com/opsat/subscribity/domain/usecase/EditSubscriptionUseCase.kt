package com.opsat.subscribity.domain.usecase

import com.opsat.subscribity.domain.model.Subscription
import com.opsat.subscribity.domain.repository.SubscriptionRepository
import javax.inject.Inject

class EditSubscriptionUseCase @Inject constructor(
    private val repository: SubscriptionRepository,
) {
    suspend operator fun invoke(subscription: Subscription) {
        require(subscription.id != 0L) { "Cannot edit a subscription without an id" }
        repository.updateSubscription(subscription)
    }
}
