package com.example.medicialcard.ui.review.reviewsList

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicialcard.api.RetrofitClient
import com.example.medicialcard.data.DoctorDto
import com.example.medicialcard.data.ReviewFormDto
import com.example.medicialcard.ui.appointment.AppointmentViewModel.AppointmentState
import com.example.medicialcard.ui.review.ReviewFormViewModel
import com.example.medicialcard.utils.JwtManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class ReviewsListViewModel(private val doctorId: Long) : ViewModel() {
    var _reviewsState = MutableStateFlow<ReviewsState>(ReviewsState.Idle)
    val reviewsState: StateFlow<ReviewsState> = _reviewsState.asStateFlow()

    var _doctorName = MutableStateFlow<String>("Имя доктора")
    val doctorName = _doctorName.asStateFlow()

    private var _reviewsCache = mutableStateMapOf<Long, String>()

    sealed class ReviewsState {
        object Idle: ReviewsState()
        object Loading: ReviewsState()
        data class Success(val reviews: List<ReviewFormDto>): ReviewsState()
        data class Error(val message: String): ReviewsState()
    }

    init{
        loadDoctorName()
        loadReviewsList()
    }

    private fun loadDoctorName(){
        viewModelScope.launch {
            val doctor = RetrofitClient.getDoctor().getDoctor(doctorId).body()?.doctor
            _doctorName.value = doctor?.firstName + doctor?.lastName
        }
    }

    private fun loadReviewsList(){
        viewModelScope.launch {
            _reviewsState.value = ReviewsState.Loading
            try {
                var response: Response<List<ReviewFormDto>> = RetrofitClient.getReviewApi().getReviews(doctorId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        it.forEach({
                           if(!_reviewsCache.containsKey(it.patientId)){
                               loadPatientNameToCache(it.patientId)
                           }
                        })
                        _reviewsState.value = ReviewsState.Success(it)
                    } ?: throw Exception("empty body reviewslist")
                }else {
                    throw Exception("HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                _reviewsState.value = ReviewsState.Error("reviews list" + (e.message?:"unknown error"))
            }
        }
    }

    private fun loadPatientNameToCache(patientId: Long){
        viewModelScope.launch {
            try {
                val response = RetrofitClient.getPatient().getPatient(patientId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _reviewsCache[patientId] = "${it.firstName} ${it.lastName}"
                    }
                }
            } catch (e: Exception) {
                _reviewsState.value = ReviewsState.Error(e.message ?: "try to get patient by id in reviews Неизвестная ошибка")
            }
        }
    }

    fun getPatientName(patientId: Long): String? {
        return _reviewsCache[patientId]
    }

    class Factory(private val doctorId: Long) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReviewsListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ReviewsListViewModel(doctorId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
