package com.example.mydevotional.repositorie

import com.example.mydevotional.BibleApiClient.client
import com.example.mydevotional.BibleApiClient.getDate
import com.example.mydevotional.BibleBook
import com.example.mydevotional.BibleBooks
import com.example.mydevotional.ui.theme.Verse
import com.google.firebase.firestore.FirebaseFirestore
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class BibleRepositoryImpl @Inject constructor(
): BibleRepository {
    override fun getBibleBooks(): List<BibleBook> {
        return BibleBooks.books
    }

    override fun getChapters(bibleBook: BibleBook): Int {
        TODO("Not yet implemented")
    }

    override fun getVerses(book: BibleBook, chapter: Int): List<Verse> {
        TODO("Not yet implemented")
    }

    override suspend fun getVersesForDay(date: Date): List<Verse> {
        val passagens = searchReadingDaily(date) ?: return emptyList()
        val versiculos = mutableListOf<Verse>()

        (passagens as List<*>).forEach { passagem ->
            try {
                val response: String = client.get {
                    url("https://bible-api.com/$passagem?translation=almeida")
                }.bodyAsText()

                gsonDeserializer<Verse>(response)?.let { versiculos.add(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return versiculos
    }

    override suspend fun searchReadingDaily(date: Date): Any? {
        val db = FirebaseFirestore.getInstance()
        val dateFormated = getDate(date)
        return try {
            val document = db.collection("readings").document(dateFormated).get().await()
            if (document.exists()) {
                document.get("passages")
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    private inline fun <reified T> gsonDeserializer(json: String): T? {
        return try {
            com.google.gson.Gson().fromJson(json, T::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}