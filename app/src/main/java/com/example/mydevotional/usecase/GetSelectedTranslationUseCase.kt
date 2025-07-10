package com.example.mydevotional.usecase

import com.example.mydevotional.model.BibleTranslation
import com.example.mydevotional.repositorie.TranslationPreferenceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSelectedTranslationUseCase @Inject constructor(
    private val repository: TranslationPreferenceRepository
) {
    operator fun invoke(): Flow<BibleTranslation> {
        return repository.getTranslationPreference()
    }
}