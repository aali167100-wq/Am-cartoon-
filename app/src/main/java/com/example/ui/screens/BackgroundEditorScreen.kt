package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.StudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundEditorScreen(
    viewModel: StudioViewModel,
    onBack: () -> Unit
) {
    val project by viewModel.currentProject.collectAsState()
    var selectedBg by remember { mutableStateOf("Cyberpunk City") }

    val backgrounds = listOf(
        Triple("Cyberpunk City", Color(0xFF0D0D11), Color(0xFF1F0D3D)),
        Triple("Magical Forest", Color(0xFF0A1F1C), Color(0xFF1B4D3E)),
        Triple("Cozy Room", Color(0xFF2C221E), Color(0xFF4A3525)),
        Triple("Space Station", Color(0xFF050508), Color(0xFF1A1A2E)),
        Triple("Sunset Beach", Color(0xFF3B1E38), Color(0xFFE056FD))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Background Editor", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Check, contentDescription = "Done")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Preview Card
            item {
                val currentGrad = backgrounds.firstOrNull { it.first == selectedBg }
                val startCol = currentGrad?.second ?: Color(0xFF0D0D11)
                val endCol = currentGrad?.third ?: Color(0xFF1F0D3D)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(listOf(startCol, endCol))),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Landscape, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(selectedBg, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                        }
                    }
                }
            }

            item {
                Text("Select Studio Background", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(backgrounds) { (name, start, end) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clickable { selectedBg = name },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedBg == name) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.horizontalGradient(listOf(start, end)))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("HD Gradient Background Layer", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (selectedBg == name) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
