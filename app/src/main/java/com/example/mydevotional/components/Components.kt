package com.example.mydevotional.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mydevotional.model.BibleResponse
import com.example.mydevotional.model.Verses

fun LazyListScope.versesListItems(
    bibleResponses: List<BibleResponse>,
    onFavoriteClick: (Verses) -> Unit
) {
    items(bibleResponses) { bibleResponse ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Título da passagem (e.g., "Salmos 65")
                Text(
                    text = bibleResponse.reference,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Separador
                Divider()

                // Exibir cada versículo individualmente dentro da passagem
                bibleResponse.verses.forEach { verse ->
                    VerseCard(
                        verse = verse,
                        onFavoriteClick = onFavoriteClick
                    )
                }
            }
        }
    }
}

