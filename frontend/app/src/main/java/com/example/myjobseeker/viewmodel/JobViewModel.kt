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

    private val _jobs = MutableStateFlow(emptyList<Job>())
    val jobs: StateFlow<List<Job>> = _jobs.asStateFlow()

    init {
        fetchJobsFromApi()
    }

    fun fetchJobsFromApi() {
        viewModelScope.launch {
            try {
                val response = com.example.myjobseeker.retrofit.RetrofitInstance.api.getJobs()
                val mappedJobs = response.data.map { apiJob ->
                    val formatter = java.text.NumberFormat.getNumberInstance(java.util.Locale("in", "ID"))
                    val salaryStr = apiJob.perkiraanSalary?.let { "Rp. ${formatter.format(it)}" } ?: "Rp. 0"
                    Job(
                        id = apiJob.id,
                        title = apiJob.jobName ?: "",
                        companyName = apiJob.namaPerusahaan ?: "",
                        description = apiJob.deskripsiUtama ?: "",
                        location = apiJob.lokasi ?: "",
                        salary = salaryStr,
                        logoResId = 0,
                        creatorId = -1,
                        imageUri = apiJob.imageUrl
                    )
                }
                _jobs.value = mappedJobs
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

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

    fun deleteApplication(applicationId: Int) {
        viewModelScope.launch {
            userDao.deleteApplication(applicationId)
        }
    }

    fun clearApplicationsByStatus(status: ApplicationStatus) {
        val userId = _currentUserId.value
        if (userId != -1) {
            viewModelScope.launch {
                userDao.deleteApplicationsByStatus(userId, status)
            }
        }
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

    fun addJob(job: Job) {
        val userId = _currentUserId.value
        if (userId == -1) return
        
        viewModelScope.launch {
            try {
                // Parse salary from string to Int (e.g. "Rp. 70.000" -> 70000)
                val cleanSalary = job.salary.replace(Regex("[^0-9]"), "")
                val salaryInt = cleanSalary.toIntOrNull() ?: 0

                val request = com.example.myjobseeker.retrofit.CreateJobRequest(
                    jobName = job.title,
                    namaPerusahaan = job.companyName,
                    deskripsiUtama = job.description,
                    lokasi = job.location,
                    perkiraanSalary = salaryInt,
                    imageUrl = job.imageUri ?: ""
                )
                
                com.example.myjobseeker.retrofit.RetrofitInstance.api.createJob(request)
                
                // Refresh jobs from API after successful addition
                fetchJobsFromApi()

                userDao.insertNotification(
                    com.example.myjobseeker.model.Notification(
                        userId = userId,
                        title = "Pekerjaan Ditambahkan",
                        message = "Pekerjaan baru '${job.title}' telah berhasil ditambahkan ke server."
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteJob(jobId: Int) {
        _jobs.value = _jobs.value.filter { it.id != jobId }
    }
}
