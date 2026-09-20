package com.classgrid.db

import com.classgrid.models.Student
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StudentRepository {
    private val queries = Database.queries
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun getAllStudents(schoolId: String): List<Student> {
        return queries.selectAllStudents(schoolId).executeAsList().map { entity ->
            Student(
                id = entity.id,
                studentName = entity.studentName,
                className = entity.className,
                gender = entity.gender,
                dateOfBirth = entity.dateOfBirth?.let { 
                    try { LocalDate.parse(it, formatter) } catch(e: Exception) { null } 
                },
                parentName = entity.parentName,
                parentTel = entity.parentTel
            )
        }
    }

    fun saveStudent(schoolId: String, student: Student) {
        queries.insertStudent(
            id = student.id,
            schoolId = schoolId,
            studentName = student.studentName,
            className = student.className,
            gender = student.gender,
            dateOfBirth = student.dateOfBirth?.format(formatter),
            parentName = student.parentName,
            parentTel = student.parentTel
        )
    }

    fun deleteStudent(id: String) {
        queries.deleteStudent(id)
    }
}
