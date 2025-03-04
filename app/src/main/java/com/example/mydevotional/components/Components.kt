package com.example.mydevotional.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mydevotional.ReadVerseWithTTS
import com.example.mydevotional.ui.theme.Verses
import com.example.mydevotional.ui.theme.Verse


@Composable
fun ChapterCard(versiculo: Verse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(text = versiculo.text, fontSize = 18.sp)
            Text(
                text = "- ${versiculo.verses.first().book_name} ${versiculo.verses.first().chapter}",
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun VerseCard(verse: Verses) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(text = verse.text, fontSize = 18.sp)
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "- ${verse.book_name} ${verse.chapter}:${verse.verse}",
                    fontStyle = FontStyle.Italic
                )
                ReadVerseWithTTS(verse.text)
            }
        }
    }
}