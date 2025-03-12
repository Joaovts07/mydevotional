package com.example.mydevotional.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mydevotional.viewmodel.VersesViewModel

@Composable
fun ChaptersScreen(navController: NavController, bookName: String, viewModel: VersesViewModel) {
    val chapters by viewModel.chapters.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn {
            items(chapters) { index ->
                val chapter = index + 1
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            viewModel.selectChapter(bookName, chapter)
                            navController.navigate("verses/$bookName/$chapter")
                        },
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
}