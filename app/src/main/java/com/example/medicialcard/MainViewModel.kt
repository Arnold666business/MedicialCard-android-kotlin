package com.example.medicialcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicialcard.api.RetrofitClient
import com.example.medicialcard.data.JwtRequest
import com.example.medicialcard.utils.JwtManager
import com.example.medicialcard.utils.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private var _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    init{
        checkAuth()
    }

    private fun checkAuth() {
        viewModelScope.launch{
            val token: String? = JwtManager.getToken()
            print(token.toString())
            val isValid: Boolean = token?.let {
                RetrofitClient.getAuth().jwtIsValid(JwtRequest(it)).body()
            } ?: false
            _startDestination.value = if (isValid) Routes.PATIENT else Routes.START
        }
    }




}