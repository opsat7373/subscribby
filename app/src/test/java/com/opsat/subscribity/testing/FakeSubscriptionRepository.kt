package com.opsat.subscribity.testing

import com.opsat.subscribity.domain.model.Subscription
import com.opsat.subscribity.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSubscriptionRepository : SubscriptionRepository {
    val subscriptionsFlow = MutableStateFlow<List<Subscription>>(emptyList())
    val addedSubscriptions = mutableListOf<Subscription>()

    private var nextId = 1L

    override fun observeSubscriptions(): Flow<List<Subscription>> = subscriptionsFlow

    override suspend fun getSubscription(id: Long): Subscription? =
        subscriptionsFlow.value.firstOrNull { it.id == id }

    override suspend fun addSubscription(subscription: Subscription): Long {
        addedSubscriptions += subscription
        val id = nextId++
        subscriptionsFlow.value = subscriptionsFlow.value + subscription.copy(id = id)
        return id
    }

    override suspend fun updateSubscription(subscription: Subscription) {
        subscriptionsFlow.value = subscriptionsFlow.value.map { if (it.id == subscription.id) subscription else it }
    }

    override suspend fun deleteSubscription(id: Long) {
        subscriptionsFlow.value = subscriptionsFlow.value.filterNot { it.id == id }
    }
}
