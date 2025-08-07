package com.example.medicialcard.data

data class AppointmentDto(
    val patientId: Long,
    val doctorId: Long,
    val reason: String,
    val date: String,
    val status: String
)