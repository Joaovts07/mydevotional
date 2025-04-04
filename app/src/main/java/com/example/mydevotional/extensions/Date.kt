package com.example.mydevotional.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.formatDate(format: String): String {
    val formatter = SimpleDateFormat(format, Locale.getDefault())
    return formatter.format(this)
}
