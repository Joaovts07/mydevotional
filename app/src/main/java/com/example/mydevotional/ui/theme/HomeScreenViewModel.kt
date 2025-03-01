package com.example.mydevotional.ui.theme

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydevotional.BibleApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date


class HomeScreenViewModel : ViewModel() {

    private val _verses = MutableStateFlow<List<Versiculo>>(emptyList())
    val verses: StateFlow<List<Versiculo>> = _verses

    private val _selectedDate = MutableStateFlow<Date?>(null)
    val selectedDate: StateFlow<Date?> = _selectedDate

    private val _completedReadings = MutableStateFlow<List<String>>(emptyList()) // Dias já lidos
    val completedReadings: StateFlow<List<String>> = _completedReadings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadVersiculo()
        carregarLeiturasRealizadas()
        _selectedDate.value?.let { buscarVersiculosPorData(it) }
    }

    fun loadVersiculo() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _verses.value = BibleApiClient.buscarVersiculoDay()
                _isLoading.value = false

            } catch (ex: Exception) {
                Log.e("Erro", ex.message.toString())
            }
        }
    }

    private fun buscarVersiculosPorData(data: Date) {
        viewModelScope.launch {
            //val leituras = repository.buscarLeiturasPorData(data)
            _verses.value = BibleApiClient.buscarVersiculoDay(data)
        }
    }

    private fun carregarLeiturasRealizadas() {
        viewModelScope.launch {
            //val datasLidas = repository.buscarDatasLidas()
            val datasLidas = null
            _completedReadings.value = datasLidas ?: emptyList()
        }
    }

    fun selectDate(novaData: Date) {
        _selectedDate.value = novaData
        buscarVersiculosPorData(novaData)
    }
}