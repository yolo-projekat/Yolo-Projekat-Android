package com.yolo.vozilo

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.objects.DetectedObject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val udpController = UdpController()
    private val visionAnalyzer = VisionAnalyzer()
    private var webRtcController: WebRtcController? = null

    private val _connected = MutableStateFlow(false)
    val connected: StateFlow<Boolean> = _connected.asStateFlow()

    private val _isCamOn = MutableStateFlow(false)
    val isCamOn: StateFlow<Boolean> = _isCamOn.asStateFlow()

    private val _useJoystick = MutableStateFlow(false)
    val useJoystick: StateFlow<Boolean> = _useJoystick.asStateFlow()

    private val _ocrResultText = MutableStateFlow("")
    val ocrResultText: StateFlow<String> = _ocrResultText.asStateFlow()

    private val _isOcrRunning = MutableStateFlow(false)
    val isOcrRunning: StateFlow<Boolean> = _isOcrRunning.asStateFlow()

    private val _isOcrAutoPilot = MutableStateFlow(false)
    val isOcrAutoPilot: StateFlow<Boolean> = _isOcrAutoPilot.asStateFlow()

    private val _isYoloActive = MutableStateFlow(false)
    val isYoloActive: StateFlow<Boolean> = _isYoloActive.asStateFlow()

    private val _isFollowActive = MutableStateFlow(false)
    val isFollowActive: StateFlow<Boolean> = _isFollowActive.asStateFlow()

    private val _detectedObjects = MutableStateFlow<List<DetectedObject>>(emptyList())
    val detectedObjects: StateFlow<List<DetectedObject>> = _detectedObjects.asStateFlow()

    private val _currentFrame = MutableStateFlow<Bitmap?>(null)
    val currentFrame: StateFlow<Bitmap?> = _currentFrame.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()
    val recordedFrames = mutableListOf<Bitmap>()

    fun toggleCamera(context: Context) {
        val newState = !_isCamOn.value
        _isCamOn.value = newState
        if (newState) {
            webRtcController = WebRtcController(
                context = context,
                scope = viewModelScope,
                onConnectionChange = { _connected.value = it },
                onFrameReceived = { handleNewFrame(it) }
            )
            webRtcController?.start()
        } else {
            webRtcController?.stop()
            _currentFrame.value = null
            _isRecording.value = false
            _isFollowActive.value = false
            _isOcrAutoPilot.value = false
            _detectedObjects.value = emptyList()
            _ocrResultText.value = ""
        }
    }

    private fun handleNewFrame(bitmap: Bitmap) {
        _currentFrame.value = bitmap

        if (_isRecording.value) {
            synchronized(recordedFrames) { recordedFrames.add(bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, false)) }
        }

        if (_isOcrRunning.value) {
            visionAnalyzer.processOcr(bitmap) { text ->
                if (text != _ocrResultText.value) _ocrResultText.value = text
                checkOcrAutoPilot(text)
            }
        }

        if (_isYoloActive.value) {
            visionAnalyzer.processYolo(bitmap) { objects ->
                _detectedObjects.value = objects
                checkYoloFollow(objects, bitmap.width)
            }
        }
    }

    private fun checkOcrAutoPilot(text: String) {
        if (!_isOcrAutoPilot.value || text.isBlank()) return
        val lowerText = text.lowercase()
        val cmd = when {
            "rotate" in lowerText -> "rot_desno"
            "left" in lowerText -> "levo"
            "right" in lowerText -> "desno"
            "back" in lowerText -> "nazad"
            "forward" in lowerText -> "napred"
            else -> null
        }

        if (cmd != null) {
            _isOcrAutoPilot.value = false
            viewModelScope.launch {
                udpController.sendCommand(cmd)
                delay(1500)
                udpController.sendCommand("stop")
                delay(500)
                _ocrResultText.value = ""
                _isOcrAutoPilot.value = true
            }
        }
    }

    private fun checkYoloFollow(objects: List<DetectedObject>, frameWidth: Int) {
        if (!_isFollowActive.value) return
        if (objects.isNotEmpty()) {
            val obj = objects.first()
            val normalizedX = obj.boundingBox.centerX().toFloat() / frameWidth
            viewModelScope.launch {
                when {
                    normalizedX < 0.35f -> udpController.sendCommand("levo")
                    normalizedX > 0.65f -> udpController.sendCommand("desno")
                    else -> udpController.sendCommand("napred")
                }
            }
        } else {
            viewModelScope.launch { udpController.sendCommand("stop") }
        }
    }

    fun sendUdpCmd(cmd: String) {
        viewModelScope.launch { udpController.sendCommand(cmd) }
    }

    fun toggleRecording(context: Context) {
        val newState = !_isRecording.value
        _isRecording.value = newState
        if (newState) {
            synchronized(recordedFrames) { recordedFrames.clear() }
        } else {
            val framesToEncode = synchronized(recordedFrames) { recordedFrames.toList() }
            MediaUtils.createMp4Natively(context, framesToEncode, viewModelScope)
        }
    }

    // Boilerplate toggles
    fun toggleOcr() {
        val state = !_isOcrRunning.value
        _isOcrRunning.value = state
        if (!state) { _isOcrAutoPilot.value = false; _ocrResultText.value = "" }
    }
    fun toggleOcrAuto() {
        _isOcrAutoPilot.value = !_isOcrAutoPilot.value
        if (!_isOcrAutoPilot.value) sendUdpCmd("stop")
    }
    fun toggleYolo() {
        val state = !_isYoloActive.value
        _isYoloActive.value = state
        if (!state) { _detectedObjects.value = emptyList(); _isFollowActive.value = false }
    }
    fun toggleFollow() {
        _isFollowActive.value = !_isFollowActive.value
        if (!_isFollowActive.value) sendUdpCmd("stop")
    }
    fun setJoystick(use: Boolean) { _useJoystick.value = use }

    override fun onCleared() {
        super.onCleared()
        webRtcController?.stop()
        udpController.close()
        visionAnalyzer.close()
    }
}