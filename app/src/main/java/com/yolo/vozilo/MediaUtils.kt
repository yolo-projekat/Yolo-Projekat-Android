package com.yolo.vozilo

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.graphics.scale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object MediaUtils {

    @SuppressLint("MissingPermission")
    fun createMp4Natively(context: Context, frames: List<Bitmap>, scope: CoroutineScope) {
        if (frames.isEmpty()) return
        val width = 640
        val height = 480
        val outputFile = File(context.cacheDir, "temp_video.mp4")
        try {
            val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height).apply {
                setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
                setInteger(MediaFormat.KEY_BIT_RATE, 1500000)
                setInteger(MediaFormat.KEY_FRAME_RATE, 20)
                setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
            }
            val encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
            encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            val surface = encoder.createInputSurface()
            encoder.start()
            val muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            var trackIndex = -1
            var muxerStarted = false
            val bufferInfo = MediaCodec.BufferInfo()
            frames.forEachIndexed { i, bitmap ->
                val canvas = surface.lockCanvas(null)
                canvas.drawBitmap(bitmap.scale(width, height), 0f, 0f, null)
                surface.unlockCanvasAndPost(canvas)
                var dequeued = false
                while (!dequeued) {
                    val outIdx = encoder.dequeueOutputBuffer(bufferInfo, 5000)
                    if (outIdx == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        trackIndex = muxer.addTrack(encoder.outputFormat)
                        muxer.start()
                        muxerStarted = true
                    } else if (outIdx >= 0) {
                        val data = encoder.getOutputBuffer(outIdx)
                        if (muxerStarted && data != null) {
                            bufferInfo.presentationTimeUs = (i * 1000000L / 20)
                            muxer.writeSampleData(trackIndex, data, bufferInfo)
                        }
                        encoder.releaseOutputBuffer(outIdx, false)
                        dequeued = true
                    } else if (outIdx == MediaCodec.INFO_TRY_AGAIN_LATER) { dequeued = true }
                }
            }
            encoder.stop()
            encoder.release()
            if (muxerStarted) {
                muxer.stop()
                muxer.release()
                saveVideoToGallery(context, outputFile)
            }
            scope.launch(Dispatchers.Main) { Toast.makeText(context, "Video Saved to Gallery!", Toast.LENGTH_SHORT).show() }
        } catch (e: Exception) { Log.e("Media", "Encoding error: ${e.message}") }
    }

    private fun saveVideoToGallery(context: Context, file: File) {
        val values = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, "VOZILO_${System.currentTimeMillis()}.mp4")
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/YoloVozilo")
        }
        val uri = context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
        uri?.let { dest -> context.contentResolver.openOutputStream(dest)?.use { out -> file.inputStream().use { input -> input.copyTo(out) } } }
    }

    fun saveToGallery(context: Context, bitmap: Bitmap, name: String) {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/YoloVozilo")
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        uri?.let { dest -> context.contentResolver.openOutputStream(dest)?.use { out -> bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out) } }
    }
}