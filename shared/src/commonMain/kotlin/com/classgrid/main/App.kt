package com.classgrid.main

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.classgrid.db.SchoolRepository
import com.classgrid.models.School
import com.classgrid.ui.dashboard.DashboardScreen
import com.classgrid.ui.setup.CreateSchoolScreen
import com.classgrid.ui.startup.StartupScreen

sealed class AppScreen {
    data object Startup : AppScreen()
    data object CreateSchool : AppScreen()
    data class Dashboard(val school: School) : AppScreen()
}

@Composable
@Preview
fun App(onPickImage: (() -> String?)? = null) {
    val schoolRepository = remember { SchoolRepository() }
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Startup) }
    
    var refreshTrigger by remember { mutableStateOf(0) }
    val existingSchools by produceState(initialValue = emptyList<School>(), refreshTrigger) {
        value = schoolRepository.getAllSchools()
    }

    MaterialTheme {
        when (val screen = currentScreen) {
            is AppScreen.Startup -> {
                StartupScreen(
                    existingSchools = existingSchools,
                    onCreateNewSchool = {
                        currentScreen = AppScreen.CreateSchool
                    },
                    onSelectSchool = { school ->
                        currentScreen = AppScreen.Dashboard(school)
                    },
                    onDeleteSchool = { school ->
                        schoolRepository.deleteSchool(school.id)
                        refreshTrigger++
                    }
                )
            }
            is AppScreen.CreateSchool -> {
                CreateSchoolScreen(
                    onPickLogo = { onPickImage?.invoke() },
                    onSubmit = { schoolInfo ->
                        schoolRepository.saveSchool(schoolInfo)
                        refreshTrigger++
                        currentScreen = AppScreen.Dashboard(schoolInfo)
                    },
                    onBack = {
                        currentScreen = AppScreen.Startup
                    }
                )
            }
            is AppScreen.Dashboard -> {
                DashboardScreen(
                    school = screen.school,
                    onLogout = {
                        refreshTrigger++
                        currentScreen = AppScreen.Startup
                    }
                )
            }
        }
    }
}
