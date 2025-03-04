package com.example.mydevotional.ui.theme.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mydevotional.BibleBook
import com.example.mydevotional.ui.VersesViewModel

@Composable
fun VersesScreen() {
    val viewModel = remember { VersesViewModel() }
    val books by viewModel.books.collectAsState()
    val chapters by viewModel.chapters.collectAsState()
    val verses by viewModel.verses.collectAsState()
    val selectedBook by viewModel.selectedBook.collectAsState()
    val selectedChapter by viewModel.selectedChapter.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            when {
                selectedBook == null -> {
                    BookListView(books) { book -> viewModel.selectBook(book) }
                }
                selectedChapter == null -> {
                    ChapterListView(chapters) { chapter -> viewModel.selectChapter(chapter) }
                }
                else -> {
                    VerseListView(verses)
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
fun ChapterListView(chapters: List<Int>, onChapterSelected: (Int) -> Unit) {
    LazyColumn {
        items(chapters) { chapter ->
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




