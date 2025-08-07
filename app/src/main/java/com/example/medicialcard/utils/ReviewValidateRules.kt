package com.example.medicialcard.utils

import com.example.medicialcard.data.RegisterRequest
import com.example.medicialcard.data.ReviewFormDto
import java.time.LocalDate

object ReviewValidateRules {
    fun validate(review: ReviewFormDto): List<String> = buildList {
        if(review.text.isBlank()) add("Комментарий не доожен быть пустым")
        if(review.text.length > 5000) add("Можно оставлять только до 5000 символов")
    }
}