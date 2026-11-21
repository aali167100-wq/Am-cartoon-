package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.StudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatorScreen(
    viewModel: StudioViewModel,
    onNavigate: (String) -> Unit
) {
    val project by viewModel.currentProject.collectAsState()
    val character by viewModel.currentCharacter.collectAsState()
    val currentFrame by viewModel.currentFrame.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val transform by viewModel.stageTransform.collectAsState()

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var scaleFactor by remember { mutableStateOf(1f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(project.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Badge(containerColor = MaterialTheme.colorScheme.primary) {
                            Text("Frame $currentFrame/${project.totalFrames}", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigate("home") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Home")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigate("timeline") }) {
                        Icon(Icons.Default.Timeline, contentDescription = "Timeline")
                    }
                    IconButton(onClick = { onNavigate("preview") }) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Preview")
                    }
                    IconButton(onClick = { onNavigate("export") }) {
                        Icon(Icons.Default.Download, contentDescription = "Export")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            // Studio Bottom Toolbar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onNavigate("character_creator") }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Face, contentDescription = "Character", tint = MaterialTheme.colorScheme.primary)
                            Text("Character", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    IconButton(onClick = { onNavigate("background_editor") }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Landscape, contentDescription = "Background", tint = MaterialTheme.colorScheme.primary)
                            Text("Background", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    IconButton(onClick = { onNavigate("voice_studio") }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Mic, contentDescription = "Voice", tint = MaterialTheme.colorScheme.primary)
                            Text("Voice", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    IconButton(onClick = { viewModel.setCurrentFrame(currentFrame - 1) }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Prev Frame", tint = MaterialTheme.colorScheme.onSurface)
                            Text("Prev", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    IconButton(onClick = { viewModel.setCurrentFrame(currentFrame + 1) }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Next Frame", tint = MaterialTheme.colorScheme.onSurface)
                            Text("Next", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(Color(0xFF0D0D11), Color(0xFF1A1A26))))
        ) {
            // 2D Canvas Stage
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF14141A))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                            viewModel.updateTransform(offsetX, offsetY, scaleFactor, 0f)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // Render Cartoon Character Rig on Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centerX = size.width / 2f + offsetX
                    val centerY = size.height / 2f + offsetY

                    // Background grid / axis
                    drawCircle(
                        color = Color.Cyan.copy(alpha = 0.3f),
                        radius = 80f * scaleFactor,
                        center = Offset(centerX, centerY)
                    )

                    // Head
                    drawCircle(
                        color = Color(0xFFFFCC80),
                        radius = 40f * scaleFactor,
                        center = Offset(centerX, centerY - 40f * scaleFactor)
                    )

                    // Eyes
                    drawCircle(
                        color = Color.Black,
                        radius = 6f * scaleFactor,
                        center = Offset(centerX - 12f * scaleFactor, centerY - 50f * scaleFactor)
                    )
                    drawCircle(
                        color = Color.Black,
                        radius = 6f * scaleFactor,
                        center = Offset(centerX + 12f * scaleFactor, centerY - 50f * scaleFactor)
                    )

                    // Body / Outfit
                    drawRect(
                        color = Color(0xFF00E5FF),
                        topLeft = Offset(centerX - 30f * scaleFactor, centerY),
                        size = androidx.compose.ui.geometry.Size(60f * scaleFactor, 90f * scaleFactor)
                    )
                }

                // Stage Info Overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Text("Character: ${character.name}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Tool: Puppet Rig / Move", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                }
            }
        }
    }
}
