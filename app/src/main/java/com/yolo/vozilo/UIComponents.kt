package com.yolo.vozilo

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateLeft
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.vision.objects.DetectedObject
import kotlin.math.atan2
import kotlin.math.sqrt

@Composable
fun VideoSectionLive(isOn: Boolean, ocrOverlay: String, objects: List<DetectedObject>, isOcrRunning: Boolean, isOcrAutoPilot: Boolean, isFollowActive: Boolean, frame: Bitmap?) {
    val textPaint = remember { Paint().apply { textSize = 38f; typeface = Typeface.DEFAULT_BOLD; setShadowLayer(3f, 2f, 2f, android.graphics.Color.BLACK) } }

    Card(modifier = Modifier.fillMaxWidth().height(220.dp), shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, ThemeBlue.copy(0.1f)), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Box(Modifier.fillMaxSize().background(Color.Black), Alignment.Center) {
            if (isOn && frame != null) {
                Image(bitmap = frame.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds)

                Canvas(Modifier.fillMaxSize()) {
                    val scaleX = size.width / frame.width
                    val scaleY = size.height / frame.height

                    objects.forEach { obj ->
                        val box = obj.boundingBox
                        val rectColor = if(isFollowActive) ThemeSuccess else Color.Yellow
                        drawRect(color = rectColor, topLeft = Offset(box.left * scaleX, box.top * scaleY), size = Size(box.width() * scaleX, box.height() * scaleY), style = Stroke(width = 2.dp.toPx()))
                        obj.labels.firstOrNull { it.confidence > 0.4f }?.let { label ->
                            drawContext.canvas.nativeCanvas.drawText("${label.text.uppercase()} ${(label.confidence * 100).toInt()}%", box.left * scaleX, (box.top * scaleY) - 15, textPaint.apply { color = rectColor.toArgb() })
                        }
                    }
                }

                if (isOcrRunning && ocrOverlay.isNotBlank()) {
                    Box(Modifier.align(Alignment.BottomStart).padding(10.dp).background(Color.Black.copy(0.6f), RoundedCornerShape(6.dp)).padding(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TextFields, null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(ocrOverlay, color = if(isOcrAutoPilot) ThemeSuccess else Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Text("STREAM STANDBY", color = ThemeBlue.copy(0.4f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun HeaderSection(isConnected: Boolean, isCamOn: Boolean, onToggleCam: () -> Unit, onCapture: () -> Unit) {
    val currentColorScheme = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Column {
            Text("YOLO VOZILO", fontSize = 24.sp, fontWeight = FontWeight.Black, color = ThemeBlue)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).background(if(isConnected) ThemeSuccess else ThemeAlert, CircleShape))
                Spacer(Modifier.width(6.dp))
                Text(text = if(isConnected) "ONLINE" else "OFFLINE", color = if(isConnected) ThemeSuccess else ThemeAlert, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isCamOn) {
                IconButton(onClick = onCapture, Modifier.padding(end = 8.dp).background(currentColorScheme.surface, CircleShape)) {
                    Icon(Icons.Default.CameraAlt, null, tint = ThemeBlue)
                }
            }
            IconButton(onClick = onToggleCam, modifier = Modifier.background(if(isCamOn) ThemeBlue else currentColorScheme.surface, CircleShape)) {
                Icon(if(isCamOn) Icons.Default.Videocam else Icons.Default.VideocamOff, null, tint = if(isCamOn) Color.White else ThemeBlue)
            }
        }
    }
}

@Composable
fun CompactDPad(onStart: (String) -> Unit, onStop: () -> Unit) {
    Box(Modifier.size(240.dp)) {
        val btnSize = 65.dp
        DPadBtn("▲", Alignment.TopCenter, btnSize, "napred", onStart, onStop)
        DPadBtn("▼", Alignment.BottomCenter, btnSize, "nazad", onStart, onStop)
        DPadBtn("◀", Alignment.CenterStart, btnSize, "levo", onStart, onStop)
        DPadBtn("▶", Alignment.CenterEnd, btnSize, "desno", onStart, onStop)
        RotationBtn(Icons.AutoMirrored.Filled.RotateLeft, Alignment.BottomStart, "rot_levo", onStart, onStop)
        RotationBtn(Icons.AutoMirrored.Filled.RotateRight, Alignment.BottomEnd, "rot_desno", onStart, onStop)
    }
}

@Composable
fun BoxScope.DPadBtn(label: String, btnAlign: Alignment, size: androidx.compose.ui.unit.Dp, cmd: String, onStart: (String) -> Unit, onStop: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val currentColorScheme = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.size(size).align(btnAlign).pointerInput(Unit) {
            detectTapGestures(onPress = { try { isPressed = true; onStart(cmd); awaitRelease() } finally { isPressed = false; onStop() } })
        },
        shape = RoundedCornerShape(16.dp), color = if (isPressed) ThemeBlue else currentColorScheme.surface, border = BorderStroke(1.dp, ThemeBlue.copy(0.1f))
    ) { Box(contentAlignment = Alignment.Center) { Text(label, fontSize = 24.sp, color = if (isPressed) Color.White else ThemeBlue) } }
}

@Composable
fun BoxScope.RotationBtn(icon: ImageVector, btnAlign: Alignment, cmd: String, onStart: (String) -> Unit, onStop: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val currentColorScheme = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.size(56.dp).align(btnAlign).pointerInput(Unit) {
            detectTapGestures(onPress = { try { isPressed = true; onStart(cmd); awaitRelease() } finally { isPressed = false; onStop() } })
        },
        shape = CircleShape, color = if (isPressed) ThemeBlue else currentColorScheme.surface, border = BorderStroke(1.dp, ThemeBlue.copy(0.2f))
    ) { Box(contentAlignment = Alignment.Center) { Icon(icon, null, Modifier.size(28.dp), if (isPressed) Color.White else ThemeBlue) } }
}

@Composable
fun CircularJoystick(onCmd: (String) -> Unit) {
    var offX by remember { mutableFloatStateOf(0f) }
    var offY by remember { mutableFloatStateOf(0f) }
    val radius = 100f
    val currentColorScheme = MaterialTheme.colorScheme
    Box(
        Modifier.size(200.dp).background(currentColorScheme.surface, CircleShape).border(1.dp, ThemeBlue.copy(0.1f), CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(onDragEnd = { offX = 0f; offY = 0f; onCmd("stop") }) { change, drag ->
                    change.consume()
                    val nX = offX + drag.x
                    val nY = offY + drag.y
                    val dist = sqrt(nX * nX + nY * nY)
                    val factor = if (dist > radius) radius / dist else 1f
                    offX = nX * factor
                    offY = nY * factor
                    if (dist > 40f) {
                        val angle = Math.toDegrees(atan2(offY.toDouble(), offX.toDouble()))
                        val cmd = when {
                            angle > -45 && angle <= 45 -> "desno"
                            angle > 45 && angle <= 135 -> "nazad"
                            angle > -135 && angle <= -45 -> "napred"
                            else -> "levo"
                        }
                        onCmd(cmd)
                    }
                }
            }, Alignment.Center
    ) {
        Box(Modifier.offset { IntOffset(offX.toInt(), offY.toInt()) }.size(60.dp).background(ThemeBlue, CircleShape).border(3.dp, Color.White, CircleShape))
    }
}

@Composable
fun FeaturePill(label: String, icon: ImageVector, modifier: Modifier, backgroundColor: Color = ThemeBlue, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = modifier.height(48.dp), shape = RoundedCornerShape(12.dp), color = backgroundColor) {
        Row(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterVertically) {
            Icon(icon, null, Modifier.size(18.dp), Color.White)
            Spacer(Modifier.width(8.dp))
            Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}