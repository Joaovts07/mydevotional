package com.example.mydevotional.usecase

import com.example.login.data.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CalculateReadingPercentageUseCase @Inject constructor(
    private val getCompletedReadingsUseCase: GetCompletedReadingsUseCase,
    private val authRepository: AuthRepository // Para saber qual usuário buscar os dados
) {
    // Defina o número total de dias no seu plano de leitura
    // TODO: Ajuste este valor de acordo com o seu plano de leitura diária
    private val TOTAL_READING_DAYS = 365 // Exemplo: 365 dias para um ano completo

    operator fun invoke(): Flow<Float> {
        return authRepository.getCurrentUserId().combine(getCompletedReadingsUseCase()) { userId, completedDates ->
            if (userId == null) {
                // Se não houver userId (usuário não logado), a porcentagem é 0.
                0f
            } else {
                // No futuro, getCompletedReadingsUseCase() pode precisar ser adaptado
                // para buscar dados específicos do userId se a sincronização não for transparente.
                // Por agora, assumimos que 'completedDates' já reflete o estado do usuário logado.
                if (TOTAL_READING_DAYS == 0) 0f else completedDates.size.toFloat() / TOTAL_READING_DAYS.toFloat()
            }
        }
    }
}