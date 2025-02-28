package com.example.mydevotional.ui.theme.screens

import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mydevotional.ReadVerseWithTTS
import com.example.mydevotional.ui.theme.HomeScreenViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen() {
    val viewModel = remember { HomeScreenViewModel() }
    val versiculo by viewModel.versiculo.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    Column(Modifier.padding(start = 4.dp, end = 4.dp, top = 32.dp)) {
        versiculo?.let { versiculo ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            ) {

                Column(Modifier.padding(8.dp)) {
                    Text(text = versiculo.random_verse.text, fontSize = 18.sp)
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "- ${versiculo.random_verse.book} ${versiculo.random_verse.chapter}:${versiculo.random_verse.verse}",
                            fontStyle = FontStyle.Italic
                        )
                        ReadVerseWithTTS(versiculo)
                    }
                }
            }
        }
    }
}

@Composable
fun EditableFieldDate(
    value: String,
    onValueChange: (Date?) -> Unit,
    label: String
) {
    var dataNascimento by remember { mutableStateOf(value) }
    val context = LocalContext.current

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val data = calendar.time
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
            dataNascimento = formato.format(data)
            onValueChange(data)
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    TextField(
        value = dataNascimento,
        onValueChange = { },
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { datePickerDialog.show() },
        readOnly = true,
        enabled = false,
        colors = TextFieldDefaults.colors(
            disabledTextColor = LocalContentColor.current,
            disabledContainerColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}