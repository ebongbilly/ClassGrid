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
import com.classgrid.models.Subject
import com.classgrid.models.SubjectAllocation

@Composable
fun SubjectsPage(
    subjects: List<Subject>,
    classes: List<AcademicClass>,
    allocations: List<SubjectAllocation>,
    onAddSubject: (Subject) -> Unit,
    onUpdateSubject: (Subject) -> Unit,
    onDeleteSubject: (Subject) -> Unit,
    onAllocateSubject: (SubjectAllocation) -> Unit
) {
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var showAllocateDialog by remember { mutableStateOf(false) }
    var subjectToEdit by remember { mutableStateOf<Subject?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Subject Management", style = MaterialTheme.typography.headlineSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { showAllocateDialog = true }) {
                    Text("Allocate to Class")
                }
                Button(onClick = { showAddSubjectDialog = true }) {
                    Text("+ Add Subject")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (subjects.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No subjects created yet. Click '+ Add Subject' to get started.")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(subjects) { subject ->
                    val allocatedClasses = allocations
                        .filter { it.subjectId == subject.id }
                        .mapNotNull { alloc -> classes.find { it.id == alloc.classId }?.name }

                    SubjectCard(
                        subject = subject, 
                        allocatedClasses = allocatedClasses,
                        onEdit = { subjectToEdit = it },
                        onDelete = { onDeleteSubject(it) }
                    )
                }
            }
        }
    }

    if (showAddSubjectDialog) {
        AddSubjectDialog(
            onDismiss = { showAddSubjectDialog = false },
            onConfirm = { newSubject ->
                onAddSubject(newSubject)
                showAddSubjectDialog = false
            }
        )
    }

    if (subjectToEdit != null) {
        EditSubjectDialog(
            subject = subjectToEdit!!,
            onDismiss = { subjectToEdit = null },
            onConfirm = { updatedSubject ->
                onUpdateSubject(updatedSubject)
                subjectToEdit = null
            }
        )
    }

    if (showAllocateDialog) {
        AllocateSubjectDialog(
            subjects = subjects,
            classes = classes,
            onDismiss = { showAllocateDialog = false },
            onConfirm = { allocation ->
                onAllocateSubject(allocation)
                showAllocateDialog = false
            }
        )
    }
}

@Composable
fun SubjectCard(
    subject: Subject, 
    allocatedClasses: List<String>,
    onEdit: (Subject) -> Unit,
    onDelete: (Subject) -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${subject.name} (${subject.code})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Coefficient: ${subject.coefficient}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Row {
                    TextButton(onClick = { onEdit(subject) }) {
                        Text("Edit")
                    }
                    TextButton(
                        onClick = { onDelete(subject) },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Allocated to Classes:",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (allocatedClasses.isEmpty()) {
                Text(
                    text = "Not allocated to any classes yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text(
                    text = allocatedClasses.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun AddSubjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (Subject) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var coefStr by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Subject") },
        text = {
            SubjectFormFields(
                name = name, onNameChange = { name = it },
                code = code, onCodeChange = { code = it },
                coefStr = coefStr, onCoefChange = { coefStr = it }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val coef = coefStr.toIntOrNull() ?: 1
                    if (name.isNotBlank() && code.isNotBlank()) {
                        onConfirm(Subject(name = name.trim(), code = code.trim().uppercase(), coefficient = coef))
                    }
                },
                enabled = name.isNotBlank() && code.isNotBlank()
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
fun EditSubjectDialog(
    subject: Subject,
    onDismiss: () -> Unit,
    onConfirm: (Subject) -> Unit
) {
    var name by remember { mutableStateOf(subject.name) }
    var code by remember { mutableStateOf(subject.code) }
    var coefStr by remember { mutableStateOf(subject.coefficient.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Subject") },
        text = {
            SubjectFormFields(
                name = name, onNameChange = { name = it },
                code = code, onCodeChange = { code = it },
                coefStr = coefStr, onCoefChange = { coefStr = it }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val coef = coefStr.toIntOrNull() ?: 1
                    if (name.isNotBlank() && code.isNotBlank()) {
                        onConfirm(subject.copy(name = name.trim(), code = code.trim().uppercase(), coefficient = coef))
                    }
                },
                enabled = name.isNotBlank() && code.isNotBlank()
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
fun SubjectFormFields(
    name: String, onNameChange: (String) -> Unit,
    code: String, onCodeChange: (String) -> Unit,
    coefStr: String, onCoefChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Subject Name *") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = code,
            onValueChange = onCodeChange,
            label = { Text("Subject Code * (e.g. MATH101)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = coefStr,
            onValueChange = onCoefChange,
            label = { Text("Coefficient *") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllocateSubjectDialog(
    subjects: List<Subject>,
    classes: List<AcademicClass>,
    onDismiss: () -> Unit,
    onConfirm: (SubjectAllocation) -> Unit
) {
    var selectedSubject by remember { mutableStateOf(subjects.firstOrNull()) }
    var selectedClass by remember { mutableStateOf(classes.firstOrNull()) }
    
    var subjectExpanded by remember { mutableStateOf(false) }
    var classExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Allocate Subject to Class") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Subject Selector
                ExposedDropdownMenuBox(
                    expanded = subjectExpanded,
                    onExpandedChange = { subjectExpanded = !subjectExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedSubject?.let { "${it.name} (${it.code})" } ?: "Select Subject",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Subject *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectExpanded) },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = subjectExpanded,
                        onDismissRequest = { subjectExpanded = false }
                    ) {
                        subjects.forEach { subj ->
                            DropdownMenuItem(
                                text = { Text("${subj.name} (${subj.code})") },
                                onClick = {
                                    selectedSubject = subj
                                    subjectExpanded = false
                                }
                            )
                        }
                    }
                }

                // Class Selector
                ExposedDropdownMenuBox(
                    expanded = classExpanded,
                    onExpandedChange = { classExpanded = !classExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedClass?.name ?: "Select Class",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Class *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded) },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = classExpanded,
                        onDismissRequest = { classExpanded = false }
                    ) {
                        classes.forEach { clazz ->
                            DropdownMenuItem(
                                text = { Text(clazz.name) },
                                onClick = {
                                    selectedClass = clazz
                                    classExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedSubject != null && selectedClass != null) {
                        onConfirm(
                            SubjectAllocation(
                                subjectId = selectedSubject!!.id,
                                classId = selectedClass!!.id
                            )
                        )
                    }
                },
                enabled = selectedSubject != null && selectedClass != null
            ) {
                Text("Allocate")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
