package com.example.mydevotional.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.AuthRepository
import com.example.mydevotional.model.BibleTranslation
import com.example.mydevotional.usecase.CalculateReadingPercentageUseCase
import com.example.mydevotional.usecase.GetSelectedTranslationUseCase
import com.example.mydevotional.usecase.SetSelectedTranslationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val calculateReadingPercentageUseCase: CalculateReadingPercentageUseCase,
    private val setSelectedTranslationUseCase: SetSelectedTranslationUseCase,
    private val getSelectedTranslationUseCase: GetSelectedTranslationUseCase,

    ) : ViewModel() {

    private val _selectedTranslation = MutableStateFlow(BibleTranslation.WEB)
    val selectedTranslation: StateFlow<BibleTranslation> = _selectedTranslation.asStateFlow()

    fun setTranslation(translation: BibleTranslation) {
        viewModelScope.launch {
            setSelectedTranslationUseCase(translation)
            // observeSelectedTranslation() vai reagir e recarregar
        }
    }

    /*private fun observeSelectedTranslation() {
        viewModelScope.launch {
            getSelectedTranslationUseCase().collect { translation ->
                _selectedTranslation.value = translation
                // Recarrega os versículos com a nova tradução
                _selectedDate.value?.let { date ->
                    _isLoading.value = true
                    _bibleResponses.value = getVersesForDayUseCase(date)
                    _isLoading.value = false
                } ?: run {
                    // Se não houver data selecionada, recarrega o dia atual
                    loadVersesForToday()
                }
            }
        }
    }*/

    // Informações do Usuário (Flows para observação na UI)
    /*val userName: StateFlow<String> = authRepository.getCurrentUserName()
        .map { it ?: "Convidado" } // Default para "Convidado" se nome nulo
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Inicia a coleta enquanto a UI está ativa
            initialValue = "Carregando..." // Valor inicial antes de carregar o nome
        )

    val userEmail: StateFlow<String> = authRepository.getCurrentUserEmail()
        .map { it ?: "N/A" } // Default para "N/A" se email nulo
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Inicia a coleta enquanto a UI está ativa
            initialValue = "Carregando..." // Valor inicial antes de carregar o email
        )

    // Porcentagem de Leitura
    // O UseCase já retorna um Flow<Float>
    val readingPercentage: StateFlow<Float> = calculateReadingPercentageUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Inicia a coleta enquanto a UI está ativa
            initialValue = 0f // Valor inicial da porcentagem
        )*/

    // Função para logout (exemplo, você precisará implementar a lógica no AuthRepository)
    fun logout() {
        viewModelScope.launch {
            // TODO: Implementar a lógica de logout no AuthRepository
            // Exemplo: authRepository.logout()
        }
    }
}