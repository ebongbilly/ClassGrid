package com.classgrid.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.classgrid.models.*

@Composable
fun MarksPage(
    examSessions: List<ExamSession>,
    classes: List<AcademicClass>,
    students: List<Student>,
    subjects: List<Subject>,
    allocations: List<SubjectAllocation>,
    marks: List<Mark>,
    onAddSession: (ExamSession) -> Unit,
    onSaveMark: (Mark) -> Unit
) {
    var selectedSession by remember { mutableStateOf(examSessions.firstOrNull()) }
    var selectedClass by remember { mutableStateOf(classes.firstOrNull()) }
    var showAddSessionDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Mark Entry Sheet", style = MaterialTheme.typography.headlineSmall)
            Button(onClick = { showAddSessionDialog = true }) {
                Text("+ New Exam Session")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selectors
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SessionSelector(
                sessions = examSessions,
                selectedSession = selectedSession,
                onSelect = { selectedSession = it }
            )
            ClassSelector(
                classes = classes,
                selectedClass = selectedClass,
                onSelect = { selectedClass = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (examSessions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No exam sessions active. Click '+ New Exam Session' at the top right to enable mark inputs.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else if (selectedSession == null || selectedClass == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Please select both an Exam Session and a Class to enter marks.")
            }
        } else {
            val classStudents = students.filter { it.className == selectedClass!!.name }
                .sortedBy { it.studentName }
            
            val classSubjectIds = allocations.filter { it.classId == selectedClass!!.id }.map { it.subjectId }
            val classSubjects = subjects.filter { it.id in classSubjectIds }

            if (classStudents.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No students in this class.")
                }
            } else if (classSubjects.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No subjects allocated to this class.")
                }
            } else {
                MarkEntryGrid(
                    students = classStudents,
                    subjects = classSubjects,
                    session = selectedSession!!,
                    marks = marks,
                    onSaveMark = onSaveMark
                )
            }
        }
    }

    if (showAddSessionDialog) {
        AddSessionDialog(
            academicYear = examSessions.firstOrNull()?.academicYear ?: "",
            onDismiss = { showAddSessionDialog = false },
            onConfirm = { 
                onAddSession(it)
                if (selectedSession == null) selectedSession = it
                showAddSessionDialog = false
            }
        )
    }
}

@Composable
fun MarkEntryGrid(
    students: List<Student>,
    subjects: List<Subject>,
    session: ExamSession,
    marks: List<Mark>,
    onSaveMark: (Mark) -> Unit
) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize().horizontalScroll(scrollState)) {
        Column {
            // Header Row
            Row(modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)) {
                TableCell(text = "Student Name", width = 200.dp, isHeader = true)
                subjects.forEach { subject ->
                    TableCell(text = "${subject.name} (${subject.coefficient})", width = 120.dp, isHeader = true)
                }
            }

            // Student Rows
            LazyColumn {
                items(students) { student ->
                    Row(modifier = Modifier.border(0.5.dp, Color.LightGray)) {
                        TableCell(text = student.studentName, width = 200.dp)
                        subjects.forEach { subject ->
                            val currentMark = marks.find { 
                                it.studentId == student.id && 
                                it.subjectId == subject.id && 
                                it.examSessionId == session.id 
                            }
                            MarkInputCell(
                                mark = currentMark,
                                onSave = { score ->
                                    onSaveMark(
                                        currentMark?.copy(score = score) ?: Mark(
                                            studentId = student.id,
                                            subjectId = subject.id,
                                            examSessionId = session.id,
                                            score = score
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TableCell(text: String, width: Dp, isHeader: Boolean = false) {
    Box(
        modifier = Modifier
            .width(width)
            .height(48.dp)
            .border(0.5.dp, Color.LightGray)
            .padding(8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
    }
}

@Composable
fun MarkInputCell(mark: Mark?, onSave: (Double) -> Unit) {
    var text by remember(mark) { mutableStateOf(mark?.score?.toString() ?: "") }
    val score = text.toDoubleOrNull()
    val isError = score != null && (score < 0.0 || score > 20.0)

    Box(
        modifier = Modifier
            .width(120.dp)
            .height(48.dp)
            .padding(vertical = 2.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = text,
            onValueChange = { input ->
                // Allow empty string, single dot, or numbers for ergonomic typing
                if (input.isEmpty() || input == "." || input.toDoubleOrNull() != null) {
                    text = input
                    val parsed = input.toDoubleOrNull()
                    if (parsed != null && parsed in 0.0..20.0) {
                        onSave(parsed)
                    }
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = if (isError) MaterialTheme.colorScheme.errorContainer 
                            else if (text.isNotEmpty()) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = MaterialTheme.shapes.small
                )
                .border(
                    width = 1.dp,
                    color = if (isError) MaterialTheme.colorScheme.error 
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.small
                )
                .padding(8.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                color = if (isError) MaterialTheme.colorScheme.onErrorContainer 
                        else MaterialTheme.colorScheme.onSurface
            )
        )
    }
}


@Composable
fun AddSessionDialog(
    academicYear: String,
    onDismiss: () -> Unit,
    onConfirm: (ExamSession) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var year by remember { mutableStateOf(academicYear) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Exam Session") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Session Name (e.g. First Term)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text("Academic Year") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && year.isNotBlank()) {
                        onConfirm(ExamSession(name = name, academicYear = year))
                    }
                },
                enabled = name.isNotBlank() && year.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
