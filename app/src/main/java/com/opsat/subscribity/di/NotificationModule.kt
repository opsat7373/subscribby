package com.opsat.subscribity.di

import com.opsat.subscribity.data.notification.AlarmManagerNotificationScheduler
import com.opsat.subscribity.domain.notification.NotificationScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {
    @Binds
    @Singleton
    abstract fun bindNotificationScheduler(impl: AlarmManagerNotificationScheduler): NotificationScheduler
}
