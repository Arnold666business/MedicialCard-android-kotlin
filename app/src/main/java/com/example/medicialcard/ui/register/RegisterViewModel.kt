package com.example.medicialcard.ui.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicialcard.api.RetrofitClient
import com.example.medicialcard.data.JwtResponse
import com.example.medicialcard.data.LoginRequest
import com.example.medicialcard.data.RegisterRequest
import com.example.medicialcard.ui.login.LoginViewModel.LoginState
import com.example.medicialcard.utils.JwtManager
import com.example.medicialcard.utils.RegisterValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    private var _resgisterState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _resgisterState.asStateFlow()

    sealed class RegisterState {
        object Idle: RegisterState()
        object Loading: RegisterState()
        object Success: RegisterState()
        data class Error(val message: String): RegisterState()
    }

    fun reg(registerInfo: RegisterRequest){
        viewModelScope.launch {
            _resgisterState.value = RegisterState.Loading
            try {
                val validateErrorList: List<String> = RegisterValidator.validate(registerInfo)
                if(!validateErrorList.isEmpty()){
                    _resgisterState.value = RegisterState.Error(validateErrorList.get(0))
                } else{
                    val response: Response<JwtResponse> = RetrofitClient.getAuth().register(registerInfo)

                    if(response.isSuccessful){
                        response.body()?.let { JwtManager.saveToken(it.jwt)
                            JwtManager.savePatientId(it.patientId)
                            _resgisterState.value = RegisterState.Success} ?: throw Exception("Register Empty response")
                    } else{
                        if(response.code()  == 409      ){
                            throw Exception("Номер телефона и логин должны быть уникальны")
                        } else{
                            throw Exception(response.code().toString())
                        }
                    }
                }
            } catch (e: Exception){
                _resgisterState.value = RegisterState.Error(e.message ?: "Unknown error with registerr")
            }
        }
    }



}