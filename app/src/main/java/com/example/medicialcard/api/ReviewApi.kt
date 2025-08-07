package com.example.medicialcard.api

import com.example.medicialcard.data.ReviewFormDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ReviewApi {
    @POST("/api/reviews")
    @Headers("Content-Type: application/json")
    suspend fun sendReview(@Body reviewRequest: ReviewFormDto) : Response<Unit>

    @GET("/api/reviews/{doctor_id}")
    suspend fun getReviews(@Path("doctor_id") doctorId: Long) : Response<List<ReviewFormDto>>


}