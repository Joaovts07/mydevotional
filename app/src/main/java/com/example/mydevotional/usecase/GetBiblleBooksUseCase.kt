package com.example.mydevotional.usecase

import com.example.mydevotional.BibleBook
import com.example.mydevotional.repositorie.BibleRepository
import javax.inject.Inject

class GetBiblleBooksUseCase @Inject constructor(
    private val repository: BibleRepository
) {
    suspend operator fun invoke(): List<BibleBook> {
        return repository.getBibleBooks()
    }
}