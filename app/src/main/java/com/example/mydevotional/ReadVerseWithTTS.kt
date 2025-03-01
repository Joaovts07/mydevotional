package com.example.mydevotional

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

import java.util.Locale

@Composable
fun ReadVerseWithTTS(versiculo: String) {
    val context = LocalContext.current
    var textToSpeech: TextToSpeech? = null

    IconButton(onClick = {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale("pt", "BR")
                textToSpeech?.speak(versiculo, TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                Log.e("TTS", "Erro ao inicializar o TextToSpeech")
            }
        }
    }) {
        Icon(imageVector = Icons.Filled.Mic, contentDescription = "Ler versículo")
    }

    DisposableEffect(Unit) {
        onDispose {
            textToSpeech?.shutdown()
        }
    }
}