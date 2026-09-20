package com.classgrid.db

import com.classgrid.models.AcademicClass

class ClassRepository {
    private val queries = Database.queries

    fun getAllClasses(schoolId: String): List<AcademicClass> {
        return queries.selectAllClasses(schoolId).executeAsList().map { entity ->
            AcademicClass(
                id = entity.id,
                name = entity.name,
                level = entity.level,
                classTeacher = entity.classTeacher
            )
        }
    }

    fun saveClass(schoolId: String, academicClass: AcademicClass) {
        queries.insertClass(
            id = academicClass.id,
            schoolId = schoolId,
            name = academicClass.name,
            level = academicClass.level,
            classTeacher = academicClass.classTeacher
        )
    }

    fun deleteClass(id: String) {
        queries.deleteClass(id)
    }
}
