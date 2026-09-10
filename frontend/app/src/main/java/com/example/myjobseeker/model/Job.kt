package com.example.myjobseeker.model

import java.io.Serializable

data class Job(
    val id: Int,
    val title: String,
    val companyName: String,
    val description: String,
    val location: String,
    val salary: String,
    val logoResId: Int = 0,
    val creatorId: Int = -1,
    val imageUri: String? = null
) : Serializable
