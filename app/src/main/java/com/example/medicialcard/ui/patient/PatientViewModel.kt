package com.example.medicialcard.ui.patient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicialcard.api.RetrofitClient
import com.example.medicialcard.data.PatientDto
import com.example.medicialcard.utils.JwtManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response


class PatientViewModel : ViewModel() {
    private var _patientDataState = MutableStateFlow<PatientState>(PatientState.Idle)
    val patientDataState: StateFlow<PatientState> = _patientDataState.asStateFlow()

    sealed class PatientState {
        object Idle: PatientState()
        object Loading: PatientState()
        data class Success(val patient: PatientDto): PatientState()
        data class Error(val message: String): PatientState()
        object Unauthorized: PatientState()
    }

    init {
        loadUser()
    }

    private fun loadUser(){
        val patientId = JwtManager.getCurrentPatientId()
        if(patientId == null){
            _patientDataState.value = PatientState.Unauthorized
            return
        }
        getPatient(patientId.toLong())
    }

    private fun getPatient(id: Long){
        viewModelScope.launch {
            _patientDataState.value = PatientState.Loading
            try {
                val response: Response<PatientDto> = RetrofitClient.getPatient().getPatient(id)
                if(response.isSuccessful) {
                    response.body()?.let {
                        _patientDataState.value = PatientState.Success(it)
                    } ?: throw Exception("empty body patient")
                } else {
                    throw Exception("HTTP ${response.code()}")
                }
            } catch (e: Exception){
                _patientDataState.value = PatientState.Error("Patient" + (e.message?:"unknown error"))
            }
        }
    }

    fun logOut(){
        JwtManager.clearToken()
        _patientDataState.value = PatientViewModel.PatientState.Unauthorized
    }

}