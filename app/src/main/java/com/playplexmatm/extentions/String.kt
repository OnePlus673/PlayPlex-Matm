package com.playplexmatm.extentions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getCurrentDateTimeFormatter(): String {
    val dateFormat = SimpleDateFormat("hh:mm a dd:MMM:yy", Locale.getDefault())
    return dateFormat.format(Date())
}

fun String.extractNumber(): String {
    val regex = Regex("(get|give)(\\d+)")
    val matchResult = regex.find(this)

    return matchResult?.groupValues?.get(2) ?: if (this == "0") "0" else this
}
fun String.extractString(): String {
    val regex = Regex("(get|give)(\\d+)")
    val matchResult = regex.find(this)

    return matchResult?.groupValues?.get(1) ?: ""
}

fun getCurrentDateFormatted(): String {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val currentDate = Date()
    return dateFormat.format(currentDate)
}
