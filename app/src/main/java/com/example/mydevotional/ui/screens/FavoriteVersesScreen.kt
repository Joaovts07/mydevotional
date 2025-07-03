package com.example.mydevotional.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mydevotional.components.VerseCard
import com.example.mydevotional.viewmodel.VersesViewModel

@Composable
fun FavoriteVersesScreen(viewModel: VersesViewModel) {
    val favoriteVerses by viewModel.favoriteVerses.collectAsState()

    LaunchedEffect(Unit) {
        //viewModel.verifyFavoriteVerses()
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Versos Favoritos",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        if (favoriteVerses.isEmpty()) {
            Text(
                text = "Nenhum verso favoritado ainda.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn {
                items(favoriteVerses.toList()) { it ->
                    VerseCard(
                        verse = it,
                        onFavoriteClick = { viewModel.toggleFavorite(it) }
                    )
                }
            }
        }
    }
}
