package com.namnp.realmandroid.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namnp.realmandroid.data.models.Course
import com.namnp.realmandroid.data.repository.RealmAppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RealmAppViewModel @Inject constructor(
    private val repository: RealmAppRepository,
) : ViewModel() {

    val courses = repository
        .getCourses()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    var selectedCourse: Course? by mutableStateOf(null)
        private set

    init {
//        createSampleData()
    }

    fun showCourseDetails(course: Course) {
        selectedCourse = course
    }

    fun hideCourseDetails() {
        selectedCourse = null
    }

    private fun createSampleData() {
        viewModelScope.launch {
            repository.createSampleData()
        }
    }

    fun deleteCourse() {
        selectedCourse?.let { course ->
            viewModelScope.launch {
                repository.deleteCourse(course)
                selectedCourse = null
            }
        }

    }
}