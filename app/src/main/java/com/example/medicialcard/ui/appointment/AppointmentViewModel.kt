package com.example.medicialcard.ui.appointment

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicialcard.api.RetrofitClient
import com.example.medicialcard.data.AppointmentDto
import com.example.medicialcard.utils.JwtManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppointmentViewModel : ViewModel() {
    private var _appointmentState = MutableStateFlow<AppointmentState>(AppointmentState.Idle)
    val appointmentState: StateFlow<AppointmentState> = _appointmentState.asStateFlow()

    private var _patientName = MutableStateFlow<String>("имя пациэнта")
    val patientName: StateFlow<String> = _patientName.asStateFlow()
    private val _doctorsCache = mutableStateMapOf<Long, String>()

    sealed class AppointmentState {
        object Idle: AppointmentState()
        object Loading: AppointmentState()
        data class Success(var appointments: List<AppointmentDto>?): AppointmentState()
        data class Error(val message: String): AppointmentState()
        object Unauthorized: AppointmentState()
    }

    init {
        loadPatientData()
        loadAppointments()
    }

    private fun loadPatientData() {
        val patientId = JwtManager.getCurrentPatientId()?.toLongOrNull()//плчкемуу!????!
        viewModelScope.launch {
            try {
                val response = RetrofitClient.getPatient().getPatient(patientId)
                if (response.isSuccessful) {
                    _patientName.value = response.body()?.firstName ?: "Неизвестный пациент"
                }
            } catch (e: Exception) {
                _appointmentState.value = AppointmentState.Error("appointmnt" + (e.message?:"unknown error"))
            }
        }
    }

    private fun loadAppointments() {
        val patientId = JwtManager.getCurrentPatientId()?.toLongOrNull()

        viewModelScope.launch {
            _appointmentState.value = AppointmentState.Loading
            try {
                val response = RetrofitClient.getAppointment().getAllAppointment(patientId)
                if(response.isSuccessful){
                    response.body()?.forEach({appointment ->
                        if(!_doctorsCache.containsKey(appointment.doctorId)){
                            loadDoctorName(appointment.doctorId)
                        }
                    })
                    _appointmentState.value = AppointmentState.Success(response.body())
                } else{
                    throw Exception("HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                _appointmentState.value = AppointmentState.Error(e.message ?: "appointment Неизвестная ошибка")
            }
        }
    }

    private fun loadDoctorName(doctorId: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.getDoctor().getDoctor(doctorId)
                if (response.isSuccessful) {
                    response.body()?.let { doctor ->
                        _doctorsCache[doctorId] = "${doctor.doctor.firstName} ${doctor.doctor.lastName}"
                    }
                }
            } catch (e: Exception) {
                _appointmentState.value = AppointmentState.Error(e.message ?: "try to get doctor by id in appointment Неизвестная ошибка")
            }
        }
    }

    fun getDoctorName(doctorId: Long): String = _doctorsCache[doctorId] ?: "Врач"
}

