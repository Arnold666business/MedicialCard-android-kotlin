package com.example.medicialcard.ui.review

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.medicialcard.api.RetrofitClient
import com.example.medicialcard.data.JwtResponse
import com.example.medicialcard.data.ReviewFormDto
import com.example.medicialcard.ui.doctorsInCategory.DoctorsListViewModel
import com.example.medicialcard.ui.register.RegisterViewModel.RegisterState
import com.example.medicialcard.utils.JwtManager
import com.example.medicialcard.utils.RegisterValidator
import com.example.medicialcard.utils.ReviewValidateRules
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class ReviewFormViewModel(private val doctorId: Long) : ViewModel() {
    private var _reviewFormState = MutableStateFlow<ReviewFormState>(ReviewFormState.Idle)
    val reviewFormState: StateFlow<ReviewFormState> = _reviewFormState.asStateFlow()

    private var _doctorName = MutableStateFlow<String>("Имя доктора")
    val doctorName: StateFlow<String> = _doctorName.asStateFlow()

    private var _patientId = MutableStateFlow<Long>(JwtManager.getCurrentPatientId()?.toLong() ?: throw IllegalStateException("Patient ID is null"))
    val patientId: StateFlow<Long> = _patientId.asStateFlow()

    sealed class ReviewFormState {
        object Idle: ReviewFormState()
        object Loading: ReviewFormState()
        object Success: ReviewFormState()
        data class Error(val message: String): ReviewFormState()
    }

    init {
        loadDoctorName()
    }

    fun sendReview(reviewRequest: ReviewFormDto){
        viewModelScope.launch {
            _reviewFormState.value = ReviewFormState.Loading
            try {

                val validateErrorList: List<String> = ReviewValidateRules.validate(reviewRequest)
                if(!validateErrorList.isEmpty()){
                    _reviewFormState.value = ReviewFormState.Error(validateErrorList.get(0))
                } else{
                    val response: Response<Unit> = RetrofitClient.getReviewApi().sendReview(reviewRequest)
                    if(response.isSuccessful){
                        response.body()?.let {
                            _reviewFormState.value = ReviewFormState.Success
                            notificationToRecalculateDoctorRating(doctorId)
                        } ?: throw Exception("Register Empty response")
                    } else{
                        throw Exception("HTTP ${response.code()}")
                    }
                }
            } catch (e: Exception){
                _reviewFormState.value = ReviewFormState.Error(e.message ?: "Unknown error with review form")
            }
        }
    }

    private fun notificationToRecalculateDoctorRating(id: Long){
        viewModelScope.launch {
            try{
                val response: Response<Unit> = RetrofitClient.getDoctor().recalculateDoctorRating(id)
                if(!response.isSuccessful){
                    throw Exception("HTTP ${response.code()}")
                }
            } catch (e: Exception){
                _reviewFormState.value = ReviewFormState.Error(e.message ?: "Unknown error with review form")
            }

        }
    }

    private fun loadDoctorName(){
        viewModelScope.launch {
            _doctorName.value =  RetrofitClient.getDoctor()
                .getDoctor(doctorId)
                .body()?.let { it.doctor.firstName + it.doctor.lastName } ?:"Имя ненайдено"
        }
    }

    class Factory(private val doctorId: Long) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReviewFormViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ReviewFormViewModel(doctorId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}