package com.classgrid.models

import kotlin.uuid.Uuid

/**
 * Represents the school/institution metadata for the current session.
 */
data class School(
    val id: String = Uuid.random().toString(),
    val name: String,
    val academicYear: String,
    val principalName: String = "",
    val address: String = "",
    val logoPath: String? = null
)
