package com.example.mydevotional.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydevotional.extensions.formatDate
import com.example.mydevotional.usecase.CompleteReadingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DailyReadingViewModel @Inject constructor(
    private val completeReadingsUseCase: CompleteReadingsUseCase
) : ViewModel() {

    private val _selectedDate = MutableStateFlow<Date?>(null)
    val selectedDate: StateFlow<Date?> = _selectedDate.asStateFlow()

    private val _completedReadingsDay = MutableStateFlow(false)
    val completedReadingsDay: StateFlow<Boolean> = _completedReadingsDay.asStateFlow()

    // Flow reativo de todas as leituras concluídas
    private val _allCompletedReadings = completeReadingsUseCase.getCompletedReadingsFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )

    fun updateSelectedDate(date: Date) {
        _selectedDate.value = date
    }

    fun toggleReadingComplete(date: Date?) {
        viewModelScope.launch {
            val formattedDate = date?.formatDate("yyyy-MM-dd") ?: Date().formatDate("yyyy-MM-dd")
            completeReadingsUseCase.toggleCompletion(formattedDate)
        }

    }


    // O StateFlow que observa a lista completa de leituras do UseCase
    private val _completedReadingsCalendar = completeReadingsUseCase.getCompletedReadingsFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet() // Usamos um Set<String> aqui, que é mais eficiente para o 'contains'
    )

    // Estado reativo dos dias lidos (para o calendário)
    val completedDays: StateFlow<Set<String>> = completeReadingsUseCase.getCompletedReadingsFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )

    // ESTA É A NOVA VARIÁVEL DE ESTADO REATIVA QUE O BOTÃO IRÁ OBSERVAR
    // Ela combina a data selecionada e a lista de leituras, e sempre tem o valor correto.
    val isReadingCompletedForSelectedDate: StateFlow<Boolean> = combine(
        _selectedDate,
        _completedReadingsCalendar
    ) { date, completedDates ->
        date?.let {
            // Formata a data e verifica se ela está no Set de leituras concluídas
            val formattedDate = it.formatDate("yyyy-MM-dd")
            val isRead = completedDates.contains(formattedDate)
            isRead
        } ?: false // Se a data for nula, o status é false
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    init {
        _selectedDate.value = Date()
    }
}