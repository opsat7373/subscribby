package com.opsat.subscribity.domain.repository

import com.opsat.subscribity.domain.model.Subscription
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    fun observeSubscriptions(): Flow<List<Subscription>>

    suspend fun getSubscription(id: Long): Subscription?

    suspend fun addSubscription(subscription: Subscription): Long

    suspend fun updateSubscription(subscription: Subscription)
}
