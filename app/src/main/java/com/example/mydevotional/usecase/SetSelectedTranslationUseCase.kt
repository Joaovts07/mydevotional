package com.example.mydevotional.usecase

import com.example.mydevotional.model.BibleTranslation
import com.example.mydevotional.repositorie.TranslationPreferenceRepository
import javax.inject.Inject

class SetSelectedTranslationUseCase @Inject constructor(
    private val repository: TranslationPreferenceRepository
) {
    suspend operator fun invoke(translation: BibleTranslation) {
        repository.setTranslationPreference(translation)
    }
}