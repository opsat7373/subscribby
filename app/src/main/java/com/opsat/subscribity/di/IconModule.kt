package com.opsat.subscribity.di

import com.opsat.subscribity.data.icon.IconFileStoreImpl
import com.opsat.subscribity.domain.repository.IconStorage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class IconModule {
    @Binds
    @Singleton
    abstract fun bindIconStorage(impl: IconFileStoreImpl): IconStorage
}
