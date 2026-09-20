package com.classgrid.models

import kotlin.uuid.Uuid

/**
 * Represents the allocation of a subject to a specific class.
 */
data class SubjectAllocation(
    val id: String = Uuid.random().toString(),
    val subjectId: String,
    val classId: String
)
