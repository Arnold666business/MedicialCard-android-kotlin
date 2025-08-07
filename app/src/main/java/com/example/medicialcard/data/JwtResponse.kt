package com.example.medicialcard.data

data class JwtResponse (
    val jwt: String,
    val patientId: Long
)