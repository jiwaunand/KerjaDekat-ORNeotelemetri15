package com.example.myjobseeker.model

import java.io.Serializable

data class Job(
    val id: Int,
    val title: String,
    val companyName: String,
    val location: String,
    val matchPercentage: Int,
    val distance: String,
    val salary: String,
    val timeRange: String,
    val skill: String,
    val logoResId: Int = 0,
    val creatorId: Int = -1,
    val imageUri: String? = null
) : Serializable
