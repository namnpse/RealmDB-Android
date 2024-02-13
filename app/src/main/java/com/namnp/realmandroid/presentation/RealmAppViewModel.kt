package com.namnp.realmandroid.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namnp.realmandroid.data.models.Course
import com.namnp.realmandroid.data.repository.RealmAppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RealmAppViewModel @Inject constructor(
    private val repository: RealmAppRepository,
) : ViewModel() {

    val courses = repository
        .getAllCourses()
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

    fun observeAllCourseChange() {
        viewModelScope.launch {
            repository.getRealmCourses() // Execute on the IO dispatcher
                // flowOn affects the upstream flow ↑
                .flowOn(Dispatchers.IO) // should move to repo func getRealmCourses()
                // the downstream flow ↓ is not affected
                .catch { exception -> // Executes in the consumer's context
                // emit()
                }
                .collect { changes: ResultsChange<Course> ->
                    // flow.collect() is blocking -- run it in a background context
                    when (changes) {
                        // UpdatedResults means this change represents an update/insert/delete operation
                        is UpdatedResults -> {
                            changes.insertions // indexes of inserted objects
                            changes.insertionRanges // ranges of inserted objects
                            changes.changes // indexes of modified objects
                            changes.changeRanges // ranges of modified objects
                            changes.deletions // indexes of deleted objects
                            changes.deletionRanges // ranges of deleted objects
                            changes.list // the full collection of objects
                        }
                        else -> {
                            // types other than UpdatedResults are not changes -- ignore them
                        }
                    }
                }
        }
    }
}