package com.classgrid.ui.setup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.classgrid.models.School
import com.classgrid.ui.components.rememberImagePainter
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale

@Composable
fun CreateSchoolScreen(
    onPickLogo: () -> String?,
    onSubmit: (School) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var academicYear by remember { mutableStateOf("") }
    var principalName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var logoPath by remember { mutableStateOf<String?>(null) }
    
    var showError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            modifier = Modifier
                .width(450.dp)
                .padding(16.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Configure Your School",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Please enter the configuration details for your institution.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; showError = false },
                    label = { Text("School Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && name.isBlank()
                )
                
                OutlinedTextField(
                    value = academicYear,
                    onValueChange = { academicYear = it; showError = false },
                    label = { Text("Academic Year * (e.g., 2024/2025)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && academicYear.isBlank()
                )
                
                OutlinedTextField(
                    value = principalName,
                    onValueChange = { principalName = it },
                    label = { Text("Principal / Headmaster Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("School Address") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Logo Picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val painter = rememberImagePainter(logoPath)
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                            .clickable { logoPath = onPickLogo() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (painter != null) {
                            Image(
                                painter = painter,
                                contentDescription = "School Logo",
                                modifier = Modifier.fillMaxSize().padding(4.dp),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No Logo", style = MaterialTheme.typography.labelSmall)
                                Text("Click to select", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                    Column {
                        Text("School Logo (Optional)", style = MaterialTheme.typography.labelLarge)
                        Text(
                            "Recommended: 150x150 px",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (logoPath != null) {
                            Text(
                                "Logo selected: ${logoPath?.substringAfterLast("/")}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
                
                if (showError) {
                    Text(
                        text = "Please fill in all required fields (*)",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onBack) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank() && academicYear.isNotBlank()) {
                                onSubmit(
                                    School(
                                        name = name.trim(),
                                        academicYear = academicYear.trim(),
                                        principalName = principalName.trim(),
                                        address = address.trim(),
                                        logoPath = logoPath
                                    )
                                )
                            } else {
                                showError = true
                            }
                        }
                    ) {
                        Text("Create & Launch")
                    }
                }
            }
        }
    }
}
