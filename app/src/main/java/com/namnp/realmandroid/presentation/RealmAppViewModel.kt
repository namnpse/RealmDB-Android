package com.namnp.realmandroid.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namnp.realmandroid.data.models.Address
import com.namnp.realmandroid.data.models.Course
import com.namnp.realmandroid.data.models.Student
import com.namnp.realmandroid.data.models.Teacher
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class RealmAppViewModel @Inject constructor(
    private val realm: Realm,
) : ViewModel() {

    val courses = realm
        .query<Course>()
        .asFlow()
        .map {
            it.list.toList()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    var courseDetails: Course? by mutableStateOf(null)
        private set

    init {
        createSampleEntries()
    }

    fun showCourseDetails(course: Course) {
        courseDetails = course
    }

    fun hideCourseDetails() {
        courseDetails = null
    }

    private fun createSampleEntries() {
        viewModelScope.launch {
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
    }

    fun deleteCourse() {
        viewModelScope.launch {
            realm.write {
                val course = courseDetails ?: return@write
                // avoid race condition, when delete the same course before
                val latestCourse = findLatest(course) ?: return@write
                delete(latestCourse)

                courseDetails = null
            }
        }
    }
}