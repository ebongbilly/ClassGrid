package com.classgrid.db

import app.cash.sqldelight.ColumnAdapter

object Database {
    private var database: ClassGridDb? = null

    fun init(driverFactory: DatabaseDriverFactory) {
        if (database == null) {
            val driver = driverFactory.createDriver()
            database = ClassGridDb(
                driver = driver,
                subjectEntityAdapter = SubjectEntity.Adapter(
                    coefficientAdapter = object : ColumnAdapter<Int, Long> {
                        override fun decode(databaseValue: Long): Int = databaseValue.toInt()
                        override fun encode(value: Int): Long = value.toLong()
                    }
                )
            )
        }
    }

    val queries: ClassGridDbQueries
        get() = database?.classGridDbQueries ?: throw IllegalStateException("Database not initialized")
}
