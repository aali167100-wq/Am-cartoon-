package com.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.auth.auth
import com.example.ui.screens.*
import com.example.ui.theme.AMCartoonTheme
import com.example.viewmodel.StudioViewModel
import kotlinx.coroutines.launch

fun configureAppCheck(context: Context, intent: Intent?) {
    try {
        val appCheck = Firebase.appCheck
        val prefs = context.getSharedPreferences("app_check_debug", Context.MODE_PRIVATE)
        prefs.edit().putString("app_check_debug_token_key", "DEBUG_TOKEN").commit()
        appCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )
    } catch (e: Exception) {
        Log.e("AppCheck", "Failed to install App Check provider", e)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        enableEdgeToEdge()

        setContent {
            AMCartoonTheme {
                val studioViewModel: StudioViewModel = viewModel()
                val navController = rememberNavController()
                val auth = Firebase.auth
                var currentUserId by remember { mutableStateOf(auth.currentUser?.uid ?: "") }
                var isSigningIn by remember { mutableStateOf(false) }

                LaunchedEffect(currentUserId) {
                    if (currentUserId.isNotBlank()) {
                        studioViewModel.loadUserData(currentUserId)
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (currentUserId.isBlank()) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("AM CARTOON", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Professional 2D Cartoon Animation Studio", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(32.dp))
                                    val scope = rememberCoroutineScope()
                                    Button(
                                        onClick = {
                                            isSigningIn = true
                                            scope.launch {
                                                val res = signInWithGoogleCredential(this@MainActivity, auth)
                                                if (res.isSuccess) {
                                                    currentUserId = res.getOrNull() ?: ""
                                                }
                                                isSigningIn = false
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth().height(50.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        if (isSigningIn) {
                                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                                        } else {
                                            Text("Sign in with Google", color = Color.Black, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        key(currentUserId) {
                            NavHost(navController = navController, startDestination = "home") {
                                composable("home") {
                                    HomeScreen(
                                        viewModel = studioViewModel,
                                        onNavigate = { route -> navController.navigate(route) },
                                        onSignOut = {
                                            auth.signOut()
                                            currentUserId = ""
                                        }
                                    )
                                }
                                composable("animator") {
                                    AnimatorScreen(
                                        viewModel = studioViewModel,
                                        onNavigate = { route -> navController.navigate(route) }
                                    )
                                }
                                composable("character_creator") {
                                    CharacterCreatorScreen(
                                        viewModel = studioViewModel,
                                        userId = currentUserId,
                                        onBack = { navController.popBackStack() }
                                    )
                                }
                                composable("background_editor") {
                                    BackgroundEditorScreen(
                                        viewModel = studioViewModel,
                                        onBack = { navController.popBackStack() }
                                    )
                                }
                                composable("timeline") {
                                    TimelineScreen(
                                        viewModel = studioViewModel,
                                        onBack = { navController.popBackStack() }
                                    )
                                }
                                composable("voice_studio") {
                                    VoiceStudioScreen(
                                        viewModel = studioViewModel,
                                        onBack = { navController.popBackStack() }
                                    )
                                }
                                composable("preview") {
                                    PreviewScreen(
                                        viewModel = studioViewModel,
                                        onBack = { navController.popBackStack() }
                                    )
                                }
                                composable("export") {
                                    ExportScreen(
                                        viewModel = studioViewModel,
                                        onBack = { navController.popBackStack() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
