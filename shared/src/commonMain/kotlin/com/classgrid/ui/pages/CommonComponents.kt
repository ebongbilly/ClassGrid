package com.classgrid.ui.pages

import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.classgrid.models.AcademicClass
import com.classgrid.models.ExamSession

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionSelector(
    sessions: List<ExamSession>,
    selectedSession: ExamSession?,
    onSelect: (ExamSession) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedSession?.name ?: "Select Session",
            onValueChange = {},
            readOnly = true,
            label = { Text("Exam Session") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable).width(250.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            sessions.forEach { session ->
                DropdownMenuItem(
                    text = { Text(session.name) },
                    onClick = {
                        onSelect(session)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassSelector(
    classes: List<AcademicClass>,
    selectedClass: AcademicClass?,
    onSelect: (AcademicClass) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedClass?.name ?: "Select Class",
            onValueChange = {},
            readOnly = true,
            label = { Text("Class") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable).width(200.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            classes.forEach { academicClass ->
                DropdownMenuItem(
                    text = { Text(academicClass.name) },
                    onClick = {
                        onSelect(academicClass)
                        expanded = false
                    }
                )
            }
        }
    }
}
