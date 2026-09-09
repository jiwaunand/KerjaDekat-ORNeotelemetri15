package com.example.myjobseeker.viewmodel

import android.app.Application as AndroidApp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myjobseeker.data.AppDatabase
import com.example.myjobseeker.model.Application
import com.example.myjobseeker.model.ApplicationStatus
import com.example.myjobseeker.model.Bookmark
import com.example.myjobseeker.model.Job
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class JobViewModel(application: AndroidApp) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()
    
    private val _currentUserId = MutableStateFlow(-1)

    val applications: StateFlow<List<Application>> = _currentUserId
        .flatMapLatest { userId ->
            if (userId != -1) userDao.getApplicationsByUserId(userId)
            else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedJobIds: StateFlow<List<Int>> = _currentUserId
        .flatMapLatest { userId ->
            if (userId != -1) userDao.getBookmarkedJobIds(userId)
            else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setCurrentUser(userId: Int) {
        _currentUserId.value = userId
    }

    fun applyForJob(job: Job) {
        val userId = _currentUserId.value
        if (userId == -1) return

        viewModelScope.launch {
            val currentApps = applications.value
            if (currentApps.any { it.jobId == job.id }) return@launch

            val newApplication = Application(
                userId = userId,
                jobId = job.id,
                status = ApplicationStatus.DIPROSES,
                appliedAt = LocalDateTime.now().toString()
            )
            userDao.insertApplication(newApplication)
        }
    }

    fun cancelApplication(applicationId: Int) {
        viewModelScope.launch {
            userDao.deleteApplication(applicationId)
        }
    }

    fun toggleBookmark(job: Job) {
        val userId = _currentUserId.value
        if (userId == -1) return

        viewModelScope.launch {
            if (bookmarkedJobIds.value.contains(job.id)) {
                userDao.deleteBookmark(userId, job.id)
            } else {
                userDao.insertBookmark(Bookmark(userId = userId, jobId = job.id))
            }
        }
    }

    fun isBookmarked(jobId: Int): Boolean {
        return bookmarkedJobIds.value.contains(jobId)
    }
}
