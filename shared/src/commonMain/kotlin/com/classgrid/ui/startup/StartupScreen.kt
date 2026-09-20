package com.classgrid.ui.startup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.classgrid.models.School

@Composable
fun StartupScreen(
    existingSchools: List<School>,
    onCreateNewSchool: () -> Unit,
    onSelectSchool: (School) -> Unit,
    onDeleteSchool: (School) -> Unit
) {
    var showOpenDialog by remember { mutableStateOf(false) }
    var schoolToDelete by remember { mutableStateOf<School?>(null) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Text(
                text = "Welcome to ClassGrid",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                StartupOptionCard(
                    title = "Create New School",
                    description = "Start a fresh academic database for your institution.",
                    iconLabel = "+",
                    onClick = onCreateNewSchool
                )
                
                StartupOptionCard(
                    title = "Open Existing School",
                    description = "Load an existing school database from your computer.",
                    iconLabel = "📁",
                    onClick = { showOpenDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "© All rights reserved to Ebong Billy\nAvailable for free download at https://237exams.com/",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }

    if (showOpenDialog) {
        AlertDialog(
            onDismissRequest = { showOpenDialog = false },
            title = { Text("Select School") },
            text = {
                if (existingSchools.isEmpty()) {
                    Text("No schools found in the database. Create a new one to get started.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(existingSchools) { school ->
                            Surface(
                                onClick = { 
                                    onSelectSchool(school)
                                    showOpenDialog = false
                                },
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = school.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Year: ${school.academicYear}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    TextButton(
                                        onClick = { schoolToDelete = school }
                                    ) {
                                        Text("Delete", color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showOpenDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    schoolToDelete?.let { school ->
        AlertDialog(
            onDismissRequest = { schoolToDelete = null },
            title = { Text("Delete School") },
            text = { Text("Are you sure you want to delete '${school.name}'? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteSchool(school)
                        schoolToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { schoolToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun StartupOptionCard(
    title: String,
    description: String,
    iconLabel: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .size(width = 340.dp, height = 240.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iconLabel,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
