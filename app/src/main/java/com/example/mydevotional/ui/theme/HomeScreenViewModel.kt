package com.example.mydevotional.ui.theme

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydevotional.BibleApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeScreenViewModel : ViewModel() {

    private val _versiculo = MutableStateFlow<Versiculo?>(null)
    val versiculo: StateFlow<Versiculo?> = _versiculo

    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
        viewModelScope.launch {
            _versiculo.value = BibleApiClient.buscarVersiculo("João", 3, 16)
        }
    }
    init {
        loadVersiculo()
    }

    fun loadVersiculo() {
        viewModelScope.launch {
            try {
                _versiculo.value = BibleApiClient.buscarVersiculo()

            } catch (ex: Exception) {
                Log.e("Erro", ex.message.toString())
            }
        }
    }

    fun updateDate(date: LocalDate) {
        _selectedDate.value =  date  }
}