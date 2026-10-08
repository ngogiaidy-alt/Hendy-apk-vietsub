package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import com.example.model.Keyframe
import com.example.model.Project
import com.example.model.TimelineClip
import com.example.model.TrackType
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.UUID

data class FileMetadata(
    val uri: Uri,
    val name: String,
    val sizeBytes: Long,
    val mimeType: String
)

data class ImportedProjectData(
    val title: String,
    val aspectRatio: String,
    val durationSeconds: Float,
    val resolution: String,
    val fps: Int,
    val clips: List<TimelineClip>,
    val keyframes: List<Keyframe>,
    val activeFilter: String
)

object FileImportExportHelper {

    fun queryUriMetadata(context: Context, uri: Uri): FileMetadata {
        var displayName = "file_${System.currentTimeMillis()}"
        var size: Long = 0
        val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"

        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) {
                        cursor.getString(nameIndex)?.let { displayName = it }
                    }
                    if (sizeIndex != -1) {
                        size = cursor.getLong(sizeIndex)
                    }
                }
            }
        } catch (_: Exception) {
            // Fallback to path segment
            uri.lastPathSegment?.let { displayName = it }
        }

        return FileMetadata(
            uri = uri,
            name = displayName,
            sizeBytes = size,
            mimeType = mimeType
        )
    }

    fun parseSubtitleFromUri(context: Context, uri: Uri): List<TimelineClip> {
        val result = mutableListOf<TimelineClip>()
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    val lines = reader.readLines()
                    var i = 0
                    while (i < lines.size) {
                        val line = lines[i].trim()
                        if (line.isEmpty() || line == "WEBVTT") {
                            i++
                            continue
                        }

                        // Check if line contains timestamp separator "-->"
                        val timestampLine = if (line.contains("-->")) {
                            line
                        } else if (i + 1 < lines.size && lines[i + 1].contains("-->")) {
                            i++
                            lines[i].trim()
                        } else {
                            i++
                            continue
                        }

                        val timeParts = timestampLine.split("-->").map { it.trim() }
                        if (timeParts.size >= 2) {
                            val startSec = parseTimestampToSeconds(timeParts[0])
                            val endSec = parseTimestampToSeconds(timeParts[1])
                            val duration = (endSec - startSec).coerceAtLeast(1.0f)

                            // Read subtitle text lines
                            val textBuilder = StringBuilder()
                            i++
                            while (i < lines.size && lines[i].trim().isNotEmpty()) {
                                if (textBuilder.isNotEmpty()) textBuilder.append(" ")
                                textBuilder.append(lines[i].trim())
                                i++
                            }

                            val textContent = textBuilder.toString()
                            if (textContent.isNotEmpty()) {
                                result.add(
                                    TimelineClip(
                                        id = UUID.randomUUID().toString(),
                                        trackType = TrackType.TEXT,
                                        title = "Vietsub: ${textContent.take(24)}...",
                                        startTimeSec = startSec,
                                        durationSec = duration,
                                        textContent = textContent,
                                        colorHex = 0xFFD97706
                                    )
                                )
                            }
                        } else {
                            i++
                        }
                    }
                }
            }
        } catch (_: Exception) {
            // If failed to parse, create default clip
            result.add(
                TimelineClip(
                    id = UUID.randomUUID().toString(),
                    trackType = TrackType.TEXT,
                    title = "Phụ đề đã nhập",
                    startTimeSec = 0f,
                    durationSec = 5f,
                    textContent = "Phụ đề mẫu từ tệp đã tải",
                    colorHex = 0xFFD97706
                )
            )
        }
        return result
    }

    private fun parseTimestampToSeconds(ts: String): Float {
        val clean = ts.replace(',', '.')
        val parts = clean.split(":")
        return try {
            when (parts.size) {
                3 -> {
                    val hours = parts[0].toFloat()
                    val minutes = parts[1].toFloat()
                    val seconds = parts[2].toFloat()
                    hours * 3600f + minutes * 60f + seconds
                }
                2 -> {
                    val minutes = parts[0].toFloat()
                    val seconds = parts[1].toFloat()
                    minutes * 60f + seconds
                }
                else -> parts[0].toFloat()
            }
        } catch (_: Exception) {
            0f
        }
    }

    fun generateSrtContent(clips: List<TimelineClip>): String {
        val textClips = clips.filter { it.trackType == TrackType.TEXT }.sortedBy { it.startTimeSec }
        val sb = StringBuilder()
        textClips.forEachIndexed { index, clip ->
            val startMs = (clip.startTimeSec * 1000).toLong()
            val endMs = ((clip.startTimeSec + clip.durationSec) * 1000).toLong()
            sb.append("${index + 1}\n")
            sb.append("${formatSrtTimestamp(startMs)} --> ${formatSrtTimestamp(endMs)}\n")
            sb.append("${clip.textContent.ifEmpty { clip.title }}\n\n")
        }
        return sb.toString()
    }

    fun generateVttContent(clips: List<TimelineClip>): String {
        val textClips = clips.filter { it.trackType == TrackType.TEXT }.sortedBy { it.startTimeSec }
        val sb = StringBuilder()
        sb.append("WEBVTT\n\n")
        textClips.forEachIndexed { index, clip ->
            val startMs = (clip.startTimeSec * 1000).toLong()
            val endMs = ((clip.startTimeSec + clip.durationSec) * 1000).toLong()
            sb.append("${index + 1}\n")
            sb.append("${formatVttTimestamp(startMs)} --> ${formatVttTimestamp(endMs)}\n")
            sb.append("${clip.textContent.ifEmpty { clip.title }}\n\n")
        }
        return sb.toString()
    }

    private fun formatSrtTimestamp(ms: Long): String {
        val hours = ms / 3600000
        val minutes = (ms % 3600000) / 60000
        val seconds = (ms % 60000) / 1000
        val millis = ms % 1000
        return String.format("%02d:%02d:%02d,%03d", hours, minutes, seconds, millis)
    }

    private fun formatVttTimestamp(ms: Long): String {
        val hours = ms / 3600000
        val minutes = (ms % 3600000) / 60000
        val seconds = (ms % 60000) / 1000
        val millis = ms % 1000
        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis)
    }

    fun exportProjectToJson(
        project: Project,
        clips: List<TimelineClip>,
        keyframes: List<Keyframe>,
        filter: String
    ): String {
        val root = JSONObject()
        root.put("app", "Hendy Vietsub")
        root.put("version", "3.0")
        root.put("exportedAt", System.currentTimeMillis())

        val projObj = JSONObject()
        projObj.put("title", project.title)
        projObj.put("aspectRatio", project.aspectRatio)
        projObj.put("durationSeconds", project.durationSeconds)
        projObj.put("resolution", project.resolution)
        projObj.put("fps", project.fps)
        projObj.put("filter", filter)
        root.put("project", projObj)

        val clipsArray = JSONArray()
        clips.forEach { clip ->
            val cObj = JSONObject()
            cObj.put("id", clip.id)
            cObj.put("trackType", clip.trackType.name)
            cObj.put("title", clip.title)
            cObj.put("startTimeSec", clip.startTimeSec.toDouble())
            cObj.put("durationSec", clip.durationSec.toDouble())
            cObj.put("speed", clip.speed.toDouble())
            cObj.put("volume", clip.volume.toDouble())
            cObj.put("colorHex", clip.colorHex)
            cObj.put("textContent", clip.textContent)
            cObj.put("effectName", clip.effectName)
            cObj.put("filterType", clip.filterType)
            clipsArray.put(cObj)
        }
        root.put("clips", clipsArray)

        val kfArray = JSONArray()
        keyframes.forEach { kf ->
            val kObj = JSONObject()
            kObj.put("id", kf.id)
            kObj.put("timeSec", kf.timeSec.toDouble())
            kObj.put("scale", kf.scale.toDouble())
            kObj.put("positionX", kf.positionX.toDouble())
            kObj.put("positionY", kf.positionY.toDouble())
            kObj.put("rotation", kf.rotation.toDouble())
            kObj.put("opacity", kf.opacity.toDouble())
            kfArray.put(kObj)
        }
        root.put("keyframes", kfArray)

        return root.toString(2)
    }

    fun importProjectFromJson(jsonString: String): ImportedProjectData {
        val root = JSONObject(jsonString)
        val projObj = root.optJSONObject("project") ?: JSONObject()
        val title = projObj.optString("title", "Dự án đã nhập")
        val aspectRatio = projObj.optString("aspectRatio", "9:16")
        val durationSeconds = projObj.optDouble("durationSeconds", 15.0).toFloat()
        val resolution = projObj.optString("resolution", "4K")
        val fps = projObj.optInt("fps", 60)
        val activeFilter = projObj.optString("filter", "Normal")

        val clips = mutableListOf<TimelineClip>()
        val clipsArray = root.optJSONArray("clips") ?: JSONArray()
        for (i in 0 until clipsArray.length()) {
            val cObj = clipsArray.getJSONObject(i)
            val trackTypeName = cObj.optString("trackType", "VIDEO")
            val trackType = try {
                TrackType.valueOf(trackTypeName)
            } catch (_: Exception) {
                TrackType.VIDEO
            }
            clips.add(
                TimelineClip(
                    id = cObj.optString("id", UUID.randomUUID().toString()),
                    trackType = trackType,
                    title = cObj.optString("title", "Clip $i"),
                    startTimeSec = cObj.optDouble("startTimeSec", 0.0).toFloat(),
                    durationSec = cObj.optDouble("durationSec", 5.0).toFloat(),
                    speed = cObj.optDouble("speed", 1.0).toFloat(),
                    volume = cObj.optDouble("volume", 1.0).toFloat(),
                    colorHex = cObj.optLong("colorHex", 0xFF2563EB),
                    textContent = cObj.optString("textContent", ""),
                    effectName = cObj.optString("effectName", ""),
                    filterType = cObj.optString("filterType", "Normal")
                )
            )
        }

        val keyframes = mutableListOf<Keyframe>()
        val kfArray = root.optJSONArray("keyframes") ?: JSONArray()
        for (i in 0 until kfArray.length()) {
            val kObj = kfArray.getJSONObject(i)
            keyframes.add(
                Keyframe(
                    id = kObj.optString("id", UUID.randomUUID().toString()),
                    timeSec = kObj.optDouble("timeSec", 0.0).toFloat(),
                    scale = kObj.optDouble("scale", 1.0).toFloat(),
                    positionX = kObj.optDouble("positionX", 0.0).toFloat(),
                    positionY = kObj.optDouble("positionY", 0.0).toFloat(),
                    rotation = kObj.optDouble("rotation", 0.0).toFloat(),
                    opacity = kObj.optDouble("opacity", 1.0).toFloat()
                )
            )
        }

        return ImportedProjectData(
            title = title,
            aspectRatio = aspectRatio,
            durationSeconds = durationSeconds,
            resolution = resolution,
            fps = fps,
            clips = clips,
            keyframes = keyframes,
            activeFilter = activeFilter
        )
    }

    fun writeTextToUri(context: Context, uri: Uri, content: String): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(content)
                    writer.flush()
                }
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    fun writeSampleMediaToUri(context: Context, uri: Uri, headerTitle: String): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                val sampleHeader = "HENDY_VIETSUB_MEDIA_EXPORT_${System.currentTimeMillis()}\nTitle: $headerTitle\nResolution: 4K 60FPS Pro Mux\n"
                outputStream.write(sampleHeader.toByteArray(Charsets.UTF_8))
                outputStream.flush()
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    fun shareText(context: Context, content: String, title: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, content)
        }
        val chooser = Intent.createChooser(intent, "Chia sẻ: $title")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
