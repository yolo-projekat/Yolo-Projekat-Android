package com.yolo.vozilo

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.SmartButton
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val currentColorScheme = MaterialTheme.colorScheme

    val connected by viewModel.connected.collectAsState()
    val isCamOn by viewModel.isCamOn.collectAsState()
    val useJoystick by viewModel.useJoystick.collectAsState()
    val ocrResultText by viewModel.ocrResultText.collectAsState()
    val isOcrRunning by viewModel.isOcrRunning.collectAsState()
    val isOcrAutoPilot by viewModel.isOcrAutoPilot.collectAsState()
    val isYoloActive by viewModel.isYoloActive.collectAsState()
    val isFollowActive by viewModel.isFollowActive.collectAsState()
    val detectedObjects by viewModel.detectedObjects.collectAsState()
    val currentFrame by viewModel.currentFrame.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()

    Surface(modifier = Modifier.fillMaxSize(), color = currentColorScheme.background) {
        Column(Modifier.padding(30.dp)) {
            HeaderSection(
                isConnected = connected,
                isCamOn = isCamOn,
                onToggleCam = { viewModel.toggleCamera(context) },
                onCapture = {
                    currentFrame?.let {
                        MediaUtils.saveToGallery(context, it, "PI_CAP_${System.currentTimeMillis()}.jpg")
                        Toast.makeText(context, "Photo Saved!", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            VideoSectionLive(
                isOn = isCamOn, ocrOverlay = ocrResultText, objects = detectedObjects,
                isOcrRunning = isOcrRunning, isOcrAutoPilot = isOcrAutoPilot,
                isFollowActive = isFollowActive, frame = currentFrame
            )

            AnimatedVisibility(visible = isCamOn, enter = fadeIn() + expandVertically()) {
                Column {
                    Row(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FeaturePill("YOLO", Icons.Default.TrackChanges, Modifier.weight(1f), if (isYoloActive) ThemeSuccess else ThemeBlue) {
                            viewModel.toggleYolo()
                        }

                        FeaturePill(
                            label = if (isRecording) "STOP" else "RECORD",
                            icon = if (isRecording) Icons.Default.Stop else Icons.Default.FiberManualRecord,
                            modifier = Modifier.weight(1f),
                            backgroundColor = if (isRecording) ThemeAlert else ThemeBlue
                        ) {
                            viewModel.toggleRecording(context)
                        }

                        IconButton(
                            onClick = { viewModel.toggleOcr() },
                            modifier = Modifier
                                .background(if (isOcrRunning) ThemeSuccess else currentColorScheme.surface, RoundedCornerShape(12.dp))
                                .size(48.dp)
                        ) {
                            Icon(Icons.Default.TextFields, null, tint = if (isOcrRunning) Color.White else ThemeBlue)
                        }
                    }

                    Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (isYoloActive) {
                            FeaturePill(
                                label = if (isFollowActive) "FOLLOWING" else "FOLLOW",
                                icon = Icons.AutoMirrored.Filled.DirectionsRun,
                                modifier = Modifier.weight(1f),
                                backgroundColor = if (isFollowActive) ThemeSuccess else Color.Gray
                            ) {
                                viewModel.toggleFollow()
                            }
                        }

                        if (isOcrRunning) {
                            FeaturePill(
                                label = if (isOcrAutoPilot) "AUTO ON" else "AUTO OFF",
                                icon = Icons.Default.SmartButton,
                                modifier = Modifier.weight(1f),
                                backgroundColor = if (isOcrAutoPilot) ThemeSuccess else Color.Gray
                            ) {
                                viewModel.toggleOcrAuto()
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), Arrangement.End, Alignment.CenterVertically) {
                Text("JOYSTICK", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ThemeBlue.copy(0.5f))
                Spacer(Modifier.width(8.dp))
                Switch(checked = useJoystick, onCheckedChange = { viewModel.setJoystick(it) })
            }

            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                if (useJoystick) {
                    CircularJoystick { viewModel.sendUdpCmd(it) }
                } else {
                    CompactDPad(onStart = { viewModel.sendUdpCmd(it) }, onStop = { viewModel.sendUdpCmd("stop") })
                }
            }
        }
    }
}