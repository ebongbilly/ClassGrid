package com.classgrid.models

import kotlin.uuid.Uuid

/**
 * Represents a class or grade level within the school.
 */
data class AcademicClass(
    val id: String = Uuid.random().toString(),
    val name: String, // e.g., "Form 5 A"
    val level: String? = null, // e.g., "Secondary", "Primary"
    val classTeacher: String? = null
)
