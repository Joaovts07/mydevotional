package com.example.mydevotional.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydevotional.extensions.formatDate
import com.example.mydevotional.usecase.CompleteReadingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DailyReadingViewModel @Inject constructor(
    private val completeReadingsUseCase: CompleteReadingsUseCase
) : ViewModel() {

    private val _selectedDate = MutableStateFlow<Date?>(null)

    private val _passageExpandedStates = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val passageExpandedStates: StateFlow<Map<String, Boolean>> = _passageExpandedStates.asStateFlow()

    init {
        _selectedDate.value = Date()
    }

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
        initialValue = emptySet()
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

    fun togglePassageExpanded(reference: String) {
        _passageExpandedStates.update { currentMap ->
            val currentState = currentMap[reference] ?: true
            currentMap.toMutableMap().apply {
                this[reference] = !currentState
            }
        }
    }

    fun collapseAllPassages() {
        _passageExpandedStates.update { currentMap ->
            val newMap = currentMap.toMutableMap()
            // Define todos os estados existentes como false (colapsado)
            currentMap.keys.forEach { key ->
                newMap[key] = false
            }
            newMap
        }
    }

}