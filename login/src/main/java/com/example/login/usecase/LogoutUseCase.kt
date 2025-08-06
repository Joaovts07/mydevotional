package com.example.login.usecase

import com.example.login.data.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke() {
        return authRepository.logout()
    }
}