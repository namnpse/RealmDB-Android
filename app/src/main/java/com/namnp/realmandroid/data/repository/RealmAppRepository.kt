package com.namnp.realmandroid.data.repository

import com.namnp.realmandroid.data.models.Course
import com.namnp.realmandroid.data.models.Teacher
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow

interface RealmAppRepository {

    fun getAllCourses(): Flow<List<Course>>

    fun getRealmCourses(): Flow<ResultsChange<Course>>

    suspend fun findAllCoursesWithoutUsingFlow(): List<Course>

    suspend fun createSampleData()

    suspend fun deleteCourse(course: Course)

    fun getCoursesByEnrolledStudentName(studentName: String): Flow<List<Course>>

    fun getCoursesEnrolledByManyStudents(numbersOfStudents: Int): Flow<List<Course>>

    fun getCoursesByTeacher(teacher: Teacher): Flow<List<Course>>

    fun getCoursesWithName(courseName: String): Flow<List<Course>>

    fun getAllAvailableCourses(): Flow<List<Course>>

    suspend fun updateCourseAvailability(course: Course, isAvailable: Boolean)

    fun closeRealm()
}