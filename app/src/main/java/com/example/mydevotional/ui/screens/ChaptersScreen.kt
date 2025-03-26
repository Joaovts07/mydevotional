package com.example.mydevotional.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mydevotional.navigation.AppDestination
import com.example.mydevotional.viewmodel.VersesViewModel

@Composable
fun ChaptersScreen(navController: NavController, bookName: String, viewModel: VersesViewModel) {
    val chapters by viewModel.chapters.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 50.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                items(chapters) { index ->
                    val chapter = index + 1
                    Text(
                        text = chapter.toString(),
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                viewModel.selectChapter(bookName, chapter)
                                navController.navigate(AppDestination.BibleVerses.route)
                            }
                            .aspectRatio(1f)
                            .wrapContentSize(Alignment.Center),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}