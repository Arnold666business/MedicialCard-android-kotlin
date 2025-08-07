package com.example.medicialcard.utils

object Routes {
    const val START = "start"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PATIENT = "patient"
    const val CATEGORY = "category"
    const val DOCTORS_LIST = "doctors/{categoryId}"
    const val DOCTOR_DETAIL = "doctor/{doctorId}"
    const val APPOINTMENTS = "appointments"
    const val REVIEWS_FORM = "reviews/creating/{doctorId}"
    const val REVIEWS = "reviews/{doctorId}"
}