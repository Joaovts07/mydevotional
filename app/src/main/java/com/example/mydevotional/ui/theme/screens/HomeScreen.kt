package com.example.mydevotional.ui.theme.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.mydevotional.ui.theme.HomeScreenViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen() {
    val viewModel = remember { HomeScreenViewModel() }
    val versiculo by viewModel.versiculo.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    viewModel.loadVersiculo()
    Column {
        CalendarioMaterial(onDateSelected = { date ->
            viewModel.onDateSelected(date)
        })

        selectedDate?.let { date ->
            Text(text = "Data selecionada: $date")
        }

        versiculo?.let { versiculo ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = versiculo.text, fontSize = 18.sp)
                    Text(text = "- ${versiculo.reference}", fontStyle = FontStyle.Italic)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarioMaterial(onDateSelected: (LocalDate) -> Unit) {
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val activity = context.findActivity()

    Button(onClick = {
        val datePicker = MaterialDatePicker.Builder.datePicker().build()
        datePicker.addOnPositiveButtonClickListener { timestamp ->
            val date = Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.of("UTC"))
                .toLocalDate()
            selectedDate = date
            selectedDate?.let { onDateSelected(it) }
        }
        activity?.supportFragmentManager?.let { datePicker.show(it, "DATE_PICKER") }
    }) {
        Text(text = "Selecionar data")
    }
}

fun android.content.Context.findActivity(): FragmentActivity? {
    var context = this
    while (context is android.content.ContextWrapper) {
        if (context is FragmentActivity) return context
        context = context.baseContext
    }
    return null
}