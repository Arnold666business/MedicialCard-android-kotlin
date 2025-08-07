package com.example.medicialcard.ui.doctorsInCategory

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicialcard.api.RetrofitClient
import com.example.medicialcard.data.CategoryDto
import com.example.medicialcard.data.DoctorDto
import com.example.medicialcard.data.PatientDto
import com.example.medicialcard.ui.patient.PatientViewModel.PatientState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class DoctorsListViewModel(private val categoryId: Long) : ViewModel() {
    private var _doctorsListState = MutableStateFlow<DoctorListState>(DoctorListState.Idle)
    val doctorsListState: StateFlow<DoctorListState> = _doctorsListState.asStateFlow()

    private var _categoryTitle = MutableStateFlow<String>("")
    val categoryTitle: StateFlow<String> = _categoryTitle.asStateFlow()


    private var filterRating = false;

    sealed class DoctorListState {
        object Idle: DoctorListState()
        object Loading: DoctorListState()
        data class Success(var doctorsList: List<DoctorDto>, var isActiveFilter: Boolean): DoctorListState()
        data class Error(val message: String): DoctorListState()
    }

    init {
        loadCategoryTitle()
        loadDoctors()
    }

    fun toggleRatingFilter(){
        filterRating = !filterRating
        loadDoctors()
    }

    private fun loadDoctors(){
        viewModelScope.launch {
            _doctorsListState.value = DoctorListState.Loading

            try {
                val response: Response<List<DoctorDto>> = if(filterRating){
                    RetrofitClient.getDoctor().getSortedByRatingDoctors(categoryId)
                } else{
                    RetrofitClient.getDoctor().getDoctors(categoryId)
                }


                if(response.isSuccessful){
                    response.body()?.let {


                        Log.d("sd", "lf ,бда бояь я тутут!")
                        _doctorsListState.value = DoctorListState.Success(it, filterRating)
                    }  ?:
                        throw Exception("empty body doctorsList")
                } else{
                    throw Exception("HTTP ${response.code()}")
                }
            } catch (e: Exception){
                _doctorsListState.value = DoctorListState.Error("DoctorsList" + (e.message?:"unknown error"))
            }
        }
    }

    private fun loadCategoryTitle(){
        viewModelScope.launch {
            _categoryTitle.value =  RetrofitClient.getDoctor()
                .getAllCategories()
                .body()
                ?.firstOrNull { it.categoryId == categoryId }
                ?.title ?: "Не найдено"
        }
    }

    class Factory(private val categoryId: Long) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DoctorsListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DoctorsListViewModel(categoryId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}