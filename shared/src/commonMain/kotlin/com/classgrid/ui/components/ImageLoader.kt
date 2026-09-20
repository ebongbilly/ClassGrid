package com.classgrid.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter

@Composable
expect fun rememberImagePainter(path: String?): Painter?
