package com.rudra.legalassistantbd.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toFormattedDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.toFormattedDateTime(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(this))
}

fun String.toCaseNumber(): String {
    val timestamp = System.currentTimeMillis() % 1000000
    return "CASE/${this.take(3).uppercase()}/$timestamp"
}
