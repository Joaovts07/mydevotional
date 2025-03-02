package com.example.mydevotional

import android.util.Log
import com.example.mydevotional.ui.theme.Versiculo
import com.google.firebase.firestore.FirebaseFirestore
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.gson.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BibleApiClient {
    val client = HttpClient(Android) {
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend fun buscarVersiculo(livro: String, capitulo: Int, versiculo: Int): Versiculo? {
        return withContext(Dispatchers.IO) {
            try {
                Log.e("URL", "https://bible-api.com/$livro+$capitulo:$versiculo")

                val response: String = client.get {
                    url("https://bible-api.com/$livro+$capitulo:$versiculo?translation=almeida")

                }.bodyAsText()

                gsonDeserializer<Versiculo>(response)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /*suspend fun buscarVersiculoDay(): Versiculo? {
        return withContext(Dispatchers.IO) {
            try {
                val response: String = client.get {
                    //url("https://bible-api.com/$livro+$capitulo:$versiculo?translation=almeida")
                    url("https://bible-api.com/numeros:6-7?translation=almeida")
                }.bodyAsText()

                gsonDeserializer<Versiculo>(response)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }*/

    suspend fun buscarVersiculoDay(date: Date = Date() ): List<Versiculo> {
        val passagens = searchReadingDaily(date) ?: return emptyList()
        val versiculos = mutableListOf<Versiculo>()

        (passagens as List<*>).forEach { passagem ->
            try {
                val response: String = client.get {
                    url("https://bible-api.com/$passagem?translation=almeida")
                }.bodyAsText()

                gsonDeserializer<Versiculo>(response)?.let { versiculos.add(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return versiculos
    }

    suspend fun searchReadingDaily(date: Date): Any? {
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

    fun getDate(date: Date): String {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formato.format(date)
    }

    suspend fun buscarVersiculo(): Versiculo? {
        return withContext(Dispatchers.IO) {
            try {

                val response: String = client.get {
                    url("https://bible-api.com/data/almeida/random")
                }.bodyAsText()

                gsonDeserializer<Versiculo>(response)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
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