package com.namnp.realmandroid.data.repository

import com.namnp.realmandroid.data.models.Course
import kotlinx.coroutines.flow.Flow

interface RealmAppRepository {

    fun getCourses(): Flow<List<Course>>

    suspend fun createSampleData()

    suspend fun deleteCourse(course: Course)
}