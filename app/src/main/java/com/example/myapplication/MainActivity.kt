package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.camera.view.PreviewView
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowCompat
import androidx.lifecycle.LifecycleOwner

class MainActivity : ComponentActivity() {
    private var selectedCamera: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Disable light status bar for dark icons
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        setContent {
            val context = LocalContext.current
            val previewView = remember { PreviewView(context) }
            val lifecycleOwner = LocalLifecycleOwner.current

            CameraApp(
                previewView = previewView,
                lifecycleOwner = lifecycleOwner,
                onNavigateToChat = {
                    startActivity(Intent(this, SecondActivity::class.java))
                },
                onCameraSelected = { camera ->
                    selectedCamera = if (camera == "Front Camera") CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
                    setupCamera(previewView, lifecycleOwner)
                }
            )
        }
    }

    private fun setupCamera(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = androidx.camera.core.Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                // Unbind previous use cases before binding new ones
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, selectedCamera, preview)

                Log.d("Camera", "Camera setup successfully")
            } catch (e: Exception) {
                Log.e("Camera", "Camera binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }
}

@Composable
fun CameraApp(
    previewView: PreviewView,
    lifecycleOwner: LifecycleOwner,
    onNavigateToChat: () -> Unit,
    onCameraSelected: (String) -> Unit
) {
    var selectedCamera by remember { mutableStateOf("Back Camera") }

    LaunchedEffect(lifecycleOwner) {
        setupCamera(previewView, lifecycleOwner)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "FDplay",
                color = Color.Green,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )
            DropdownMenu(
                selectedCamera = selectedCamera,
                onCameraSelected = {
                    selectedCamera = it
                    onCameraSelected(it)
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .background(Color.Black)
        ) {
            AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
            Icon(
                painter = painterResource(id = R.drawable.ic_rec),
                contentDescription = "Record",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 2.dp)
                    .size(70.dp),
                tint = Color.Green
            )
        }

        BottomAppBar(
            backgroundColor = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            IconButton(onClick = onNavigateToChat) {
                Icon(Icons.Filled.Chat, contentDescription = "Chat", tint = Color.Green)
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { setupCamera(previewView, lifecycleOwner) }) {
                Icon(Icons.Filled.CameraAlt, contentDescription = "Refresh", tint = Color.Green)
            }
        }
    }
}


fun setupCamera(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {

}

@Composable
fun DropdownMenu(
    selectedCamera: String,
    onCameraSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val cameraOptions = listOf("Front Camera", "Back Camera")

    Box {
        TextButton(onClick = { expanded = true }) {
            Text(text = selectedCamera, color = Color.Green)
        }
        androidx.compose.material.DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            cameraOptions.forEach { option ->
                androidx.compose.material.DropdownMenuItem(onClick = {
                    onCameraSelected(option)
                    expanded = false
                }) {
                    Text(option)
                }
            }
        }
    }
}

@Preview
@Composable
fun CameraAppPreview() {
    CameraApp(
        previewView = PreviewView(LocalContext.current),
        lifecycleOwner = LocalLifecycleOwner.current,
        onNavigateToChat = {},
        onCameraSelected = {}
    )
}
