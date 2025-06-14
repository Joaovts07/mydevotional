package com.example.mydevotional.repositorie

import android.util.Log
import com.example.mydevotional.BibleBook
import com.example.mydevotional.BibleBooks
import com.example.mydevotional.extensions.formatDate
import com.example.mydevotional.model.BibleResponse
import com.google.firebase.firestore.FirebaseFirestore
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
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

    override suspend fun getVerses(book: String, chapter: Int): List<BibleResponse> {
        val bibleResponse = mutableListOf<BibleResponse>()
        try {
            val response: String = httpClient.get {
                url("https://bible-api.com/$book-$chapter?translation=almeida")
            }.bodyAsText()
            Log.e("bible-url", "https://bible-api.com/$book-$chapter?translation=almeida")
            gsonDeserializer<BibleResponse>(response)?.let { bibleResponse.add(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return bibleResponse
    }

    override suspend fun getVersesForDay(date: Date): List<BibleResponse> {
        val dateFormated = getDate(date)
        val passages = searchReadingDaily(dateFormated) as? List<String> ?: return emptyList()

        return coroutineScope {
            val deferredResponses = passages.map { passage ->
                async {
                    try {
                        val response: String = httpClient.get {
                            url("https://bible-api.com/$passage?translation=almeida")
                        }.bodyAsText()
                        gsonDeserializer<BibleResponse>(response)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }
            deferredResponses.awaitAll().filterNotNull()
        }
    }

    override suspend fun searchReadingDaily(date: String): Any? {
        return try {
            val document = firestore.collection("readings").document(date).get().await()
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

    private suspend fun fetchVersesFromApi(passages: List<String>): List<BibleResponse> {
        val bibleResponses = mutableListOf<BibleResponse>()
        passages.forEach { passage ->
            try {
                val response: String = httpClient.get {
                    url("https://bible-api.com/$passage?translation=almeida")
                }.bodyAsText()
                Log.e("bible-url", "https://bible-api.com/$passage?translation=almeida")
                val bibleResponse = gsonDeserializer<BibleResponse>(response)

                bibleResponse?.let {
                    bibleResponses.add(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
        return bibleResponses
    }

    fun getDate(date: Date): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(date)
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
