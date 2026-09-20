package com.classgrid.models

import kotlin.uuid.Uuid

/**
 * Represents a subject taught in the school.
 */
data class Subject(
    val id: String = Uuid.random().toString(),
    val name: String, // e.g., "Mathematics"
    val code: String, // e.g., "MATH101"
    val coefficient: Int = 1 // Weighting factor for average calculation
)
