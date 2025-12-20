package com.example.util

import javax.sql.DataSource
import java.sql.Connection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend inline fun <T> DataSource.tx(
    crossinline block: (Connection) -> T
): T = withContext(Dispatchers.IO) {
    connection.use { conn ->
        conn.autoCommit = false
        try {
            val result = block(conn)
            conn.commit()
            result
        } catch (e: Exception) {
            conn.rollback()
            throw e
        } finally {
            conn.autoCommit = true
        }
    }
}