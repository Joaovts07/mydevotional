package com.example.mydevotional.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mydevotional.components.CalendarReadings
import com.example.mydevotional.components.CompleteReadingButton
import com.example.mydevotional.components.versesListItems
import com.example.mydevotional.extensions.formatDate
import com.example.mydevotional.model.BibleTranslation
import com.example.mydevotional.viewmodel.DailyReadingViewModel
import com.example.mydevotional.viewmodel.HomeScreenViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeScreenViewModel = hiltViewModel(),
    dailyReadingViewModel: DailyReadingViewModel = hiltViewModel()
) {
    val bibleResponse by homeViewModel.bibleResponse.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()
    val completedReadings by homeViewModel.completedReadings.collectAsState()
    val isReadingCompleted by dailyReadingViewModel.isReadingCompletedForSelectedDate.collectAsState()

    var calendarHeight by remember { mutableStateOf(354.dp) }
    var isSingleCardMode by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

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
                    completedReadings = completedReadings,
                    onDateSelected = { selectedDate -> homeViewModel.selectDate(selectedDate) }
                )
            }
        }
        if (isLoading) {
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
        } else {
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
            if (bibleResponse.isEmpty()) {
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
                                text = "Nenhuma Leitura para hoje",
                                fontSize = 18.sp,
                                modifier = Modifier.fillMaxWidth()
                            )


                        }
                    }
                }
            }
            versesListItems(
                bibleResponses = bibleResponse,
                isSingleCardMode = isSingleCardMode,
                onFavoriteClick = { homeViewModel.toggleFavorite(it) }
            )
        }
        item {
            CompleteReadingButton(
                isReadingCompleted = isReadingCompleted,
                onClick = {
                    val date = homeViewModel.selectedDate.value?.formatDate("yyy-MM-dd") ?: ""
                    if (homeViewModel.verifyDailyIsReading()) {
                        homeViewModel.markReadingAsComplete(date)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Leitura Desmarcada!",
                                duration = SnackbarDuration.Short
                            )
                        }
                    } else {
                        homeViewModel.markReadingAsComplete(date)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Leitura marcada como lida!",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .height(48.dp)
            )


        }
    }
}






