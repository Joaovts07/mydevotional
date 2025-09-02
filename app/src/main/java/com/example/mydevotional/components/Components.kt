package com.example.mydevotional.components


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mydevotional.model.BibleResponse
import com.example.mydevotional.model.Verses

fun LazyListScope. versesListItems(
    bibleResponses: List<BibleResponse>,
    isSingleCardMode: Boolean,
    onFavoriteClick: (Verses) -> Unit
) {
    if (isSingleCardMode) {
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
                    Text(
                        text = bibleResponse.text,
                        fontSize = 18.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "- ${bibleResponse.reference} ",
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    } else {
        items(bibleResponses.flatMap { it.verses }) { verse ->
            VerseCard(
                verse = verse,
                onFavoriteClick = onFavoriteClick
            )
        }
    }
}

