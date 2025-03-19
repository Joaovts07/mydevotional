package com.example.mydevotional.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mydevotional.BibleBook
import com.example.mydevotional.ReadVerseWithTTS
import com.example.mydevotional.model.BibleResponse
import com.example.mydevotional.model.Verses

@Composable
fun ChapterCard(versiculo: String, reference: String, ) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(text = versiculo, fontSize = 18.sp)
            Text(
                text = "- $reference ",
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun VerseCard(
    verse: Verses,
    onFavoriteClick: (Verses) -> Unit = {}
) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onFavoriteClick(verse) }) {
                        Icon(
                            imageVector = if (verse.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favoritar verso",
                            tint = if (verse.isFavorite) Color.Red else Color.Gray
                        )
                    }
                    ReadVerseWithTTS(verse.text)
                }
            }
        }
    }
}



@Composable
fun BookListView(books: List<BibleBook>, onBookSelected: (BibleBook) -> Unit) {
    LazyColumn {
        items(books) { book ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onBookSelected(book) },
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    text = book.name,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun ChapterListView(chapters: Int, onChapterSelected: (Int) -> Unit) {
    LazyColumn {
        items(chapters) { index ->
            val chapter = index + 1
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onChapterSelected(chapter) },
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    text = "Capítulo $chapter",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun ChaptersGrid(chapters: Int, onChapterSelected: (Int) -> Unit) {
    val columns = 4
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(chapters) { chapter ->
            val chapterNumber = chapter + 1
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onChapterSelected(chapterNumber) },
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "$chapter", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun DisplayModeContent(
    isSingleCardMode: Boolean,
    onModeChange: (Boolean) -> Unit,
    bibleResponses: List<BibleResponse> = emptyList(),
    verses: List<Verses> = emptyList(),
    onFavoriteClick: (Verses) -> Unit = {}
) {
    Column {
        DisplayModeSelector(
            isSingleCardMode = isSingleCardMode,
            onModeChange = onModeChange
        )

        if (isSingleCardMode) {
            bibleResponses.forEach { bibleResponse ->
                ChapterCard(bibleResponse.text, bibleResponse.reference)
            }
        } else {
            bibleResponses.forEach { bibleResponse ->
                bibleResponse.verses.forEach { verses ->
                    VerseCard(verses) {
                        onFavoriteClick(it)
                    }
                }

            }
        }
    }
}

@Composable
fun DisplayModeSelector(
    isSingleCardMode: Boolean,
    onModeChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = { onModeChange(true) },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Book,
                contentDescription = "Show full chapter",
                tint = if (isSingleCardMode) MaterialTheme.colorScheme.primary else Color.Gray
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        IconButton(
            onClick = { onModeChange(false) },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                contentDescription = "Show verse by verse",
                tint = if (!isSingleCardMode) MaterialTheme.colorScheme.primary else Color.Gray
            )
        }
    }
}
