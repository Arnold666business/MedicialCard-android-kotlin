package com.example.medicialcard.data

data class DoctorDto(
    val doctorId: Long,

    val phone: String,

    val lastName: String,

    val photoUrl: String,

    val firstName: String,

    val rating: Double
)