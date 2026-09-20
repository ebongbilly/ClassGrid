package com.classgrid.models

/**
 * Detailed result for a single subject on a report card.
 */
data class SubjectResult(
    val subjectName: String,
    val subjectCode: String,
    val score: Double,
    val maxScore: Double,
    val coefficient: Int,
    val weightedScore: Double = score * coefficient,
    val rank: Int? = null,
    val teacherRemarks: String? = null
)

/**
 * Aggregated data representing a student's performance for an entire exam session.
 */
data class ReportCard(
    val studentId: String,
    val studentName: String,
    val className: String,
    val sessionName: String,
    val academicYear: String,
    val results: List<SubjectResult>,
    val totalWeightedScore: Double,
    val totalCoefficients: Int,
    val average: Double,
    val rank: Int,
    val classSize: Int,
    val principalRemarks: String? = null
)
