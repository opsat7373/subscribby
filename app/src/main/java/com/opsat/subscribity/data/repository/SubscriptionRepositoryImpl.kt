package com.opsat.subscribity.data.repository

import com.opsat.subscribity.data.local.SubscriptionDao
import com.opsat.subscribity.data.mapper.toDomain
import com.opsat.subscribity.data.mapper.toEntity
import com.opsat.subscribity.domain.model.Subscription
import com.opsat.subscribity.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SubscriptionRepositoryImpl @Inject constructor(
    private val dao: SubscriptionDao,
) : SubscriptionRepository {
    override fun observeSubscriptions(): Flow<List<Subscription>> =
        dao.observeSubscriptions().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getSubscription(id: Long): Subscription? =
        dao.getSubscription(id)?.toDomain()

    override suspend fun addSubscription(subscription: Subscription): Long =
        dao.insert(subscription.toEntity())

    override suspend fun updateSubscription(subscription: Subscription) {
        dao.update(subscription.toEntity())
    }
}
