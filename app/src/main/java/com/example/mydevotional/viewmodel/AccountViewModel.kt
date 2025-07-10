package com.example.mydevotional.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.data.repository.AuthRepository
import com.example.mydevotional.usecase.CalculateReadingPercentageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val calculateReadingPercentageUseCase: CalculateReadingPercentageUseCase
) : ViewModel() {

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