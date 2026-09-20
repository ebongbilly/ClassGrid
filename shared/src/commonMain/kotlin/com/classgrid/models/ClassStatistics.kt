package com.classgrid.models

/**
 * Holds calculated statistical data for a specific class and exam session.
 */
data class ClassStatistics(
    val className: String,
    val sessionName: String,
    val studentCount: Int,
    val classAverage: Double,
    val passRate: Double, // Percentage of students who passed
    val highestAverage: Double,
    val lowestAverage: Double,
    val topStudentName: String
)

/**
 * Holds calculated statistical data across all classes for a session.
 */
data class GlobalStatistics(
    val academicYear: String,
    val sessionName: String,
    val totalStudents: Int,
    val globalAverage: Double,
    val overallPassRate: Double
)
