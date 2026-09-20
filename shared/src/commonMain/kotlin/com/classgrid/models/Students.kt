package com.classgrid.models

import java.time.LocalDate
import kotlin.uuid.Uuid

/**
 * Represents a student within the ClassGrid management system.
 *
 * @property id Unique student identification number (e.g., CG-2024-COMPA-001).
 * @property studentName Student's full name.
 * @property className The class/grade the student belongs to (e.g., "Form 5", "Grade 10-A").
 * @property gender The gender of the student (e.g., "Male", "Female").
 * @property dateOfBirth Optional date of birth of the student.
 * @property parentName Optional name of the student's parent.
 * @property parentTel Optional contact number of the student's parent.
 */
data class Student(
    val id: String = Uuid.random().toString(),
    val studentName: String,
    val className: String,
    val gender: String,
    val dateOfBirth: LocalDate? = null,
    val parentName: String? = null,
    val parentTel: String? = null,
) {

    companion object {
        /**
         * Utility function to generate a structured, unique Student ID.
         * Format: CG-[YEAR]-[CLASS-PREFIX]-[SEQUENCE]
         * Example: CG-2025-5A-042
         *
         * @param className The name of the class to incorporate into the ID prefix.
         * @param sequenceNumber A sequential index unique to that class/year combination.
         * @param year The current academic year (defaults to the current calendar year).
         */
        fun generateStudentId(className: String, sequenceNumber: Int, year: Int = LocalDate.now().year): String {
            val classPrefix = className.replace(Regex("[^A-Za-z0-9]"), "").uppercase()
            val formattedSequence = String.format("%03d", sequenceNumber)
            val safeClassPrefix = if (classPrefix.isEmpty()) "GEN" else classPrefix
            return "CG-$year-$safeClassPrefix-$formattedSequence"
        }
    }
}
