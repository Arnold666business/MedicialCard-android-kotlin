package com.example.medicialcard.api


import com.example.medicialcard.utils.Endpoints

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private lateinit var authApi: AuthApi
    private lateinit var doctorApi: DoctorApi
    private lateinit var patientApi: PatientApi
    private lateinit var appointmentApi: AppointmentApi
    private lateinit var reviewApi: ReviewApi

    fun init(){

        val retrofit = Retrofit.Builder()
            .baseUrl(Endpoints.BASE_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        this.authApi = retrofit.create(AuthApi::class.java)
        this.doctorApi = retrofit.create(DoctorApi::class.java)
        this.patientApi = retrofit.create(PatientApi::class.java)
        this.appointmentApi = retrofit.create(AppointmentApi::class.java)
        this.reviewApi = retrofit.create(ReviewApi::class.java)
    }
    fun getAuth() = authApi
    fun getDoctor() = doctorApi
    fun getPatient() = patientApi
    fun getAppointment() = appointmentApi
    fun getReviewApi() = reviewApi

}