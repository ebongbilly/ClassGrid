package com.classgrid.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.classgrid.models.*
import com.classgrid.ui.components.rememberImagePainter
import com.classgrid.utils.MeritExportData
import com.classgrid.utils.getPdfExportService

@Composable
fun StatisticsPage(
    school: School,
    examSessions: List<ExamSession>,
    classes: List<AcademicClass>,
    students: List<Student>,
    subjects: List<Subject>,
    allocations: List<SubjectAllocation>,
    marks: List<Mark>
) {
    var selectedSession by remember { mutableStateOf(examSessions.firstOrNull()) }
    var selectedClass by remember { mutableStateOf(classes.firstOrNull()) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val logoPainter = rememberImagePainter(school.logoPath)
            if (logoPainter != null) {
                Image(
                    painter = logoPainter,
                    contentDescription = "School Logo",
                    modifier = Modifier.size(150.dp),
                    contentScale = ContentScale.Fit
                )
            }
            
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = school.name.uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Order of Merit / Session Statistics",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
            
            if (logoPainter != null) {
                Spacer(modifier = Modifier.size(150.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Selectors
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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

            if (selectedSession != null && selectedClass != null) {
                Button(
                    onClick = {
                        val classStudents = students.filter { it.className == selectedClass!!.name }
                        val classSubjectIds = allocations.filter { it.classId == selectedClass!!.id }.map { it.subjectId }
                        val classSubjects = subjects.filter { it.id in classSubjectIds }
                        
                        val meritList = classStudents.map { student ->
                            val studentMarks = marks.filter { it.studentId == student.id && it.examSessionId == selectedSession!!.id }
                            var totalWeighted = 0.0
                            var totalCoefs = 0
                            classSubjects.forEach { sub ->
                                val mk = studentMarks.find { it.subjectId == sub.id }
                                if (mk != null) {
                                    totalWeighted += (mk.score * sub.coefficient)
                                    totalCoefs += sub.coefficient
                                }
                            }
                            val average = if (totalCoefs > 0) totalWeighted / totalCoefs else 0.0
                            
                            MeritExportData(
                                studentName = student.studentName,
                                scores = classSubjects.associate { sub -> 
                                    sub.id to studentMarks.find { it.subjectId == sub.id }?.score 
                                },
                                average = average
                            )
                        }.sortedByDescending { it.average }

                        getPdfExportService().exportOrderOfMerit(
                            school = school,
                            session = selectedSession!!,
                            academicClass = selectedClass!!,
                            meritList = meritList,
                            subjects = classSubjects
                        )
                    }
                ) {
                    Text("📄 Export Merit List to PDF")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (selectedSession == null || selectedClass == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Please select both an Exam Session and a Class to view the Order of Merit.")
            }
        } else {
            val classStudents = students.filter { it.className == selectedClass!!.name }
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
                // Calculate Order of Merit
                val meritList = classStudents.map { student ->
                    val studentMarks = marks.filter { it.studentId == student.id && it.examSessionId == selectedSession!!.id }
                    var totalWeighted = 0.0
                    var totalCoefs = 0
                    classSubjects.forEach { sub ->
                        val mk = studentMarks.find { it.subjectId == sub.id }
                        if (mk != null) {
                            totalWeighted += (mk.score * sub.coefficient)
                            totalCoefs += sub.coefficient
                        }
                    }
                    val average = if (totalCoefs > 0) totalWeighted / totalCoefs else 0.0
                    MeritItem(student, studentMarks, average)
                }.sortedByDescending { it.average }

                OrderOfMeritTable(
                    meritList = meritList,
                    subjects = classSubjects
                )
            }
        }
    }
}

data class MeritItem(
    val student: Student,
    val marks: List<Mark>,
    val average: Double
)

@Composable
fun OrderOfMeritTable(
    meritList: List<MeritItem>,
    subjects: List<Subject>
) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize().horizontalScroll(scrollState)) {
        Column {
            // Header Row
            Row(modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)) {
                TableCell(text = "Rank", width = 60.dp, isHeader = true)
                TableCell(text = "Student Name", width = 200.dp, isHeader = true)
                subjects.forEach { subject ->
                    TableCell(text = "${subject.code} (${subject.coefficient})", width = 100.dp, isHeader = true)
                }
                TableCell(text = "Average / 20", width = 120.dp, isHeader = true)
            }

            // Data Rows
            LazyColumn {
                items(meritList.size) { index ->
                    val item = meritList[index]
                    Row(modifier = Modifier.border(0.5.dp, Color.LightGray)) {
                        TableCell(text = "${index + 1}", width = 60.dp)
                        TableCell(text = item.student.studentName, width = 200.dp)
                        subjects.forEach { subject ->
                            val mark = item.marks.find { it.subjectId == subject.id }
                            TableCell(
                                text = mark?.let { String.format("%.2f", it.score) } ?: "-",
                                width = 100.dp,
                                textAlign = TextAlign.Center
                            )
                        }
                        TableCell(
                            text = String.format("%.2f", item.average),
                            width = 120.dp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TableCell(
    text: String,
    width: Dp,
    isHeader: Boolean = false,
    textAlign: TextAlign = TextAlign.Start,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(48.dp)
            .border(0.5.dp, Color.LightGray)
            .padding(8.dp),
        contentAlignment = if (textAlign == TextAlign.Center) Alignment.Center else Alignment.CenterStart
    ) {
        Text(
            text = text,
            fontWeight = if (isHeader) FontWeight.Bold else fontWeight,
            maxLines = 1,
            textAlign = textAlign
        )
    }
}

