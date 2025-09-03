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

    fun toggleReadingComplete() {
        _selectedDate.value?.let { date ->
            viewModelScope.launch {
                val formattedDate = date.formatDate("yyyy-MM-dd")
                completeReadingsUseCase.toggleCompletion(formattedDate)
            }
        }
    }

    init {
        _selectedDate.value = Date()
    }
}