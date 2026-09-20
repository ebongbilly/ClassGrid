package com.classgrid.main

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.classgrid.db.Database
import com.classgrid.db.DatabaseDriverFactory
import java.awt.FileDialog
import java.awt.Frame

fun main() = application {
    // Initialize Database
    Database.init(DatabaseDriverFactory())

    Window(
        onCloseRequest = ::exitApplication,
        title = "ClassGrid",
    ) {
        App(onPickImage = {
            val fileDialog = FileDialog(Frame(), "Select School Logo", FileDialog.LOAD)
            fileDialog.setFilenameFilter { _, name -> 
                name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") 
            }
            fileDialog.isVisible = true
            if (fileDialog.file != null) {
                fileDialog.directory + fileDialog.file
            } else {
                null
            }
        })
    }
}
