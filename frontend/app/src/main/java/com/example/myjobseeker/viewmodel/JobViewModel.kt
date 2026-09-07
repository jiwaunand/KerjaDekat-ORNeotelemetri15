package com.example.myjobseeker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import com.example.myjobseeker.model.Application
import com.example.myjobseeker.model.ApplicationStatus
import com.example.myjobseeker.model.Job
import java.time.LocalDateTime

class JobViewModel : ViewModel() {
    private val _applications = mutableStateListOf<Application>()
    val applications: List<Application> = _applications

    fun applyForJob(job: Job) {
        // Avoid duplicate applications for the same job if needed
        if (_applications.any { it.job.id == job.id }) return

        val newApplication = Application(
            id = _applications.size + 1,
            job = job,
            status = ApplicationStatus.DIPROSES,
            appliedAt = LocalDateTime.now()
        )
        _applications.add(newApplication)
    }

    fun cancelApplication(applicationId: Int) {
        _applications.removeIf { it.id == applicationId }
    }
}
