package com.example.mydevotional.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydevotional.extensions.formatDate
import com.example.mydevotional.usecase.CompleteReadingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DailyReadingViewModel @Inject constructor(
    private val completeReadingsUseCase: CompleteReadingsUseCase
) : ViewModel() {

    private val _selectedDate = MutableStateFlow<Date?>(null)

    fun updateSelectedDate(date: Date) {
        _selectedDate.value = date
    }

    fun toggleReadingComplete(date: Date?) {
        viewModelScope.launch {
            val formattedDate = date?.formatDate("yyyy-MM-dd") ?: Date().formatDate("yyyy-MM-dd")
            completeReadingsUseCase.toggleCompletion(formattedDate)
        }

    }

    private val _completedReadingsCalendar = completeReadingsUseCase.getCompletedReadingsFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet() // Usamos um Set<String> aqui, que é mais eficiente para o 'contains'
    )

    val completedDays: StateFlow<Set<String>> = completeReadingsUseCase.getCompletedReadingsFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )

    val isReadingCompletedForSelectedDate: StateFlow<Boolean> = combine(
        _selectedDate,
        _completedReadingsCalendar
    ) { date, completedDates ->
        date?.let {
            val formattedDate = it.formatDate("yyyy-MM-dd")
            val isRead = completedDates.contains(formattedDate)
            isRead
        } ?: false
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    init {
        _selectedDate.value = Date()
    }
}