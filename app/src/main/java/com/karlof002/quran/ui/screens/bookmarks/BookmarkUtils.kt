package com.karlof002.quran.ui.screens.bookmarks

import java.text.SimpleDateFormat
import java.util.*

fun formatDate(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}

