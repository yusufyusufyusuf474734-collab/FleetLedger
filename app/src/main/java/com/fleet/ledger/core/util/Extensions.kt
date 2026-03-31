package com.fleet.ledger.core.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun Double.formatCurrency(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("tr", "TR"))
    return format.format(this)
}

fun Long.formatDate(pattern: String = "dd.MM.yyyy"): String {
    val sdf = SimpleDateFormat(pattern, Locale("tr", "TR"))
    return sdf.format(Date(this))
}

fun Long.formatDateTime(pattern: String = "dd.MM.yyyy HH:mm"): String {
    val sdf = SimpleDateFormat(pattern, Locale("tr", "TR"))
    return sdf.format(Date(this))
}
