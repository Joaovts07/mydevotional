package com.example.mydevotional.repositorie

import com.example.mydevotional.BibleBook
import com.example.mydevotional.BibleBooks
import com.example.mydevotional.model.BibleResponse
import com.example.mydevotional.model.Verses
import com.google.firebase.firestore.FirebaseFirestore
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class BibleRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val httpClient: HttpClient
) : BibleRepository {

    override fun getBibleBooks(): List<BibleBook> {
        return BibleBooks.books
    }

    override fun getChapters(bibleBook: BibleBook): Int {
        return bibleBook.chapters
    }

    override suspend fun getVerses(book: String, chapter: Int): List<Verses> {
        val verses = mutableListOf<Verses>()
        try {
            val response: String = httpClient.get {
                url("https://bible-api.com/$book-$chapter?translation=almeida")
            }.bodyAsText()

            gsonDeserializer<Verses>(response)?.let { verses.add(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return verses
    }

    override suspend fun getVersesForDay(date: Date): List<Verses> {
        val passages = searchReadingDaily(date) ?: return emptyList()
        return fetchVersesFromApi(passages)
    }

    override suspend fun searchReadingDaily(date: Date): List<String>? {
        return try {
            val document = firestore.collection("readings")
                .document(formatDate(date))
                .get().await()

            if (document.exists()) {
                document.get("passages") as? List<String>
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun fetchVersesFromApi(passages: List<String>): List<Verses> {
        val verses = ArrayList<Verses>()
        passages.forEach { passage ->
            try {
                val response: String = httpClient.get {
                    url("https://bible-api.com/$passage?translation=almeida")
                }.bodyAsText()

                val bibleResponse = gsonDeserializer<BibleResponse>(response)

                bibleResponse?.verses?.let { verses.addAll(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return verses
    }

    private fun formatDate(date: Date): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(date)
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
