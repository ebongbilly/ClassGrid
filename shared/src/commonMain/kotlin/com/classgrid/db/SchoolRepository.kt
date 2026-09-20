package com.classgrid.db

import com.classgrid.models.School

class SchoolRepository {
    private val queries = Database.queries

    fun getAllSchools(): List<School> {
        return queries.selectAllSchools().executeAsList().map { entity ->
            School(
                id = entity.id,
                name = entity.name,
                academicYear = entity.academicYear,
                principalName = entity.principalName,
                address = entity.address,
                logoPath = entity.logoPath
            )
        }
    }

    fun saveSchool(school: School) {
        queries.insertSchool(
            id = school.id,
            name = school.name,
            academicYear = school.academicYear,
            principalName = school.principalName,
            address = school.address,
            logoPath = school.logoPath
        )
    }

    fun deleteSchool(id: String) {
        queries.transaction {
            queries.deleteClassesBySchool(id)
            queries.deleteStudentsBySchool(id)
            queries.deleteSubjectsBySchool(id)
            queries.deleteAllocationsBySchool(id)
            queries.deleteSessionsBySchool(id)
            queries.deleteMarksBySchool(id)
            queries.deleteSchool(id)
        }
    }
}
