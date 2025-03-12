package com.example.mydevotional.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mydevotional.viewmodel.VersesViewModel

@Composable
fun VersesScreen(bookName: String, chapter: Int, viewModel: VersesViewModel) {
    val verses by viewModel.verses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var isSingleCardMode by remember { mutableStateOf(true) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                item {
                    DisplayModeContent(
                        isSingleCardMode = isSingleCardMode,
                        onModeChange = { isSingleCardMode = it },
                        verses = verses
                    )
                }
            }
        }
    }
}



