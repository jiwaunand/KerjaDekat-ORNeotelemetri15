package com.example.myjobseeker.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.io.Serializable
import java.time.LocalDateTime

@Entity(tableName = "applications")
data class Application(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val jobId: Int,
    val status: ApplicationStatus,
    val appliedAt: String, // String for Room simplicity, or use TypeConverter
    val responseTimeEstimate: String = "1-2 hari"
) : Serializable

enum class ApplicationStatus {
    DIPROSES,
    DITERIMA,
    DITOLAK
}
