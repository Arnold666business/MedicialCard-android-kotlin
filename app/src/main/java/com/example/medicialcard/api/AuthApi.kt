package com.example.medicialcard.api

import com.example.medicialcard.data.JwtRequest
import com.example.medicialcard.data.RegisterRequest
import com.example.medicialcard.data.JwtResponse
import com.example.medicialcard.data.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST


interface AuthApi {

    @Headers("Content-Type: application/json")
    @POST("/api/register")
    suspend fun register(@Body request: RegisterRequest): Response<JwtResponse>

    @Headers("Content-Type: application/json")
    @POST("/api/login")
    suspend fun login(@Body request: LoginRequest): Response<JwtResponse>

    @Headers("Content-Type: application/json")
    @POST("/api/jwt")
    suspend fun jwtIsValid(@Body token: JwtRequest): Response<Boolean>


}