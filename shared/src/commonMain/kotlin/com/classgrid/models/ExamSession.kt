package com.classgrid.models

import java.time.LocalDate
import kotlin.uuid.Uuid

/**
 * Represents a specific examination period.
 */
data class ExamSession(
    val id: String = Uuid.random().toString(),
    val name: String, // e.g., "First Term", "Final Exam"
    val academicYear: String, // e.g., "2024/2025"
    val date: LocalDate = LocalDate.now()
)
