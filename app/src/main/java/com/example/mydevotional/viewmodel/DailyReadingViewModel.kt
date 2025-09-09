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

    private val _completedReadingsCalendar = MutableStateFlow<List<String>>(emptyList())
    val completedReadingsCalendar: StateFlow<List<String>> = _completedReadingsCalendar.asStateFlow()

    // Flow reativo de todas as leituras concluídas
    private val _allCompletedReadings = completeReadingsUseCase.getCompletedReadingsFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )

    // Estado reativo que diz se o dia selecionado está completo
    val isReadingCompletedForSelectedDate: StateFlow<Boolean> = combine(
        _selectedDate,
        _allCompletedReadings
    ) { date, completedDates ->
        date?.let {
            val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
            completedDates.contains(formattedDate)
        } ?: false
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun updateSelectedDate(date: Date) {
        _selectedDate.value = date
    }

    fun toggleReadingComplete(date: Date?) {
        viewModelScope.launch {
            val formattedDate = date?.formatDate("yyyy-MM-dd") ?: ""
            completeReadingsUseCase.toggleCompletion(formattedDate)
        }

    }

    fun verifyDailyIsReading() : Boolean {
        viewModelScope.launch {
            completeReadingsUseCase.getCompletedReadingsFlow().collect { reading ->
                _completedReadingsCalendar.value = reading.toList()
            }
        }
        return _completedReadingsCalendar.value.contains(selectedDate.value?.formatDate("yyy-MM-dd"))
    }

    init {
        _selectedDate.value = Date()
        verifyDailyIsReading()
    }
}