package com.classgrid.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.classgrid.models.AcademicClass
import com.classgrid.models.Student
import java.time.LocalDate
import java.time.format.DateTimeParseException

@Composable
fun StudentsPage(
    students: List<Student>,
    classes: List<AcademicClass>,
    onAddStudent: (Student) -> Unit,
    onUpdateStudent: (Student) -> Unit,
    onDeleteStudent: (Student) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var studentToEdit by remember { mutableStateOf<Student?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Student Management", style = MaterialTheme.typography.headlineSmall)
            Button(onClick = { showAddDialog = true }) {
                Text("+ Add Student")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (students.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No students registered yet. Click '+ Add Student' to start.")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(students) { student ->
                    StudentItem(
                        student = student,
                        onEdit = { studentToEdit = it },
                        onDelete = { onDeleteStudent(it) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddStudentDialog(
            classes = classes,
            onDismiss = { showAddDialog = false },
            onConfirm = { newStudent ->
                onAddStudent(newStudent)
                showAddDialog = false
            }
        )
    }

    if (studentToEdit != null) {
        EditStudentDialog(
            student = studentToEdit!!,
            classes = classes,
            onDismiss = { studentToEdit = null },
            onConfirm = { updatedStudent ->
                onUpdateStudent(updatedStudent)
                studentToEdit = null
            }
        )
    }
}

@Composable
fun StudentItem(
    student: Student,
    onEdit: (Student) -> Unit,
    onDelete: (Student) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        ListItem(
            headlineContent = { Text(student.studentName) },
            supportingContent = { 
                val dobText = student.dateOfBirth?.toString() ?: "N/A"
                Text("Class: ${student.className}  |  Gender: ${student.gender}  |  DOB: $dobText") 
            },
            overlineContent = {
                Text("ID: ${student.id}", style = MaterialTheme.typography.labelSmall)
            },
            trailingContent = {
                Row {
                    TextButton(onClick = { onEdit(student) }) {
                        Text("Edit")
                    }
                    TextButton(
                        onClick = { onDelete(student) },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete")
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStudentDialog(
    classes: List<AcademicClass>,
    onDismiss: () -> Unit,
    onConfirm: (Student) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf(classes.firstOrNull()?.name ?: "") }
    var gender by remember { mutableStateOf("Male") }
    var dobStr by remember { mutableStateOf("") }
    var parentName by remember { mutableStateOf("") }
    var parentTel by remember { mutableStateOf("") }
    
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register New Student") },
        text = {
            StudentFormFields(
                name = name, onNameChange = { name = it },
                classes = classes,
                selectedClass = selectedClass, onClassSelect = { selectedClass = it },
                gender = gender, onGenderChange = { gender = it },
                dobStr = dobStr, onDobChange = { dobStr = it },
                parentName = parentName, onParentNameChange = { parentName = it },
                parentTel = parentTel, onParentTelChange = { parentTel = it },
                expanded = expanded, onExpandedChange = { expanded = it }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val dob = try { LocalDate.parse(dobStr) } catch (e: Exception) { null }
                    if (name.isNotBlank() && selectedClass.isNotBlank()) {
                        onConfirm(
                            Student(
                                studentName = name,
                                className = selectedClass,
                                gender = gender,
                                dateOfBirth = dob,
                                parentName = parentName.ifBlank { null },
                                parentTel = parentTel.ifBlank { null }
                            )
                        )
                    }
                },
                enabled = name.isNotBlank() && selectedClass.isNotBlank()
            ) {
                Text("Register")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditStudentDialog(
    student: Student,
    classes: List<AcademicClass>,
    onDismiss: () -> Unit,
    onConfirm: (Student) -> Unit
) {
    var name by remember { mutableStateOf(student.studentName) }
    var selectedClass by remember { mutableStateOf(student.className) }
    var gender by remember { mutableStateOf(student.gender) }
    var dobStr by remember { mutableStateOf(student.dateOfBirth?.toString() ?: "") }
    var parentName by remember { mutableStateOf(student.parentName ?: "") }
    var parentTel by remember { mutableStateOf(student.parentTel ?: "") }
    
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Student Profile") },
        text = {
            StudentFormFields(
                name = name, onNameChange = { name = it },
                classes = classes,
                selectedClass = selectedClass, onClassSelect = { selectedClass = it },
                gender = gender, onGenderChange = { gender = it },
                dobStr = dobStr, onDobChange = { dobStr = it },
                parentName = parentName, onParentNameChange = { parentName = it },
                parentTel = parentTel, onParentTelChange = { parentTel = it },
                expanded = expanded, onExpandedChange = { expanded = it }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val dob = try { LocalDate.parse(dobStr) } catch (e: Exception) { null }
                    if (name.isNotBlank() && selectedClass.isNotBlank()) {
                        onConfirm(
                            student.copy(
                                studentName = name,
                                className = selectedClass,
                                gender = gender,
                                dateOfBirth = dob,
                                parentName = parentName.ifBlank { null },
                                parentTel = parentTel.ifBlank { null }
                            )
                        )
                    }
                },
                enabled = name.isNotBlank() && selectedClass.isNotBlank()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentFormFields(
    name: String, onNameChange: (String) -> Unit,
    classes: List<AcademicClass>,
    selectedClass: String, onClassSelect: (String) -> Unit,
    gender: String, onGenderChange: (String) -> Unit,
    dobStr: String, onDobChange: (String) -> Unit,
    parentName: String, onParentNameChange: (String) -> Unit,
    parentTel: String, onParentTelChange: (String) -> Unit,
    expanded: Boolean, onExpandedChange: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Student Full Name *") },
            modifier = Modifier.fillMaxWidth()
        )

        // Class Selection Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = onExpandedChange
        ) {
            OutlinedTextField(
                value = selectedClass,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Class *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable).fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                if (classes.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("No classes available. Create one first.") },
                        onClick = { onExpandedChange(false) }
                    )
                } else {
                    classes.forEach { academicClass ->
                        DropdownMenuItem(
                            text = { Text(academicClass.name) },
                            onClick = {
                                onClassSelect(academicClass.name)
                                onExpandedChange(false)
                            }
                        )
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Gender Selection
            Column(modifier = Modifier.weight(1f)) {
                Text("Gender", style = MaterialTheme.typography.labelMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = gender == "Male", onClick = { onGenderChange("Male") })
                    Text("Male", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.width(4.dp))
                    RadioButton(selected = gender == "Female", onClick = { onGenderChange("Female") })
                    Text("Female", style = MaterialTheme.typography.bodySmall)
                }
            }

            OutlinedTextField(
                value = dobStr,
                onValueChange = onDobChange,
                label = { Text("Date of Birth (YYYY-MM-DD)") },
                modifier = Modifier.weight(1f),
                placeholder = { Text("2010-05-15") }
            )
        }

        OutlinedTextField(
            value = parentName,
            onValueChange = onParentNameChange,
            label = { Text("Parent Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = parentTel,
            onValueChange = onParentTelChange,
            label = { Text("Parent Telephone") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
