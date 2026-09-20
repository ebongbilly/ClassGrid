package com.classgrid.db

import com.classgrid.models.ExamSession
import com.classgrid.models.Mark
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ExamRepository {
    private val queries = Database.queries
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun getAllSessions(schoolId: String): List<ExamSession> {
        return queries.selectAllSessions(schoolId).executeAsList().map { entity ->
            ExamSession(
                id = entity.id,
                name = entity.name,
                academicYear = entity.academicYear,
                date = try { LocalDate.parse(entity.date, formatter) } catch(e: Exception) { LocalDate.now() }
            )
        }
    }

    fun saveSession(schoolId: String, session: ExamSession) {
        queries.insertSession(
            id = session.id,
            schoolId = schoolId,
            name = session.name,
            academicYear = session.academicYear,
            date = session.date.format(formatter)
        )
    }

    fun getAllMarks(schoolId: String): List<Mark> {
        return queries.selectAllMarks(schoolId).executeAsList().map { entity ->
            Mark(
                id = entity.id,
                studentId = entity.studentId,
                subjectId = entity.subjectId,
                examSessionId = entity.examSessionId,
                score = entity.score,
                maxScore = entity.maxScore,
                remarks = entity.remarks
            )
        }
    }

    fun saveMark(schoolId: String, mark: Mark) {
        queries.insertMark(
            id = mark.id,
            schoolId = schoolId,
            studentId = mark.studentId,
            subjectId = mark.subjectId,
            examSessionId = mark.examSessionId,
            score = mark.score,
            maxScore = mark.maxScore,
            remarks = mark.remarks
        )
    }
}
