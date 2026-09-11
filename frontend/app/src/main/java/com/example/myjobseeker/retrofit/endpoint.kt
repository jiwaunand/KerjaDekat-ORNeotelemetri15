package com.example.myjobseeker.retrofit

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class JobResponse(
    @SerializedName("data") val data: List<JobApiData>,
    @SerializedName("pagination") val pagination: PaginationApiData? = null
)

data class JobApiData(
    @SerializedName("id") val id: Int,
    @SerializedName("job_name") val jobName: String?,
    @SerializedName("nama_perusahaan") val namaPerusahaan: String?,
    @SerializedName("deskripsi_utama") val deskripsiUtama: String?,
    @SerializedName("lokasi") val lokasi: String?,
    @SerializedName("perkiraan_salary") val perkiraanSalary: Int?,
    @SerializedName("status_enum") val statusEnum: String?,
    @SerializedName("scoring") val scoring: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("created_at") val createdAt: String?
)

data class PaginationApiData(
    @SerializedName("page") val page: Int,
    @SerializedName("limit") val limit: Int,
    @SerializedName("hasNextPage") val hasNextPage: Boolean
)

data class CreateJobRequest(
    @SerializedName("job_name") val jobName: String,
    @SerializedName("nama_perusahaan") val namaPerusahaan: String,
    @SerializedName("deskripsi_utama") val deskripsiUtama: String,
    @SerializedName("lokasi") val lokasi: String,
    @SerializedName("perkiraan_salary") val perkiraanSalary: Int,
    @SerializedName("image_url") val imageUrl: String
)

interface JobApiService {
    @GET("jobs")
    suspend fun getJobs(): JobResponse

    @retrofit2.http.POST("jobs")
    suspend fun createJob(@retrofit2.http.Body request: CreateJobRequest): JobApiData
}

object RetrofitInstance {
    private const val BASE_URL = "https://kerjadekat.my.id/"

    val api: JobApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JobApiService::class.java)
    }
}
