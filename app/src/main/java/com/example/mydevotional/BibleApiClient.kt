package com.example.mydevotional

import android.util.Log
import com.example.mydevotional.ui.theme.Versiculo
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.gson.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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