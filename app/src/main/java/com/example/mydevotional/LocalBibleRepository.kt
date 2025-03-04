package com.example.mydevotional

class LocalBibleRepository : BibleRepository {
    override fun getBibleBooks(): List<BibleBook> {
        return BibleBooks.books
    }
}