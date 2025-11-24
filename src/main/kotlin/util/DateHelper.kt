package com.example.util

import java.util.Date

fun Date.format(): String {
    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return formatter.format(this)
}
