package com.example.mydevotional.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydevotional.usecase.GetCompletedReadingsUseCase
import com.example.mydevotional.usecase.MarkReadingAsCompleteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DailyReadingViewModel @Inject constructor(
    private val markReadingAsCompleteUseCase: MarkReadingAsCompleteUseCase,
    private val getCompletedReadingsUseCase: GetCompletedReadingsUseCase,
) : ViewModel() {

    private val _selectedDate = MutableStateFlow<Date?>(null) // Ou inicialize com Date()
    val selectedDate: StateFlow<Date?> = _selectedDate.asStateFlow()

    private val _allCompletedReadings = getCompletedReadingsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )

    val isReadingCompletedForSelectedDate: StateFlow<Boolean> = combine(
        _selectedDate,
        _allCompletedReadings
    ) { date, completedDates ->
        date?.let {
            val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
            val cleanedFormattedDate = formattedDate.replace("[", "").replace("]", "").replace(" ", "")
            completedDates.contains(cleanedFormattedDate)
        } ?: false
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false // Valor inicial
    )

    fun markReadingAsComplete(date: String) {
        viewModelScope.launch {
            try {
                markReadingAsCompleteUseCase(date)
            } catch (e: Exception) {
                // TODO: Lidar com erros, talvez emitir um Snackbar de erro
                e.printStackTrace()
            }
        }
    }

    fun updateSelectedDate(date: Date) {
        _selectedDate.value = date
    }

    // No seu `init` ou onde você carrega os dados iniciais
    init {
        // Se a data inicial não for TODAY, defina aqui. Exemplo:
        _selectedDate.value = Date() // Define a data inicial como hoje

    }
}