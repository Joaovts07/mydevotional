package com.example.mydevotional.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mydevotional.components.versesListItems
import com.example.mydevotional.viewmodel.VersesViewModel

@Composable
fun VersesScreen(viewModel: VersesViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isSingleCardMode by remember { mutableStateOf(true) }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (uiState.loadFailed) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Não foi possível carregar o capítulo. Verifique sua conexão.",
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = { viewModel.retry() },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Tentar novamente")
            }
        }
    } else {
        LazyColumn {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = { isSingleCardMode = true },
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
                        onClick = { isSingleCardMode = false },
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
            versesListItems(
                bibleResponses = uiState.readings,
                onFavoriteClick = { viewModel.toggleFavorite(it)  }
            )

        }
    }
}
