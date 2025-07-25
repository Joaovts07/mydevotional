package com.example.mydevotional.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarReadings(
    completedReadings: List<String>,
    onDateSelected: (Date) -> Unit
) {
    val today = Calendar.getInstance().time
    var selectedDate by remember { mutableStateOf(today) }
    var currentMonthCalendar by remember {
        mutableStateOf(Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) })
    }

    val isDarkMode = isSystemInDarkTheme()
    val backgroundColor = if (isDarkMode) Color.DarkGray else Color.White
    val normalTextColor = if (isDarkMode) Color.White else Color.Black
    val selectedDayBgColor = Color(0xFF8AB4F8)
    val readDotColor = Color(0xFF4A90E2)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                currentMonthCalendar = (currentMonthCalendar.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Mês Anterior",
                    tint = normalTextColor
                )
            }

            Text(
                text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentMonthCalendar.time),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = normalTextColor
            )

            IconButton(onClick = {
                currentMonthCalendar = (currentMonthCalendar.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Próximo Mês",
                    tint = normalTextColor
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val weekdays = listOf("D", "S", "T", "Q", "Q", "S", "S") // Domingo a Sábado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weekdays.forEach { day ->
                Text(
                    text = day,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = normalTextColor,
                    modifier = Modifier.width(40.dp), // Ajuste o tamanho para caber o novo dia
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        val daysList = remember(currentMonthCalendar.time) {
            val calendar = currentMonthCalendar.clone() as Calendar
            val firstDayOfMonth = (calendar.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }
            val firstDayOfWeekIndex = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1 // 0-indexed for Sunday

            val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            val calendarDays = mutableListOf<Date?>()

            for (i in 0 until firstDayOfWeekIndex) {
                calendarDays.add(null)

            }
            for (i in 1..daysInMonth) {
                val dayCalendar = (calendar.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, i) }
                calendarDays.add(dayCalendar.time)
            }

            val numWeeksToShow = 6
            while (calendarDays.size < numWeeksToShow * 7) {
                calendarDays.add(null) // Preencher com nulls para os dias do próximo mês
            }
            calendarDays
        }

        val cellSide = 40.dp
        val cellSpacing = 4.dp
        val dayNumberFontSize = 14.sp
        val dotSize = 5.dp
        val dotPadding = 2.dp
        val rowHeight = cellSide + dotSize + dotPadding

        val numWeeksToShow = 6
        val gridHeight = rowHeight * numWeeksToShow


        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .height(gridHeight),
            userScrollEnabled = false,
            verticalArrangement = Arrangement.spacedBy(cellSpacing),
            horizontalArrangement = Arrangement.spacedBy(cellSpacing)
        ) {
            items(daysList) { date ->
                if (date == null) {
                    Spacer(modifier = Modifier.size(cellSide))
                } else {
                    val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
                    val isRead = completedReadings.contains(formattedDate)
                    val isSelected = remember(selectedDate, date) {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate) ==
                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .size(cellSide) // Tamanho da célula
                            .clip(if (isSelected) CircleShape else RoundedCornerShape(0.dp))
                            .background(if (isSelected) selectedDayBgColor else Color.Transparent)
                            .clickable {
                                selectedDate = date
                                onDateSelected(date)
                            }
                            .padding(top = 4.dp, bottom = dotSize + dotPadding),
                    ) {
                        Text(
                            text = SimpleDateFormat("d", Locale.getDefault()).format(date),
                            color = if (isSelected) Color.White else normalTextColor,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = dayNumberFontSize,
                        )
                        if (isRead) {
                            Box(
                                modifier = Modifier
                                    .size(dotSize)
                                    .clip(CircleShape)
                                    .background(readDotColor)
                            )
                        }
                    }
                }
            }
        }
    }
}