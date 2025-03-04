package com.example.mydevotional.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydevotional.BibleApiClient
import com.example.mydevotional.GetBibleContentUseCase
import com.example.mydevotional.ui.theme.Verse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getBibleContentUseCase: GetBibleContentUseCase
) : ViewModel() {

    private val _verses = MutableStateFlow<List<Verse>>(emptyList())
    val verses: StateFlow<List<Verse>> = _verses

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

    private fun loadVersiculo() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                //_verses.value = BibleApiClient.buscarVersiculoDay()
                _verses.value = getBibleContentUseCase.getVerseForDate(date = Date())
                _isLoading.value = false

            } catch (ex: Exception) {
                Log.e("Erro", ex.message.toString())
            }
        }
    }

    private fun buscarVersiculosPorData(data: Date) {
        viewModelScope.launch {
            _isLoading.value = true
            //val leituras = repository.buscarLeiturasPorData(data)
            _verses.value = BibleApiClient.buscarVersiculoDay(data)
            _isLoading.value = false
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