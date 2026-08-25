package com.opsat.subscribity.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions ORDER BY nextPaymentDate ASC")
    fun observeSubscriptions(): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions WHERE id = :id")
    suspend fun getSubscription(id: Long): SubscriptionEntity?

    @Insert
    suspend fun insert(entity: SubscriptionEntity): Long

    @Insert
    suspend fun insertAll(entities: List<SubscriptionEntity>)

    @Update
    suspend fun update(entity: SubscriptionEntity)
}
