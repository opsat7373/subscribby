package com.opsat.subscribity.data.seed

import com.opsat.subscribity.data.local.SubscriptionDao
import com.opsat.subscribity.data.mapper.toEntity

object SubscriptionSeeder {
    suspend fun seed(dao: SubscriptionDao) {
        dao.insertAll(SubscriptionSeedData.subscriptions.map { it.toEntity() })
    }
}
