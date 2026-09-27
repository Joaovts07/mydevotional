package com.example.mydevotional.ui.screens

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mydevotional.components.CalendarReadings
import com.example.mydevotional.components.CompleteReadingButton
import com.example.mydevotional.components.versesListItems
import com.example.mydevotional.viewmodel.HomeScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    snackbarHostState: SnackbarHostState,
    homeViewModel: HomeScreenViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    var calendarHeight by remember { mutableStateOf(354.dp) }
    val listState = rememberLazyListState()

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            }
            homeViewModel.saveReadingsFromImage(bitmap)
        }
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            homeViewModel.messageShown()
        }
    }

    LaunchedEffect(remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }) {
        val minHeight = 80.dp
        val maxHeight = 354.dp
        calendarHeight = (maxHeight - (listState.firstVisibleItemScrollOffset / 5).dp).coerceIn(
            minHeight,
            maxHeight
        )
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(calendarHeight)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                CalendarReadings(
                    completedReadings = uiState.completedDays,
                    onDateSelected = { homeViewModel.selectDate(it) }
                )
            }

        }
        if (uiState.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if (uiState.loadFailed) {
            item {
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
                            text = "Não foi possível carregar a leitura. Verifique sua conexão.",
                            fontSize = 18.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = { homeViewModel.retry() },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }
        } else {
             if (uiState.readings.isEmpty()) {
                item {
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
                                text = "Nenhuma leitura para este dia",
                                fontSize = 18.sp,
                                modifier = Modifier.fillMaxWidth()
                            )


                        }
                    }
                }
            }
            versesListItems(
                bibleResponses = uiState.readings,
                onFavoriteClick = { homeViewModel.toggleFavorite(it) }
            )
        }
        item {
            CompleteReadingButton(
                isReadingCompleted = uiState.isReadingCompleted,
                onClick = { homeViewModel.toggleReadingComplete() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .height(48.dp)
            )

        }
    }
}
