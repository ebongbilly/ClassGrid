package com.classgrid.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.classgrid.db.*
import com.classgrid.models.*
import com.classgrid.ui.pages.*
import com.classgrid.ui.components.rememberImagePainter

enum class DashboardTab(val title: String, val iconLabel: String) {
    Classes("Classes", "🏫"),
    Subjects("Subjects", "📚"),
    Students("Students", "👥"),
    Marks("Marks", "📝"),
    Reports("Reports", "📄"),
    Statistics("Statistics", "📊")
}

@Composable
fun DashboardScreen(
    school: School,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(DashboardTab.Students) }
    
    val classRepo = remember { ClassRepository() }
    val studentRepo = remember { StudentRepository() }
    val subjectRepo = remember { SubjectRepository() }
    val examRepo = remember { ExamRepository() }

    var classes by remember { mutableStateOf(emptyList<AcademicClass>()) }
    var students by remember { mutableStateOf(emptyList<Student>()) }
    var subjects by remember { mutableStateOf(emptyList<Subject>()) }
    var subjectAllocations by remember { mutableStateOf(emptyList<SubjectAllocation>()) }
    var examSessions by remember { mutableStateOf(emptyList<ExamSession>()) }
    var marks by remember { mutableStateOf(emptyList<Mark>()) }

    LaunchedEffect(school.id) {
        classes = classRepo.getAllClasses(school.id)
        students = studentRepo.getAllStudents(school.id)
        subjects = subjectRepo.getAllSubjects(school.id)
        subjectAllocations = subjectRepo.getAllAllocations(school.id)
        examSessions = examRepo.getAllSessions(school.id)
        marks = examRepo.getAllMarks(school.id)
    }

    Row(modifier = Modifier.fillMaxSize()) {
        NavigationRail(
            modifier = Modifier.fillMaxHeight(),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            header = {
                val logoPainter = rememberImagePainter(school.logoPath)
                if (logoPainter != null) {
                    Image(
                        painter = logoPainter,
                        contentDescription = "School Logo",
                        modifier = Modifier.size(150.dp).padding(8.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        text = "CG",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }
        ) {
            DashboardTab.entries.forEach { tab ->
                NavigationRailItem(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    icon = {
                        Text(text = tab.iconLabel, style = MaterialTheme.typography.titleLarge)
                    },
                    label = {
                        Text(text = tab.title)
                    }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            NavigationRailItem(
                selected = false,
                onClick = onLogout,
                icon = {
                    Text(text = "🚪", style = MaterialTheme.typography.titleLarge)
                },
                label = {
                    Text(text = "Exit")
                }
            )
        }
        
        Column(modifier = Modifier.fillMaxSize()) {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Column {
                        Text(text = school.name, style = MaterialTheme.typography.titleLarge)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Academic Year: ${school.academicYear}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (school.principalName.isNotBlank()) {
                                Text(
                                    text = "|  Principal: ${school.principalName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    DashboardTab.Classes -> ClassesPage(
                        classes = classes,
                        students = students,
                        onAddClass = { newClass -> 
                            classRepo.saveClass(school.id, newClass)
                            classes = classRepo.getAllClasses(school.id)
                        },
                        onUpdateClass = { updatedClass ->
                            classRepo.saveClass(school.id, updatedClass)
                            classes = classRepo.getAllClasses(school.id)
                        },
                        onDeleteClass = { classToDelete ->
                            classRepo.deleteClass(classToDelete.id)
                            classes = classRepo.getAllClasses(school.id)
                            students = studentRepo.getAllStudents(school.id) // Refetch students as they might be affected by class deletion in UI logic
                        }
                    )
                    DashboardTab.Subjects -> SubjectsPage(
                        subjects = subjects,
                        classes = classes,
                        allocations = subjectAllocations,
                        onAddSubject = { newSub -> 
                            subjectRepo.saveSubject(school.id, newSub)
                            subjects = subjectRepo.getAllSubjects(school.id)
                        },
                        onUpdateSubject = { updatedSub ->
                            subjectRepo.saveSubject(school.id, updatedSub)
                            subjects = subjectRepo.getAllSubjects(school.id)
                        },
                        onDeleteSubject = { subToDelete ->
                            subjectRepo.deleteSubject(subToDelete.id)
                            subjects = subjectRepo.getAllSubjects(school.id)
                            subjectAllocations = subjectRepo.getAllAllocations(school.id)
                        },
                        onAllocateSubject = { newAlloc -> 
                            subjectRepo.saveAllocation(school.id, newAlloc)
                            subjectAllocations = subjectRepo.getAllAllocations(school.id)
                        }
                    )
                    DashboardTab.Students -> StudentsPage(
                        students = students,
                        classes = classes,
                        onAddStudent = { newStudent -> 
                            studentRepo.saveStudent(school.id, newStudent)
                            students = studentRepo.getAllStudents(school.id)
                        },
                        onUpdateStudent = { updatedStudent ->
                            studentRepo.saveStudent(school.id, updatedStudent)
                            students = studentRepo.getAllStudents(school.id)
                        },
                        onDeleteStudent = { studentToDelete ->
                            studentRepo.deleteStudent(studentToDelete.id)
                            students = studentRepo.getAllStudents(school.id)
                        }
                    )
                    DashboardTab.Marks -> MarksPage(
                        examSessions = examSessions,
                        classes = classes,
                        students = students,
                        subjects = subjects,
                        allocations = subjectAllocations,
                        marks = marks,
                        onAddSession = { newSession ->
                            examRepo.saveSession(school.id, newSession)
                            examSessions = examRepo.getAllSessions(school.id)
                        },
                        onSaveMark = { newMark ->
                            examRepo.saveMark(school.id, newMark)
                            marks = examRepo.getAllMarks(school.id)
                        }
                    )
                    DashboardTab.Reports -> ReportsPage(
                        school = school,
                        examSessions = examSessions,
                        classes = classes,
                        students = students,
                        subjects = subjects,
                        allocations = subjectAllocations,
                        marks = marks
                    )
                    DashboardTab.Statistics -> StatisticsPage(
                        school = school,
                        examSessions = examSessions,
                        classes = classes,
                        students = students,
                        subjects = subjects,
                        allocations = subjectAllocations,
                        marks = marks
                    )
                }
            }
        }
    }
}
