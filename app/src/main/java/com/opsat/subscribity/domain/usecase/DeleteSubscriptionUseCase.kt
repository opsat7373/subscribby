package com.opsat.subscribity.domain.usecase

import com.opsat.subscribity.domain.repository.SubscriptionRepository
import javax.inject.Inject

class DeleteSubscriptionUseCase @Inject constructor(
    private val repository: SubscriptionRepository,
) {
    suspend operator fun invoke(id: Long) {
        require(id != 0L) { "Cannot delete a subscription without an id" }
        repository.deleteSubscription(id)
    }
}
