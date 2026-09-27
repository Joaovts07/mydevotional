package com.example.mydevotional.repositorie

import com.example.mydevotional.BibleBook
import com.example.mydevotional.BibleBooks
import com.example.mydevotional.model.BibleResponse
import com.example.mydevotional.usecase.GetSelectedTranslationUseCase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BibleRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val httpClient: HttpClient,
    private val gson: Gson,
    private val getSelectedTranslationUseCase: GetSelectedTranslationUseCase
) : BibleRepository {

    private val selectedTranslation = getSelectedTranslationUseCase()

    override fun getBibleBooks(): List<BibleBook> {
        return BibleBooks.books
    }

    override fun getChapters(bibleBook: BibleBook): Int {
        return bibleBook.chapters
    }

    override suspend fun getVerses(book: String, chapter: Int): Result<List<BibleResponse>> =
        runCatchingCancellable {
            listOf(fetchPassage("$book-$chapter"))
        }

    override suspend fun getVersesForDay(date: Date): Result<List<BibleResponse>> =
        runCatchingCancellable {
            val passages = fetchPassagesForDay(formatDate(date))
            coroutineScope {
                passages.map { passage -> async { fetchPassage(passage) } }.awaitAll()
            }
        }

    override suspend fun savePassages(passagesByDate: Map<String, List<String>>): Result<Unit> =
        runCatchingCancellable {
            val batch = firestore.batch()
            passagesByDate.forEach { (date, passages) ->
                val document = firestore.collection(READINGS_COLLECTION).document(date)
                batch.set(document, mapOf(PASSAGES_FIELD to passages), SetOptions.merge())
            }
            batch.commit().await()
        }

    private suspend fun fetchPassagesForDay(date: String): List<String> {
        val document = firestore.collection(READINGS_COLLECTION).document(date).get().await()
        val passages = document.get(PASSAGES_FIELD) as? List<*> ?: return emptyList()
        return passages.filterIsInstance<String>()
    }

    private suspend fun fetchPassage(passage: String): BibleResponse {
        val url = "https://bible-api.com/$passage?${selectedTranslation.first().apiCode}"
        val response = httpClient.get { url(url) }
        check(response.status.isSuccess()) { "bible-api returned ${response.status} for $passage" }
        return gson.fromJson(response.bodyAsText(), BibleResponse::class.java)
            ?: error("Empty response for $passage")
    }

    private fun formatDate(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date)
    }

    private inline fun <T> runCatchingCancellable(block: () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private companion object {
        const val READINGS_COLLECTION = "readings"
        const val PASSAGES_FIELD = "passages"
    }
}
