package com.example.medicialcard.ui.doctor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.medicialcard.api.RetrofitClient
import com.example.medicialcard.data.DoctorProfile
import com.example.medicialcard.data.ReviewCanRequest
import com.example.medicialcard.utils.JwtManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception

class DoctorViewModel(private val doctorId: Long) : ViewModel() {
    var _doctorState = MutableStateFlow<DoctorState>(DoctorState.Idle)
    val doctorState: StateFlow<DoctorState> = _doctorState.asStateFlow()

    var _notificationMessage = MutableStateFlow<String?>(null)
    val notificationMessage: StateFlow<String?> = _notificationMessage.asStateFlow()

    sealed class DoctorState {
        object Idle: DoctorState()
        object Loading: DoctorState()
        data class Success(var doctor: DoctorProfile): DoctorState()
        data class Error(val message: String): DoctorState()
        object Unauthorized: DoctorState()
    }

    init {
        loadDoctorInfo()
    }


    private fun loadDoctorInfo(){
        viewModelScope.launch {
            _doctorState.value = DoctorState.Loading

            try {
                val response: Response<DoctorProfile> = RetrofitClient.getDoctor().getDoctor(doctorId)
                if(response.isSuccessful){
                    response.body()?.let {
                        _doctorState.value = DoctorState.Success(it)
                    }  ?:
                    throw Exception("empty body doctor profile")
                } else{
                    throw Exception("HTTP ${response.code()}")
                }
            } catch (e: Exception){
                _doctorState.value = DoctorState.Error("Doctors profile" + (e.message?:"unknown error"))
            }
        }
    }



    fun tryTySendReview(navController: NavController){
        viewModelScope.launch {
            val patientId: String? = JwtManager.getCurrentPatientId()
            try {
                val response: Response<Boolean> =
                    RetrofitClient.getAppointment().checkToPossabilityToSendReview(
                        ReviewCanRequest(patientId?.toLong() ?: 0L, doctorId)
                    )
                if(response.isSuccessful){
                   if(response.body()==true){
                       navController.navigate("reviews/creating/${doctorId}")
                   } else {
                        _notificationMessage.value = "Вы не можете оставить отзыв"
                   }

                } else {
                    throw Exception("HTTP ${response.code()}")
                }
            } catch (e: Exception){
                _doctorState.value = DoctorState.Error("Doctor" + (e.message?:"unknown error"))
            }
        }
    }

    fun clearNotification(){
        _notificationMessage.value = null
    }

    fun sendNotificationToRegistryAppointment(){
        viewModelScope.launch {
            val patientId: String? = JwtManager.getCurrentPatientId()
            try {
                val response: Response<Unit> = RetrofitClient.getAppointment().sendNotificationToCreateAppointment(patientId, doctorId)
                if(response.isSuccessful){
                    _notificationMessage.value = "Сообщение отправлено в регистратуру, ожидайте звонка"
                } else{
                    _notificationMessage.value = "Ошибка отправки сообщения в регестратуру"
                }
            }catch (e: Exception){
                _doctorState.value = DoctorState.Error("Doctor" + (e.message?:"unknown error"))
            }
        }
    }

    class Factory(private val doctorId: Long) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DoctorViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DoctorViewModel(doctorId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}