package com.opsat.subscribity.di

import android.content.Context
import androidx.room.Room
import com.opsat.subscribity.data.local.SubscribityDatabase
import com.opsat.subscribity.data.local.SubscriptionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideSubscribityDatabase(@ApplicationContext context: Context): SubscribityDatabase =
        Room.databaseBuilder(context, SubscribityDatabase::class.java, "subscribity.db").build()

    @Provides
    fun provideSubscriptionDao(database: SubscribityDatabase): SubscriptionDao = database.subscriptionDao()
}
