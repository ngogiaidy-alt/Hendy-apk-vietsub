package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TrackType(val label: String) {
    VIDEO("Luồng Video"),
    AUDIO("Âm thanh & Nhạc"),
    TEXT("Phụ đề & Chữ"),
    EFFECT("Hiệu ứng & Bộ lọc"),
    STICKER("Nhãn dán & Lớp phủ")
}

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val aspectRatio: String = "9:16",
    val durationSeconds: Float = 15.0f,
    val resolution: String = "4K",
    val fps: Int = 60,
    val isCloudSynced: Boolean = true,
    val cloudSyncTimestamp: String = "Just now",
    val thumbnailKey: String = "banner",
    val trackCount: Int = 4,
    val lastEdited: String = "Today"
)

data class TimelineClip(
    val id: String,
    val trackType: TrackType,
    val title: String,
    val startTimeSec: Float,
    val durationSec: Float,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val colorHex: Long = 0xFF3B82F6,
    val textContent: String = "",
    val effectName: String = "",
    val filterType: String = "Normal"
)

data class Keyframe(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timeSec: Float,
    val scale: Float = 1.0f,
    val positionX: Float = 0f,
    val positionY: Float = 0f,
    val rotation: Float = 0f,
    val opacity: Float = 1.0f
)

@Entity(tableName = "ai_generations")
data class AIGenerationItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "KLING_3_0", "VEO_3_1", "SORA_2", "IMAGE_GEN", "VOICE_TTS", etc.
    val modelName: String,
    val prompt: String,
    val aspectRatio: String = "9:16",
    val status: String = "Completed",
    val resultTitle: String,
    val resultPreview: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class AITemplate(
    val id: String,
    val title: String,
    val category: String,
    val aspectRatio: String,
    val duration: String,
    val bpm: Int,
    val musicTitle: String,
    val tags: List<String>,
    val description: String
)

data class VoiceProfile(
    val id: String,
    val name: String,
    val category: String,
    val language: String,
    val gender: String,
    val style: String,
    val sampleQuote: String
)

data class AudioItem(
    val id: String,
    val title: String,
    val artistOrCategory: String,
    val duration: String,
    val isSFX: Boolean = false,
    val tags: String
)

data class AdScriptResult(
    val hook: String,
    val problem: String,
    val agitation: String,
    val solution: String,
    val callToAction: String,
    val visualCues: List<String>,
    val estimatedDuration: String = "30s"
)
