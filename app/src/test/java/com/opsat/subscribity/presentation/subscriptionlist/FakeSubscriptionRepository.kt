package com.opsat.subscribity.presentation.subscriptionlist

import com.opsat.subscribity.domain.model.Subscription
import com.opsat.subscribity.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSubscriptionRepository : SubscriptionRepository {
    val subscriptionsFlow = MutableStateFlow<List<Subscription>>(emptyList())

    override fun observeSubscriptions(): Flow<List<Subscription>> = subscriptionsFlow

    override suspend fun getSubscription(id: Long): Subscription? =
        subscriptionsFlow.value.firstOrNull { it.id == id }

    override suspend fun addSubscription(subscription: Subscription): Long =
        error("not used in these tests")

    override suspend fun updateSubscription(subscription: Subscription) {
        error("not used in these tests")
    }
}
