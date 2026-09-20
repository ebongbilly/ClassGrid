package com.classgrid.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val databasePath = File(System.getProperty("user.home"), ".classgrid/classgrid.db")
        val databaseFile = databasePath.absoluteFile
        val isNew = !databaseFile.exists() || databaseFile.length() == 0L
        
        if (!databaseFile.parentFile.exists()) {
            databaseFile.parentFile.mkdirs()
        }
        
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")
        
        if (isNew) {
            ClassGridDb.Schema.create(driver)
        }
        
        return driver
    }
}
