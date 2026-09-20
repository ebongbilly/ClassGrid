package com.classgrid.db

import com.classgrid.models.Subject
import com.classgrid.models.SubjectAllocation

class SubjectRepository {
    private val queries = Database.queries

    fun getAllSubjects(schoolId: String): List<Subject> {
        return queries.selectAllSubjects(schoolId).executeAsList().map { entity ->
            Subject(
                id = entity.id,
                name = entity.name,
                code = entity.code,
                coefficient = entity.coefficient
            )
        }
    }

    fun saveSubject(schoolId: String, subject: Subject) {
        queries.insertSubject(
            id = subject.id,
            schoolId = schoolId,
            name = subject.name,
            code = subject.code,
            coefficient = subject.coefficient
        )
    }

    fun deleteSubject(id: String) {
        queries.deleteSubject(id)
    }

    fun getAllAllocations(schoolId: String): List<SubjectAllocation> {
        return queries.selectAllAllocations(schoolId).executeAsList().map { entity ->
            SubjectAllocation(
                id = entity.id,
                subjectId = entity.subjectId,
                classId = entity.classId
            )
        }
    }

    fun saveAllocation(schoolId: String, allocation: SubjectAllocation) {
        queries.insertAllocation(
            id = allocation.id,
            schoolId = schoolId,
            subjectId = allocation.subjectId,
            classId = allocation.classId
        )
    }
}
