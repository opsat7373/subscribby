package com.opsat.subscribity.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.opsat.subscribity.BuildConfig
import com.opsat.subscribity.data.local.MIGRATION_1_2
import com.opsat.subscribity.data.local.SubscribityDatabase
import com.opsat.subscribity.data.local.SubscriptionDao
import com.opsat.subscribity.data.seed.SubscriptionSeeder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideSubscribityDatabase(
        @ApplicationContext context: Context,
        databaseProvider: Provider<SubscribityDatabase>,
        @ApplicationScope applicationScope: CoroutineScope,
    ): SubscribityDatabase =
        Room.databaseBuilder(context, SubscribityDatabase::class.java, "subscribity.db")
            .addMigrations(MIGRATION_1_2)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    if (BuildConfig.DEBUG) {
                        applicationScope.launch {
                            SubscriptionSeeder.seed(databaseProvider.get().subscriptionDao())
                        }
                    }
                }
            })
            .build()

    @Provides
    fun provideSubscriptionDao(database: SubscribityDatabase): SubscriptionDao = database.subscriptionDao()
}
