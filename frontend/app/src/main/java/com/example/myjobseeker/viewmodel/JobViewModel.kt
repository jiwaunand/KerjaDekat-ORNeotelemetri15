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

    val notifications: StateFlow<List<com.example.myjobseeker.model.Notification>> = _currentUserId
        .flatMapLatest { userId ->
            if (userId != -1) userDao.getNotificationsByUserId(userId)
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
            userDao.insertNotification(
                com.example.myjobseeker.model.Notification(
                    userId = userId,
                    title = "Lamaran Baru",
                    message = "Lamaran baru telah ditambahkan, anda ingin melihat?"
                )
            )
        }
    }

    fun updateApplicationStatus(applicationId: Int, status: ApplicationStatus) {
        val userId = _currentUserId.value
        if (userId == -1) return
        viewModelScope.launch {
            userDao.updateApplicationStatus(applicationId, status)
            val message = when (status) {
                ApplicationStatus.DITERIMA -> "Selamat! lamaran anda baru saja diterima, silahkan dilihat!"
                ApplicationStatus.DITOLAK -> "Sayangnya, lamaran anda telah ditolak. berusaha lebih giat lagi!"
                else -> null
            }
            message?.let {
                userDao.insertNotification(
                    com.example.myjobseeker.model.Notification(
                        userId = userId,
                        title = if (status == ApplicationStatus.DITERIMA) "Lamaran Diterima" else "Lamaran Ditolak",
                        message = it
                    )
                )
            }
        }
    }

    fun cancelApplication(applicationId: Int) {
        updateApplicationStatus(applicationId, ApplicationStatus.DITOLAK)
    }

    fun markNotificationAsRead(notificationId: Int) {
        viewModelScope.launch {
            userDao.markAsRead(notificationId)
        }
    }

    fun clearNotifications() {
        val userId = _currentUserId.value
        if (userId != -1) {
            viewModelScope.launch {
                userDao.clearNotifications(userId)
            }
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
