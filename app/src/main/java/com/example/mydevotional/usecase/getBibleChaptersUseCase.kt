package com.example.mydevotional.usecase

import com.example.mydevotional.BibleBook
import com.example.mydevotional.repositorie.BibleRepository
import javax.inject.Inject

class GetBibleChaptersUseCase @Inject constructor(
    private val repository: BibleRepository
) {
    operator fun invoke(bibleBook: BibleBook): Int {
        return repository.getChapters(bibleBook)
    }
}