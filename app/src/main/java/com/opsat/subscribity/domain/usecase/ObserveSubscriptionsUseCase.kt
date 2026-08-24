package com.opsat.subscribity.domain.usecase

import com.opsat.subscribity.domain.model.Subscription
import com.opsat.subscribity.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSubscriptionsUseCase @Inject constructor(
    private val repository: SubscriptionRepository,
) {
    operator fun invoke(): Flow<List<Subscription>> = repository.observeSubscriptions()
}
