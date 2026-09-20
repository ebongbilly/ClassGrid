package com.classgrid.ui.components

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.loadImageBitmap
import java.io.File

@Composable
actual fun rememberImagePainter(path: String?): Painter? {
    if (path == null) return null
    return remember(path) {
        try {
            val file = File(path)
            if (file.exists()) {
                BitmapPainter(loadImageBitmap(file.inputStream()))
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
