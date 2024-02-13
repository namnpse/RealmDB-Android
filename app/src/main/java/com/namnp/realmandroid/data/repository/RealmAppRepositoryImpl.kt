package com.namnp.realmandroid.data.repository

import com.namnp.realmandroid.data.models.Address
import com.namnp.realmandroid.data.models.Course
import com.namnp.realmandroid.data.models.Student
import com.namnp.realmandroid.data.models.Teacher
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class RealmAppRepositoryImpl @Inject constructor(
    private val realm: Realm,
) : RealmAppRepository {

    override fun getAllCourses(): Flow<List<Course>> {
        return realm
            .query<Course>()
            .limit(20)
            .asFlow()
            .map {
                it.list.toList()
            }
            .flowOn(Dispatchers.IO)
            .catch { exception -> // Executes in the consumer's context
//                emit()
            }
    }

    override fun getRealmCourses(): Flow<ResultsChange<Course>> {
        return realm
            .query<Course>()
            .limit(20)
            .asFlow()
            .flowOn(Dispatchers.IO)
    }

    override suspend fun findAllCoursesWithoutUsingFlow(): List<Course> {
        return realm
            .query<Course>()
            .limit(20)
            .find()
            .map { it } // map RealmResults<Course> to List<Course>
    }

    override suspend fun deleteCourse(course: Course) {
        realm.write {
            // avoid race condition, when delete the same course before
            // need to find before delete to avoid race condition (delete an already deleted record)
            val latestCourse = findLatest(course) ?: return@write
            delete(latestCourse)
        }
    }

    override fun getCoursesByEnrolledStudentName(studentName: String): Flow<List<Course>> {
        return realm
            .query<Course>(
                "enrolledStudents.name == $0",
                studentName,
            )
            .asFlow()
            .map {
                it.list.toList()
            }
            .flowOn(Dispatchers.IO)
    }

    override fun getCoursesEnrolledByManyStudents(numbersOfStudents: Int): Flow<List<Course>> {
        return realm
            .query<Course>(
                "enrolledStudents.@count >= $0",
                numbersOfStudents,
            )
            .asFlow()
            .map {
                it.list.toList()
            }
            .flowOn(Dispatchers.IO)
    }

    override fun getCoursesByTeacher(teacher: Teacher): Flow<List<Course>> {
        return realm
            .query<Course>(
                "teacher.address.fullName CONTAINS $0",
                teacher.address?.fullName ?: "",
            )
            .asFlow()
            .map {
                it.list.toList()
            }
            .flowOn(Dispatchers.IO)
    }

    override fun getCoursesWithName(courseName: String): Flow<List<Course>> {
        return realm
            .query<Course>(
                "name BEGINSWITH $0",
                courseName,
            )
            .asFlow()
            .map {
                it.list.toList()
            }
            .flowOn(Dispatchers.IO)
    }

    override fun getAllAvailableCourses(): Flow<List<Course>> {
        return realm
            .query<Course>("isAvailable == true")
            .asFlow()
            .map {
                it.list.toList()
            }
    }

    override suspend fun updateCourseAvailability(course: Course, isAvailable: Boolean) {
        // Modify the Realm file while blocking the calling thread until the transaction is done
        realm.writeBlocking {
            // avoid race condition, when updating an already deleted course
            findLatest(course)?.isAvailable = isAvailable
        }
    }


    override suspend fun createSampleData() {
        realm.write {
            val address1 = Address().apply {
                fullName = "Namnpse Nguyen"
                street = "Hill Street"
                houseNumber = 240
                zip = 123
                city = "Hillcity"
            }
            val address2 = Address().apply {
                fullName = "Bryan Nguyen"
                street = "Bryan Nguyen Street"
                houseNumber = 250
                zip = 1234
                city = "Hillcity"
            }

            val course1 = Course().apply {
                name = "Kotlin Programming Made Easy"
            }
            val course2 = Course().apply {
                name = "Android Basics"
            }
            val course3 = Course().apply {
                name = "Asynchronous Programming With Coroutines"
            }

            val teacher1 = Teacher().apply {
                address = address1
                courses = realmListOf(course1, course2)
            }
            val teacher2 = Teacher().apply {
                address = address2
                courses = realmListOf(course3)
            }

            course1.teacher = teacher1
            course2.teacher = teacher1
            course3.teacher = teacher2

            address1.teacher = teacher1
            address2.teacher = teacher2

            val student1 = Student().apply {
                name = "Nam Junior"
            }
            val student2 = Student().apply {
                name = "Nam Senior"
            }

            course1.enrolledStudents.add(student1)
            course2.enrolledStudents.add(student2)
            course3.enrolledStudents.addAll(listOf(student1, student2))

            // UpdatePolicy.ALL: if have the same id, update all other fields of the object
            copyToRealm(teacher1, updatePolicy = UpdatePolicy.ALL)
            copyToRealm(teacher2, updatePolicy = UpdatePolicy.ALL)

            copyToRealm(course1, updatePolicy = UpdatePolicy.ALL)
            copyToRealm(course2, updatePolicy = UpdatePolicy.ALL)
            copyToRealm(course3, updatePolicy = UpdatePolicy.ALL)

            copyToRealm(student1, updatePolicy = UpdatePolicy.ALL)
            copyToRealm(student2, updatePolicy = UpdatePolicy.ALL)
        }
    }

    override fun closeRealm() {
        realm.close()
    }

}