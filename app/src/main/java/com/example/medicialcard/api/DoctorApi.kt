package com.example.medicialcard.api

import com.example.medicialcard.data.CategoryDto
import com.example.medicialcard.data.DoctorDto
import com.example.medicialcard.data.DoctorProfile
import com.example.medicialcard.data.JwtResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DoctorApi {
    @GET("/api/category")
    suspend fun getAllCategories(): Response<List<CategoryDto>>

    @GET("/api/doctors/{category_id}")
    suspend fun getDoctors(@Path("category_id") categoryId: Long): Response<List<DoctorDto>>

    @GET("/api/doctors/{category_id}/sort-by-rating")
    suspend fun getSortedByRatingDoctors(@Path("category_id") categoryId: Long): Response<List<DoctorDto>>

    @GET("/api/doctor/{doctor_id}")
    suspend fun getDoctor(@Path("doctor_id") doctorId: Long) : Response<DoctorProfile>

    @GET("/api/doctor/{doctor_id}/rating/recalculation")
    suspend fun recalculateDoctorRating(@Path("doctor_id") doctorId : Long): Response<Unit>

}