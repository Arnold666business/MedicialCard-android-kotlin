package com.example.medicialcard.data

import java.time.LocalDate

data class PatientDto(
    val id: Long,

    val login: String,

    val password: String,

    val email: String,

    val phone: String,

    val lastName: String,

    val firstName: String,

    val dateOfRegistration: String,

    val birthDate: String
)