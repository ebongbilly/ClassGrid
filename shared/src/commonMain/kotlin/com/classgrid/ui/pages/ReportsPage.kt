package com.classgrid.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.classgrid.models.*
import com.classgrid.ui.components.rememberImagePainter
import com.classgrid.utils.getPdfExportService
import java.time.LocalDate
import java.time.Period

@Composable
fun ReportsPage(
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
    var selectedStudent by remember { mutableStateOf<Student?>(null) }

    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        // Left Column: Filters and Student Selection list
        Column(modifier = Modifier.width(300.dp).fillMaxHeight()) {
            Text(text = "Report Cards", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            SessionSelector(
                sessions = examSessions,
                selectedSession = selectedSession,
                onSelect = { selectedSession = it; selectedStudent = null }
            )
            Spacer(modifier = Modifier.height(8.dp))
            ClassSelector(
                classes = classes,
                selectedClass = selectedClass,
                onSelect = { selectedClass = it; selectedStudent = null }
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Select Student:", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))

            if (selectedClass != null) {
                val classStudentsList = students.filter { it.className == selectedClass!!.name }
                    .sortedBy { it.studentName }

                if (classStudentsList.isEmpty()) {
                    Text(
                        text = "No students in this class.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) {
                        items(classStudentsList) { student ->
                            val isSelected = selectedStudent?.id == student.id
                            Surface(
                                onClick = { selectedStudent = student },
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium,
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                                        else Color.Transparent,
                                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer 
                                               else MaterialTheme.colorScheme.onSurface
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp, horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "👤",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                    Text(
                                        text = student.studentName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        VerticalDivider()

        // Right Column: Report Card View
        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
            if (selectedSession == null || selectedClass == null || selectedStudent == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Select a session, class, and student to render the report card view.")
                }
            } else {
                val currentStudent = selectedStudent!!
                val currentSession = selectedSession!!
                val currentClass = selectedClass!!

                val studentMarks = marks.filter { 
                    it.studentId == currentStudent.id && it.examSessionId == currentSession.id 
                }

                val classSubjectIds = allocations.filter { it.classId == currentClass.id }.map { it.subjectId }
                val classSubjects = subjects.filter { it.id in classSubjectIds }

                // Calculate Rankings across the class for total average
                val classStudents = students.filter { it.className == currentClass.name }
                val studentAverages = classStudents.map { st ->
                    val stMarks = marks.filter { it.studentId == st.id && it.examSessionId == currentSession.id }
                    var totalWeighted = 0.0
                    var totalCoefs = 0
                    classSubjects.forEach { sub ->
                        val mk = stMarks.find { it.subjectId == sub.id }
                        if (mk != null) {
                            totalWeighted += (mk.score * sub.coefficient)
                            totalCoefs += sub.coefficient
                        }
                    }
                    val avg = if (totalCoefs > 0) totalWeighted / totalCoefs else 0.0
                    st.id to avg
                }.sortedByDescending { it.second }

                val overallRank = studentAverages.indexOfFirst { it.first == currentStudent.id } + 1

                // Calculate Subject-Specific Ranks
                val subjectRanks = classSubjects.associate { sub ->
                    val subjectMarks = classStudents.map { st ->
                        val score = marks.find { 
                            it.studentId == st.id && 
                            it.subjectId == sub.id && 
                            it.examSessionId == currentSession.id 
                        }?.score ?: 0.0
                        st.id to score
                    }.sortedByDescending { it.second }
                    
                    val sRank = subjectMarks.indexOfFirst { it.first == currentStudent.id } + 1
                    sub.id to sRank
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                getPdfExportService().exportReportCard(
                                    school = school,
                                    student = currentStudent,
                                    session = currentSession,
                                    subjects = classSubjects,
                                    marks = studentMarks,
                                    overallRank = overallRank,
                                    classSize = classStudents.size,
                                    studentAverages = studentAverages
                                )
                            }
                        ) {
                            Text("📄 Export to PDF")
                        }
                    }
                    RenderReportCard(
                        school = school,
                        student = currentStudent,
                        session = currentSession,
                        subjects = classSubjects,
                        studentMarks = studentMarks,
                        subjectRanks = subjectRanks,
                        overallRank = overallRank,
                        classSize = classStudents.size,
                        studentAverages = studentAverages
                    )
                }
            }
        }
    }
}

@Composable
fun RenderReportCard(
    school: School,
    student: Student,
    session: ExamSession,
    subjects: List<Subject>,
    studentMarks: List<Mark>,
    subjectRanks: Map<String, Int>,
    overallRank: Int,
    classSize: Int,
    studentAverages: List<Pair<String, Double>>
) {
    // Calculate totals explicitly before composition to avoid lazy-loading side-effects
    val computedResults = subjects.map { subject ->
        val mark = studentMarks.find { it.subjectId == subject.id }
        val score = mark?.score ?: 0.0
        val weighted = score * subject.coefficient
        val hasMark = mark != null
        Triple(subject, weighted, hasMark)
    }

    val totalWeightedScore = computedResults.filter { it.third }.sumOf { it.second }
    val totalCoefficients = computedResults.filter { it.third }.sumOf { it.first.coefficient }

    ElevatedCard(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            // Report Header
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
                } else {
                    Spacer(modifier = Modifier.size(150.dp))
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = school.name.uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "STUDENT PROGRESS REPORT CARD",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "BULLETIN DE NOTES DU PROGRÈS DE L'ÉLÈVE",
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                // Balance the logo on the right if needed, or just leave it asymmetrical
                Spacer(modifier = Modifier.width(150.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Metadata block
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "Student Name: ${student.studentName}", fontWeight = FontWeight.SemiBold)
                    Text(text = "Student ID: ${student.id}", style = MaterialTheme.typography.bodyMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Gender: ${student.gender}", style = MaterialTheme.typography.bodyMedium)
                        student.dateOfBirth?.let { dob ->
                            val age = Period.between(dob, LocalDate.now()).years
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "Age: $age years", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "/ Âge: $age ans", style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Class: ${student.className}", fontWeight = FontWeight.SemiBold)
                    Text(text = "Session: ${session.name}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Academic Year: ${session.academicYear}", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Marks Table Header
            Row(modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer).padding(8.dp)) {
                Text(text = "Subject Name", modifier = Modifier.weight(2.5f), fontWeight = FontWeight.Bold)
                Text(text = "Code", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text(text = "Score (/20)", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text(text = "Coef", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text(text = "Total", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text(text = "Rank", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }

            HorizontalDivider()

            // Table Rows
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(subjects) { subject ->
                    val mark = studentMarks.find { it.subjectId == subject.id }
                    val score = mark?.score ?: 0.0
                    val weighted = score * subject.coefficient
                    val sRank = subjectRanks[subject.id] ?: 0

                    Row(modifier = Modifier.padding(8.dp)) {
                        Text(text = subject.name, modifier = Modifier.weight(2.5f))
                        Text(text = subject.code, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text(
                            text = mark?.let { String.format("%.2f", score) } ?: "-",
                            modifier = Modifier.weight(1.2f),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                        Text(text = subject.coefficient.toString(), modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center)
                        Text(
                            text = mark?.let { String.format("%.2f", weighted) } ?: "-",
                            modifier = Modifier.weight(1.2f),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = if (mark != null) sRank.toString() else "-",
                            modifier = Modifier.weight(0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Summary Metrics Footer
            val finalAverage = if (totalCoefficients > 0) totalWeightedScore / totalCoefficients else 0.0
            val classAverage = if (studentAverages.isNotEmpty()) studentAverages.map { it.second }.sum() / studentAverages.size else 0.0

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Total Weighted Score: ${String.format("%.2f", totalWeightedScore)}", fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "/ Total des points pondérés", style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Total Coefficients: $totalCoefficients", fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "/ Total des coefficients", style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Student Average: ${String.format("%.2f", finalAverage)} / 20.0",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "/ Moyenne de l'élève",
                                style = MaterialTheme.typography.bodySmall,
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
                            Text(text = "Class Size: $classSize students", fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "/ Effectif de la classe", style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
                            Text(text = "Class Average: ${String.format("%.2f", classAverage)}", fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "/ Moyenne de la classe", style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
                            Text(
                                text = "Rank: $overallRank / $classSize",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (overallRank <= classSize / 2) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "/ Rang de l'élève",
                                style = MaterialTheme.typography.bodySmall,
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
