package com.example.myjobseeker.model

import java.io.Serializable
import java.time.LocalDateTime

data class Application(
    val id: Int,
    val job: Job,
    val status: ApplicationStatus,
    val appliedAt: LocalDateTime,
    val responseTimeEstimate: String = "1-2 hari"
) : Serializable

enum class ApplicationStatus {
    DIPROSES,
    DITERIMA,
    DITOLAK
}
