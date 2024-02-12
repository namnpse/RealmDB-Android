package com.namnp.realmandroid.di

import com.namnp.realmandroid.data.repository.RealmAppRepository
import com.namnp.realmandroid.data.repository.RealmAppRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindRepository(
        repositoryImpl: RealmAppRepositoryImpl
    ): RealmAppRepository
}