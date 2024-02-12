package com.namnp.realmandroid.di

import com.namnp.realmandroid.data.models.Address
import com.namnp.realmandroid.data.models.Course
import com.namnp.realmandroid.data.models.Student
import com.namnp.realmandroid.data.models.Teacher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RealmAppModule {

    @Provides
    @Singleton
    fun provideRealmDB(): Realm {
        return Realm.open(
            configuration = RealmConfiguration.create(
                schema = setOf(
                    Course::class,
                    Student::class,
                    Teacher::class,
                    Address::class,
                )
            )
        )
    }
}