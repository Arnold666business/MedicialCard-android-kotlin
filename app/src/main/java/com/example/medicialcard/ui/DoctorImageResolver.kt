package com.example.medicialcard.ui


import com.example.medicialcard.R

object DoctorImageResolver {
    private val doctorImages = mapOf(
        152L to R.drawable.doc152,
        153L to R.drawable.doc153,
        154L to R.drawable.doc154,
        155L to R.drawable.doc155,
        156L to R.drawable.doc156,
        157L to R.drawable.doc157,
        158L to R.drawable.doc158
    )

    fun getImageResource(id: Long): Int{
        return doctorImages.get(id)?:R.drawable.man
    }
}