package com.yolo.vozilo

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class VisionAnalyzer {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val objectDetector = ObjectDetection.getClient(
        ObjectDetectorOptions.Builder().setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableClassification()
            .enableMultipleObjects()
            .build()
    )

    fun processOcr(bitmap: Bitmap, onResult: (String) -> Unit) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        recognizer.process(inputImage).addOnSuccessListener { visionText ->
            val detected = visionText.text.lines().firstOrNull { it.isNotBlank() } ?: ""
            onResult(detected)
        }
    }

    fun processYolo(bitmap: Bitmap, onResult: (List<DetectedObject>) -> Unit) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        objectDetector.process(inputImage).addOnSuccessListener { objects ->
            onResult(objects)
        }
    }

    fun close() {
        recognizer.close()
        objectDetector.close()
    }
}