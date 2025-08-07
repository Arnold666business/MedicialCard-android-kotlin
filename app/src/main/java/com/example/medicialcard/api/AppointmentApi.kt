package com.example.medicialcard.api

import com.example.medicialcard.data.AppointmentDto
import com.example.medicialcard.data.ReviewCanRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AppointmentApi {
    @POST("/api/appointment")
    suspend fun checkToPossabilityToSendReview(@Body request: ReviewCanRequest) : Response<Boolean>

    @GET("/api/{patient_id}/appointment/{doctor_id}")
    suspend fun sendNotificationToCreateAppointment(@Path("patient_id") patientId: String?, @Path("doctor_id") doctorId: Long): Response<Unit>

    @GET("/api/appointment/{patient_id}")
    suspend fun getAllAppointment(@Path("patient_id") id: Long?) : Response<List<AppointmentDto>>

}