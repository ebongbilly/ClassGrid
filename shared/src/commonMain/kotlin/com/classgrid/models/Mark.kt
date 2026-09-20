package com.classgrid.models

import kotlin.uuid.Uuid

/**
 * Represents a single mark obtained by a student in a subject during an exam session.
 */
data class Mark(
    val id: String = Uuid.random().toString(),
    val studentId: String,
    val subjectId: String,
    val examSessionId: String,
    val score: Double,
    val maxScore: Double = 20.0,
    val remarks: String? = null
)
