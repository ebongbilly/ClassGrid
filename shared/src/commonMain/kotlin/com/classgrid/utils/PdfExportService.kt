package com.classgrid.utils

import com.classgrid.models.*

interface PdfExportService {
    fun exportReportCard(
        school: School,
        student: Student,
        session: ExamSession,
        subjects: List<Subject>,
        marks: List<Mark>,
        overallRank: Int,
        classSize: Int,
        studentAverages: List<Pair<String, Double>>
    )

    fun exportOrderOfMerit(
        school: School,
        session: ExamSession,
        academicClass: AcademicClass,
        meritList: List<MeritExportData>,
        subjects: List<Subject>
    )
}

data class MeritExportData(
    val studentName: String,
    val scores: Map<String, Double?>, // subjectId to score
    val average: Double
)

expect fun getPdfExportService(): PdfExportService
