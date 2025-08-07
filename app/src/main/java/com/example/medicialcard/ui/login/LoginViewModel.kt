package com.example.medicialcard.ui.login


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicialcard.api.RetrofitClient
import com.example.medicialcard.data.JwtResponse
import com.example.medicialcard.data.LoginRequest
import com.example.medicialcard.utils.JwtManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel : ViewModel() {
    private val authRepository = RetrofitClient.getAuth()
    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state: StateFlow<LoginState> = _state.asStateFlow()

    sealed class LoginState {
        object Idle: LoginState()
        object Loading: LoginState()
        object Success: LoginState()
        data class Error(val message: String): LoginState()
    }


    fun login(login: String, password: String){
        viewModelScope.launch {
            _state.value = LoginState.Loading
            try {
                if(login.isBlank() || password.isBlank()){
                    _state.value = LoginState.Error("Заполните все поля")
                } else{
                    val response: Response<JwtResponse> = authRepository.login(LoginRequest(login, password))
                    if(response.isSuccessful){
                        response.body()?.let { JwtManager.saveToken(it.jwt)
                            JwtManager.savePatientId(it.patientId)
                            _state.value = LoginState.Success} ?: throw Exception("Empty response")
                    } else{
                        throw Exception("HTTP ${response.code()}")
                    }
                }
            } catch (e: Exception){
                _state.value = LoginState.Error(e.message ?: "Unknown error")
            }
        }
    }

}

