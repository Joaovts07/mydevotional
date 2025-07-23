package com.example.mydevotional.model

import com.example.mydevotional.BibleBook

fun BibleBook.getLocalizedName(translation: BibleTranslation): String {
    return when (translation) {
        BibleTranslation.ALMEIDA -> this.name
        BibleTranslation.WEB -> this.englishName
    }
}

fun BibleBook.getApiName(): String {
    return this.abbreviation
}