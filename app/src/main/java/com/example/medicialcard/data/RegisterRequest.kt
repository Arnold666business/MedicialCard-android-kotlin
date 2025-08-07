package com.example.medicialcard.data

import java.time.LocalDate

data class RegisterRequest(
    var login: String,
    var password: String,
    var  email: String,
    var  phone: String,
    var  lastName: String,
    var firstName: String,
    var birthDate: String
)

