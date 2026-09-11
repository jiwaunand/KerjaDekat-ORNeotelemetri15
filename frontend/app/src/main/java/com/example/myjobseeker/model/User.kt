package com.example.myjobseeker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val email: String,
    val password: String,
    val fullName: String = "",
    val nickname: String = "",
    val age: Int = 0,
    val gender: String = "",
    val biodata: String = "",
    val address: String = "",
    val postalCode: String = "",
    val education: String = "",
    val skills: String = "" // Stored as comma-separated values
)
