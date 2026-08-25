package com.opsat.subscribity.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [SubscriptionEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class SubscribityDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
}
