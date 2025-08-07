package com.example.medicialcard.ui.category

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicialcard.api.RetrofitClient
import com.example.medicialcard.data.CategoryDto
import com.example.medicialcard.data.PatientDto
import com.example.medicialcard.ui.patient.PatientViewModel.PatientState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class CategoryModelView : ViewModel() {
    private var _categoriesState = MutableStateFlow<CategoriesState>(CategoriesState.Idle)
    val categoriesState: StateFlow<CategoriesState> = _categoriesState.asStateFlow()

    sealed class CategoriesState{
        object Idle: CategoriesState()
        object Loading: CategoriesState()
        data class Success(val categories: List<CategoryDto>): CategoriesState()
        data class Error(val message: String): CategoriesState()
        object Unauthorized: CategoriesState()
    }

    init {
        loadCategories()
    }

    private fun loadCategories(){
        viewModelScope.launch {
            _categoriesState.value = CategoriesState.Loading
            try {
                val response: Response<List<CategoryDto>> =
                    RetrofitClient.getDoctor().getAllCategories()
                if (response.isSuccessful) {
                    response.body()?.let {
                        _categoriesState.value = CategoriesState.Success(it)
                    } ?: throw Exception("empty body categories")
                } else {
                    throw Exception("HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                _categoriesState.value =
                    CategoriesState.Error("Category" + (e.message ?: "unknown error"))
            }
        }
    }

}