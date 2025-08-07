package com.example.medicialcard.utils

import android.annotation.SuppressLint
import com.example.medicialcard.data.RegisterRequest
import java.time.LocalDate

object RegisterValidator {
    @SuppressLint("NewApi")
    fun validate(regInfo: RegisterRequest): List<String> = buildList {
        if (regInfo.login.isBlank()) add("Логин не должен быть пустым")
        if (regInfo.password.length < 6) add("Минимальная пароля логина 6")
        if (!regInfo.email.contains("@")) add("Неверный формат почты")
        if (regInfo.phone.length !in 10..15) add("Допустимая длина номера телефона от 10 до 15 цифр")
        if (regInfo.firstName.isBlank()) add("имя не может быть пустым")
        if (regInfo.lastName.isBlank()) add("фамилия не может быть пустой")
        if (!LocalDate.parse(regInfo.birthDate).isBefore(LocalDate.now())) add("Недопустимая дата рождения")
    }
}