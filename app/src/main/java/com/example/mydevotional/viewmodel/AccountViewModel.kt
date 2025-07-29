package com.example.mydevotional.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydevotional.model.BibleTranslation
import com.example.mydevotional.usecase.GetSelectedTranslationUseCase
import com.example.mydevotional.usecase.SetSelectedTranslationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val setSelectedTranslationUseCase: SetSelectedTranslationUseCase,
    private val getSelectedTranslationUseCase: GetSelectedTranslationUseCase,
    ) : ViewModel() {

    val selectedTranslation: StateFlow<BibleTranslation> = getSelectedTranslationUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BibleTranslation.ALMEIDA
        )


    fun setTranslation(translation: BibleTranslation) {
        viewModelScope.launch {
            setSelectedTranslationUseCase(translation)
            }
    }
}