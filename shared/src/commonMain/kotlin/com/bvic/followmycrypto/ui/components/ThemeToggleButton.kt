package com.bvic.followmycrypto.ui.components

import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

@Composable
fun ThemeToggleButton(darkTheme: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    val label = if (darkTheme) "Passer au thème clair" else "Passer au thème sombre"
    IconButton(onClick = onToggle, modifier = modifier.semantics { contentDescription = label }) {
        Text(if (darkTheme) "☀️" else "🌙")
    }
}