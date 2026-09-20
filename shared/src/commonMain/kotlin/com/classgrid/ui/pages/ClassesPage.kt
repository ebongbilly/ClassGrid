package com.classgrid.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.classgrid.models.AcademicClass
import com.classgrid.models.Student

@Composable
fun ClassesPage(
    classes: List<AcademicClass>,
    students: List<Student>,
    onAddClass: (AcademicClass) -> Unit,
    onUpdateClass: (AcademicClass) -> Unit,
    onDeleteClass: (AcademicClass) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var classToEdit by remember { mutableStateOf<AcademicClass?>(null) }
    var classToView by remember { mutableStateOf<AcademicClass?>(null) }
    var classToDelete by remember { mutableStateOf<AcademicClass?>(null) }

    if (classToView != null) {
        ClassDetailView(
            academicClass = classToView!!,
            students = students.filter { it.className == classToView!!.name }
                .sortedBy { it.studentName },
            onBack = { classToView = null }
        )
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Class Management", style = MaterialTheme.typography.headlineSmall)
                Button(onClick = { showAddDialog = true }) {
                    Text("+ Add Class")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (classes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No classes recorded yet. Click '+ Add Class' to start.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(classes) { academicClass ->
                        ClassItem(
                            academicClass = academicClass,
                            onEdit = { classToEdit = it },
                            onView = { classToView = it },
                            onDelete = { classToDelete = it }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddClassDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { newClass ->
                onAddClass(newClass)
                showAddDialog = false
            }
        )
    }

    if (classToEdit != null) {
        EditClassDialog(
            academicClass = classToEdit!!,
            onDismiss = { classToEdit = null },
            onConfirm = { updatedClass ->
                onUpdateClass(updatedClass)
                classToEdit = null
            }
        )
    }

    if (classToDelete != null) {
        AlertDialog(
            onDismissRequest = { classToDelete = null },
            title = { Text("Delete Class") },
            text = { Text("Are you sure you want to delete '${classToDelete?.name}'? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteClass(classToDelete!!)
                        classToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { classToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ClassItem(
    academicClass: AcademicClass,
    onEdit: (AcademicClass) -> Unit,
    onView: (AcademicClass) -> Unit,
    onDelete: (AcademicClass) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        ListItem(
            headlineContent = { Text(academicClass.name) },
            supportingContent = { 
                Text("Level: ${academicClass.level ?: "N/A"}  |  Teacher: ${academicClass.classTeacher ?: "None"}") 
            },
            trailingContent = {
                Row {
                    TextButton(onClick = { onView(academicClass) }) {
                        Text("View")
                    }
                    TextButton(onClick = { onEdit(academicClass) }) {
                        Text("Edit")
                    }
                    TextButton(
                        onClick = { onDelete(academicClass) },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete")
                    }
                }
            }
        )
    }
}

@Composable
fun ClassDetailView(
    academicClass: AcademicClass,
    students: List<Student>,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) {
                Text("⬅ Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Class: ${academicClass.name}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        
        Text(
            text = "Class Master/Teacher: ${academicClass.classTeacher ?: "Not Assigned"}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 48.dp, bottom = 16.dp),
            color = MaterialTheme.colorScheme.primary
        )

        HorizontalDivider()
        
        Text(
            text = "Student List (${students.size} students)",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (students.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No students registered in this class.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(students) { student ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        ListItem(
                            headlineContent = { Text(student.studentName) },
                            supportingContent = { Text("ID: ${student.id}") },
                            trailingContent = { Text(student.gender) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddClassDialog(
    onDismiss: () -> Unit,
    onConfirm: (AcademicClass) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("") }
    var teacher by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Class") },
        text = {
            ClassFormFields(
                name = name, onNameChange = { name = it },
                level = level, onLevelChange = { level = it },
                teacher = teacher, onTeacherChange = { teacher = it }
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(AcademicClass(name = name, level = level, classTeacher = teacher)) },
                enabled = name.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditClassDialog(
    academicClass: AcademicClass,
    onDismiss: () -> Unit,
    onConfirm: (AcademicClass) -> Unit
) {
    var name by remember { mutableStateOf(academicClass.name) }
    var level by remember { mutableStateOf(academicClass.level ?: "") }
    var teacher by remember { mutableStateOf(academicClass.classTeacher ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Class") },
        text = {
            ClassFormFields(
                name = name, onNameChange = { name = it },
                level = level, onLevelChange = { level = it },
                teacher = teacher, onTeacherChange = { teacher = it }
            )
        },
        confirmButton = {
            Button(
                onClick = { 
                    onConfirm(academicClass.copy(name = name, level = level, classTeacher = teacher)) 
                },
                enabled = name.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ClassFormFields(
    name: String, onNameChange: (String) -> Unit,
    level: String, onLevelChange: (String) -> Unit,
    teacher: String, onTeacherChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Class Name *") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = level,
            onValueChange = onLevelChange,
            label = { Text("Level (e.g. Secondary)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = teacher,
            onValueChange = onTeacherChange,
            label = { Text("Class Teacher") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
