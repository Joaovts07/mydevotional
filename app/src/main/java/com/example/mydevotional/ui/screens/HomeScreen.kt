package com.example.mydevotional.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mydevotional.components.CalendarReadings
import com.example.mydevotional.components.CompleteReadingButton
import com.example.mydevotional.components.versesListItems
import com.example.mydevotional.navigation.AppDestination
import com.example.mydevotional.viewmodel.DailyReadingViewModel
import com.example.mydevotional.viewmodel.HomeScreenViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeScreenViewModel = hiltViewModel(),
    dailyReadingViewModel: DailyReadingViewModel = hiltViewModel()
) {
    val bibleResponse by homeViewModel.bibleResponse.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()
    val completedReadingsCalendar by dailyReadingViewModel.completedDays.collectAsState()
    val completedReadingsDay by dailyReadingViewModel.isReadingCompletedForSelectedDate.collectAsState()

    var calendarHeight by remember { mutableStateOf(354.dp) }
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

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AppDestination.ReadingScanner.route) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Escanear Leitura"
                )
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                        completedReadings = completedReadingsCalendar,
                        onDateSelected = { selectedDate ->
                            homeViewModel.selectDate(selectedDate)
                            dailyReadingViewModel.updateSelectedDate(selectedDate)
                        }
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
                    onFavoriteClick = { homeViewModel.toggleFavorite(it) }
                )
            }
            item {
                CompleteReadingButton(
                    isReadingCompleted = completedReadingsDay,
                    onClick = {
                        dailyReadingViewModel.toggleReadingComplete(homeViewModel.selectedDate.value)
                        val message = if (completedReadingsDay) "Leitura Desmarcada!" else "Leitura marcada como lida!"
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = message,
                                duration = SnackbarDuration.Short
                            )
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
}
