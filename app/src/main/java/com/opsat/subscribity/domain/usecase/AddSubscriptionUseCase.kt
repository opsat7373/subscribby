package com.opsat.subscribity.domain.usecase

import com.opsat.subscribity.domain.model.Subscription
import com.opsat.subscribity.domain.repository.SubscriptionRepository
import javax.inject.Inject

class AddSubscriptionUseCase @Inject constructor(
    private val repository: SubscriptionRepository,
) {
    suspend operator fun invoke(subscription: Subscription): Long {
        require(subscription.id == 0L) { "New subscription must not have an id yet" }
        return repository.addSubscription(subscription)
    }
}
