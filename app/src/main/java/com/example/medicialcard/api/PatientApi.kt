package com.example.medicialcard.api

import com.example.medicialcard.data.PatientDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PatientApi {
    @GET("/api/patient/{patient_id}")
    suspend fun getPatient(@Path("patient_id") id: Long?): Response<PatientDto>

}