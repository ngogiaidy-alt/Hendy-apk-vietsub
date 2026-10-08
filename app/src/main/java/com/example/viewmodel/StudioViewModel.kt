package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ProjectRepository
import com.example.model.AIGenerationItem
import com.example.model.AITemplate
import com.example.model.AdScriptResult
import com.example.model.AudioItem
import com.example.model.Keyframe
import com.example.model.Project
import com.example.model.TimelineClip
import com.example.model.TrackType
import com.example.model.VoiceProfile
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class StudioScreen(val label: String) {
    EDIT("Chỉnh sửa"),
    TEMPLATES("Mẫu"),
    AI_SUITE("Công cụ AI"),
    VIETSUB_AUDIO("Dịch & Âm thanh"),
    ME_CLOUD("Tôi"),
    EDITOR("Trình dựng"),
    CAMERA_RECORD("Máy ảnh"),
    AI_VIDEO("Video AI"),
    AI_IMAGE("Hình ảnh AI"),
    AI_AUDIO("Âm thanh AI"),
    AD_SCRIPT("Kịch bản AI");

    companion object {
        val HOME = EDIT
    }
}

class StudioViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = ProjectRepository(database.projectDao())

    val projects: StateFlow<List<Project>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val generations: StateFlow<List<AIGenerationItem>> = repository.allGenerations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val templates: List<AITemplate> = repository.getTemplates()
    val voiceProfiles: List<VoiceProfile> = repository.getVoiceProfiles()
    val royaltyFreeAudio: List<AudioItem> = repository.getRoyaltyFreeAudio()

    // Navigation (CapCut default: EDIT)
    private val _currentScreen = MutableStateFlow(StudioScreen.EDIT)
    val currentScreen: StateFlow<StudioScreen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: StudioScreen) {
        _currentScreen.value = screen
    }

    // Cloud Sync State (Đồng bộ đám mây CapCut)
    private val _isCloudSynced = MutableStateFlow(true)
    val isCloudSynced: StateFlow<Boolean> = _isCloudSynced.asStateFlow()

    private val _cloudSyncText = MutableStateFlow("Đám mây CapCut: Đã đồng bộ • 14.2 GB / 100 GB")
    val cloudSyncText: StateFlow<String> = _cloudSyncText.asStateFlow()

    fun triggerCloudSync() {
        viewModelScope.launch {
            _isCloudSynced.value = false
            _cloudSyncText.value = "Đang đồng bộ thay đổi lên đám mây..."
            delay(1200)
            _isCloudSynced.value = true
            _cloudSyncText.value = "Đám mây CapCut: Đã đồng bộ • 14.3 GB / 100 GB"
        }
    }

    // --- TIMELINE EDITOR STATE ---
    private val _activeProject = MutableStateFlow<Project?>(null)
    val activeProject: StateFlow<Project?> = _activeProject.asStateFlow()

    private val _timelineClips = MutableStateFlow<List<TimelineClip>>(emptyList())
    val timelineClips: StateFlow<List<TimelineClip>> = _timelineClips.asStateFlow()

    private val _keyframes = MutableStateFlow<List<Keyframe>>(emptyList())
    val keyframes: StateFlow<List<Keyframe>> = _keyframes.asStateFlow()

    private val _playheadSec = MutableStateFlow(0f)
    val playheadSec: StateFlow<Float> = _playheadSec.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _selectedClipId = MutableStateFlow<String?>(null)
    val selectedClipId: StateFlow<String?> = _selectedClipId.asStateFlow()

    private val _activeFilter = MutableStateFlow("Bình thường")
    val activeFilter: StateFlow<String> = _activeFilter.asStateFlow()

    private val _activeAspectRatio = MutableStateFlow("9:16")
    val activeAspectRatio: StateFlow<String> = _activeAspectRatio.asStateFlow()

    private var playbackJob: Job? = null

    init {
        loadDemoTimeline()
    }

    private fun loadDemoTimeline() {
        val defaultClips = listOf(
            TimelineClip(
                id = "clip_vid_1",
                trackType = TrackType.VIDEO,
                title = "Kling 3.0 Siêu xe Drift.mp4",
                startTimeSec = 0f,
                durationSec = 8.5f,
                speed = 1.0f,
                volume = 1.0f,
                colorHex = 0xFF2563EB
            ),
            TimelineClip(
                id = "clip_vid_2",
                trackType = TrackType.VIDEO,
                title = "VEO 3.1 Mưa Đêm Phố Neon.mp4",
                startTimeSec = 8.5f,
                durationSec = 9.5f,
                speed = 1.0f,
                volume = 1.0f,
                colorHex = 0xFF1D4ED8
            ),
            TimelineClip(
                id = "clip_aud_1",
                trackType = TrackType.AUDIO,
                title = "Nhạc Phonk Drift Bass (Đồng bộ)",
                startTimeSec = 0f,
                durationSec = 18.0f,
                volume = 0.85f,
                colorHex = 0xFF059669
            ),
            TimelineClip(
                id = "clip_txt_1",
                trackType = TrackType.TEXT,
                title = "Phụ đề Vietsub: 'Bứt phá giới hạn'",
                startTimeSec = 1.0f,
                durationSec = 4.0f,
                textContent = "⚡ BỨT PHÁ GIỚI HẠN ⚡",
                colorHex = 0xFFD97706
            ),
            TimelineClip(
                id = "clip_fx_1",
                trackType = TrackType.EFFECT,
                title = "Thu phóng 3D & Giật Glitch",
                startTimeSec = 6.0f,
                durationSec = 4.0f,
                effectName = "Glitch 3D",
                colorHex = 0xFF7C3AED
            )
        )
        _timelineClips.value = defaultClips
        _keyframes.value = listOf(
            Keyframe(timeSec = 0f, scale = 1.0f, positionX = 0f, positionY = 0f, rotation = 0f, opacity = 1.0f),
            Keyframe(timeSec = 4f, scale = 1.25f, positionX = 15f, positionY = -10f, rotation = 2.5f, opacity = 1.0f),
            Keyframe(timeSec = 8.5f, scale = 1.0f, positionX = 0f, positionY = 0f, rotation = 0f, opacity = 1.0f)
        )
    }

    fun openProjectInEditor(project: Project) {
        _activeProject.value = project
        _activeAspectRatio.value = project.aspectRatio
        _currentScreen.value = StudioScreen.EDITOR
    }

    fun createNewProject(aspectRatio: String = "9:16", title: String = "Dự án mới Hendy Vietsub") {
        viewModelScope.launch {
            val newProject = Project(
                title = title,
                aspectRatio = aspectRatio,
                durationSeconds = 15.0f,
                resolution = "4K",
                fps = 60,
                isCloudSynced = true,
                thumbnailKey = "banner",
                trackCount = 4
            )
            val newId = repository.insertProject(newProject)
            _activeProject.value = newProject.copy(id = newId)
            _activeAspectRatio.value = aspectRatio
            loadDemoTimeline()
            _currentScreen.value = StudioScreen.EDITOR
        }
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            repository.deleteProject(project)
        }
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            _isPlaying.value = false
            playbackJob?.cancel()
        } else {
            _isPlaying.value = true
            playbackJob = viewModelScope.launch {
                val totalDuration = _activeProject.value?.durationSeconds ?: 18f
                while (_isPlaying.value) {
                    delay(50)
                    val next = _playheadSec.value + 0.05f
                    if (next >= totalDuration) {
                        _playheadSec.value = 0f
                    } else {
                        _playheadSec.value = next
                    }
                }
            }
        }
    }

    fun seekTo(timeSec: Float) {
        val total = _activeProject.value?.durationSeconds ?: 18f
        _playheadSec.value = timeSec.coerceIn(0f, total)
    }

    fun selectClip(clipId: String?) {
        _selectedClipId.value = clipId
    }

    fun addClipToTrack(type: TrackType, title: String, durationSec: Float = 5.0f, text: String = "") {
        val current = _timelineClips.value.toMutableList()
        val start = _playheadSec.value
        val color = when (type) {
            TrackType.VIDEO -> 0xFF2563EB
            TrackType.AUDIO -> 0xFF059669
            TrackType.TEXT -> 0xFFD97706
            TrackType.EFFECT -> 0xFF7C3AED
            TrackType.STICKER -> 0xFFDB2777
        }
        val newClip = TimelineClip(
            id = UUID.randomUUID().toString(),
            trackType = type,
            title = title,
            startTimeSec = start,
            durationSec = durationSec,
            colorHex = color,
            textContent = text
        )
        current.add(newClip)
        _timelineClips.value = current
        triggerCloudSync()
    }

    fun deleteClip(clipId: String) {
        _timelineClips.value = _timelineClips.value.filterNot { it.id == clipId }
        if (_selectedClipId.value == clipId) {
            _selectedClipId.value = null
        }
        triggerCloudSync()
    }

    fun addKeyframeAtCurrentTime(scale: Float, posX: Float, posY: Float, rot: Float, opacity: Float) {
        val list = _keyframes.value.toMutableList()
        // Replace existing keyframe near this timestamp if any (< 0.1s)
        list.removeAll { kotlin.math.abs(it.timeSec - _playheadSec.value) < 0.1f }
        list.add(
            Keyframe(
                timeSec = _playheadSec.value,
                scale = scale,
                positionX = posX,
                positionY = posY,
                rotation = rot,
                opacity = opacity
            )
        )
        list.sortBy { it.timeSec }
        _keyframes.value = list
    }

    fun removeKeyframe(id: String) {
        _keyframes.value = _keyframes.value.filterNot { it.id == id }
    }

    fun setFilter(filter: String) {
        _activeFilter.value = filter
    }

    fun setAspectRatio(ratio: String) {
        _activeAspectRatio.value = ratio
    }

    fun applyTemplateToProject(template: AITemplate) {
        viewModelScope.launch {
            createNewProject(
                aspectRatio = template.aspectRatio,
                title = "${template.title} (AI Sync)"
            )
            setFilter(if (template.id == "tmpl_4") "VHS Retro" else "Normal")
            _currentScreen.value = StudioScreen.EDITOR
        }
    }

    fun addRecordedFootageToStudio(videoTitle: String, durationSec: Float, asNewProject: Boolean = false) {
        viewModelScope.launch {
            if (asNewProject || _activeProject.value == null) {
                createNewProject("9:16", videoTitle)
                addClipToTrack(TrackType.VIDEO, videoTitle, durationSec)
            } else {
                addClipToTrack(TrackType.VIDEO, videoTitle, durationSec)
                _currentScreen.value = StudioScreen.EDITOR
            }
        }
    }

    // --- AI VIDEO GENERATION STATE ---
    val videoModels = listOf("Kling 3.0 Ultra", "Kling 2.6 Nghệ thuật", "VEO 3.1 Khớp âm thanh", "VEO 3.0 Điện ảnh", "Sora 2 Mượt mà", "Seedance 2.5 Tiết tấu")
    private val _selectedVideoModel = MutableStateFlow("Kling 3.0 Ultra")
    val selectedVideoModel: StateFlow<String> = _selectedVideoModel.asStateFlow()

    private val _videoPrompt = MutableStateFlow("Siêu xe điện tương lai drift trên đại lộ Tokyo dưới trời mưa neon phản chiếu điện ảnh và khói nước chân thực")
    val videoPrompt: StateFlow<String> = _videoPrompt.asStateFlow()

    private val _videoCameraMotion = MutableStateFlow("Quay vòng trái")
    val videoCameraMotion: StateFlow<String> = _videoCameraMotion.asStateFlow()

    private val _videoMotionDynamics = MutableStateFlow(7.5f)
    val videoMotionDynamics: StateFlow<Float> = _videoMotionDynamics.asStateFlow()

    private val _videoDurationSec = MutableStateFlow(10)
    val videoDurationSec: StateFlow<Int> = _videoDurationSec.asStateFlow()

    private val _videoAudioSyncEnabled = MutableStateFlow(true)
    val videoAudioSyncEnabled: StateFlow<Boolean> = _videoAudioSyncEnabled.asStateFlow()

    private val _isVideoGenerating = MutableStateFlow(false)
    val isVideoGenerating: StateFlow<Boolean> = _isVideoGenerating.asStateFlow()

    private val _videoGenProgress = MutableStateFlow(0f)
    val videoGenProgress: StateFlow<Float> = _videoGenProgress.asStateFlow()

    private val _videoGenStatusText = MutableStateFlow("")
    val videoGenStatusText: StateFlow<String> = _videoGenStatusText.asStateFlow()

    fun setVideoModel(model: String) { _selectedVideoModel.value = model }
    fun setVideoPrompt(p: String) { _videoPrompt.value = p }
    fun setCameraMotion(motion: String) { _videoCameraMotion.value = motion }
    fun setMotionDynamics(dyn: Float) { _videoMotionDynamics.value = dyn }
    fun setVideoDuration(d: Int) { _videoDurationSec.value = d }
    fun toggleVideoAudioSync() { _videoAudioSyncEnabled.value = !_videoAudioSyncEnabled.value }

    fun generateAIVideo() {
        if (_isVideoGenerating.value) return
        viewModelScope.launch {
            _isVideoGenerating.value = true
            _videoGenProgress.value = 0.05f
            _videoGenStatusText.value = "Đang khởi tạo mô hình khuếch tán ${_selectedVideoModel.value}..."
            delay(600)
            _videoGenProgress.value = 0.25f
            _videoGenStatusText.value = "Đang tổng hợp chuyển động không gian và góc quay: ${_videoCameraMotion.value}..."
            delay(800)
            _videoGenProgress.value = 0.55f
            _videoGenStatusText.value = if (_videoAudioSyncEnabled.value) "Đang đồng bộ hóa âm thanh không gian theo hình ảnh..." else "Đang kết xuất khung hình 4K..."
            delay(800)
            _videoGenProgress.value = 0.85f
            _videoGenStatusText.value = "Đang áp dụng bộ khử nhiễu siêu phân giải và làm mượt chuyển động..."
            delay(600)
            _videoGenProgress.value = 1.0f
            _videoGenStatusText.value = "Hoàn thành tạo video AI!"

            val item = AIGenerationItem(
                type = when {
                    _selectedVideoModel.value.contains("Kling 3") -> "KLING_3_0"
                    _selectedVideoModel.value.contains("Kling 2") -> "KLING_2_6"
                    _selectedVideoModel.value.contains("VEO 3.1") -> "VEO_3_1"
                    _selectedVideoModel.value.contains("VEO 3.0") -> "VEO_3_0"
                    _selectedVideoModel.value.contains("Sora") -> "SORA_2"
                    else -> "SEEDANCE_2_5"
                },
                modelName = _selectedVideoModel.value,
                prompt = _videoPrompt.value,
                aspectRatio = _activeAspectRatio.value,
                status = "Hoàn thành",
                resultTitle = "${_selectedVideoModel.value} • Video ${_videoDurationSec.value}s",
                resultPreview = "${_videoDurationSec.value}s • 4K 60fps • ${_videoCameraMotion.value} • Âm thanh: ${if (_videoAudioSyncEnabled.value) "Đã đồng bộ" else "Tắt tiếng"}"
            )
            repository.insertGeneration(item)
            delay(400)
            _isVideoGenerating.value = false
        }
    }

    // Tiện ích video: Xóa nền, Tự động định khung, Nâng cấp 4K, Dịch Vietsub & Lồng tiếng, Phụ đề tự động
    private val _videoUtilStatus = MutableStateFlow<String?>(null)
    val videoUtilStatus: StateFlow<String?> = _videoUtilStatus.asStateFlow()

    fun runVideoBackgroundRemover() {
        viewModelScope.launch {
            _videoUtilStatus.value = "Đang phân đoạn chủ thể con người không cần phông xanh (Mặt nạ Alpha)..."
            delay(1200)
            _videoUtilStatus.value = "Chủ thể đã được tách sạch sẽ! Sẵn sàng xuất kênh Alpha trong suốt."
            delay(2000)
            _videoUtilStatus.value = null
        }
    }

    fun runAutoReframe(targetRatio: String) {
        viewModelScope.launch {
            _videoUtilStatus.value = "Đang phân tích điểm nhìn khuôn mặt để tự động định khung sang $targetRatio..."
            delay(1200)
            _videoUtilStatus.value = "Đã chuyển đổi sang định dạng $targetRatio, theo dõi chủ thể ở trung tâm!"
            _activeAspectRatio.value = targetRatio
            delay(2000)
            _videoUtilStatus.value = null
        }
    }

    fun runVideoUpscaler() {
        viewModelScope.launch {
            _videoUtilStatus.value = "Đang nâng cấp video lên chuẩn 4K 60fps với AI phục chế chi tiết..."
            delay(1400)
            _videoUtilStatus.value = "Video đã được nâng cấp lên 4K 60fps siêu nét (3840x2160)!"
            delay(2000)
            _videoUtilStatus.value = null
        }
    }

    fun runVideoTranslation(targetLang: String) {
        viewModelScope.launch {
            _videoUtilStatus.value = "Đang trích xuất giọng nói, dịch sang $targetLang và tổng hợp khớp chuyển động môi..."
            delay(1500)
            _videoUtilStatus.value = "Dịch & Lồng tiếng Vietsub thành công! Giọng nói tự nhiên và khẩu hình môi hoàn toàn khớp."
            delay(2500)
            _videoUtilStatus.value = null
        }
    }

    fun runAutoCaptions() {
        viewModelScope.launch {
            _videoUtilStatus.value = "Đang nhận diện giọng nói tiếng Việt với độ chính xác cao 99.4%..."
            delay(1100)
            addClipToTrack(TrackType.TEXT, "Phụ đề Vietsub: 'Sáng tạo không giới hạn'", 4.5f, "✨ SÁNG TẠO KHÔNG GIỚI HẠN ✨")
            _videoUtilStatus.value = "Đã tạo 14 phụ đề động tiếng Việt đưa trực tiếp vào luồng chữ!"
            delay(2000)
            _videoUtilStatus.value = null
        }
    }

    // --- AI IMAGE & DESIGN STATE ---
    private val _imagePrompt = MutableStateFlow("Chai nước hoa thủy tinh mờ sang trọng trên bệ đá cẩm thạch đen với sóng nước lan tỏa và ánh đèn neon phản chiếu")
    val imagePrompt: StateFlow<String> = _imagePrompt.asStateFlow()

    private val _imageModel = MutableStateFlow("Seedream 5.0")
    val imageModel: StateFlow<String> = _imageModel.asStateFlow()

    private val _imageStyle = MutableStateFlow("Chân thực")
    val imageStyle: StateFlow<String> = _imageStyle.asStateFlow()

    private val _isImageProcessing = MutableStateFlow(false)
    val isImageProcessing: StateFlow<Boolean> = _isImageProcessing.asStateFlow()

    private val _imageResultStatus = MutableStateFlow<String?>(null)
    val imageResultStatus: StateFlow<String?> = _imageResultStatus.asStateFlow()

    fun setImagePrompt(p: String) { _imagePrompt.value = p }
    fun setImageModel(m: String) { _imageModel.value = m }
    fun setImageStyle(s: String) { _imageStyle.value = s }

    fun generateTextImage() {
        if (_isImageProcessing.value) return
        viewModelScope.launch {
            _isImageProcessing.value = true
            _imageResultStatus.value = "Generating with ${_imageModel.value} [${_imageStyle.value}]..."
            delay(1400)
            _imageResultStatus.value = "Image generated successfully in 4K resolution!"
            _isImageProcessing.value = false
        }
    }

    fun runImageToImage(style: String) {
        viewModelScope.launch {
            _isImageProcessing.value = true
            _imageResultStatus.value = "Transforming image style into $style..."
            delay(1300)
            _imageResultStatus.value = "Image transformed into $style with preserved geometry!"
            _isImageProcessing.value = false
        }
    }

    fun runImageUpscaler() {
        viewModelScope.launch {
            _isImageProcessing.value = true
            _imageResultStatus.value = "Sharpening texture & upscaling to 4K UHD..."
            delay(1100)
            _imageResultStatus.value = "Upscaled to 4096x4096 4K with deep texture restoration!"
            _isImageProcessing.value = false
        }
    }

    fun runOldPhotoRestoration() {
        viewModelScope.launch {
            _isImageProcessing.value = true
            _imageResultStatus.value = "Scanning scratches, creases and tears for neural infill..."
            delay(1200)
            _imageResultStatus.value = "Photo restored! All scratches removed and facial details sharpened."
            _isImageProcessing.value = false
        }
    }

    fun runPhotoColorizer() {
        viewModelScope.launch {
            _isImageProcessing.value = true
            _imageResultStatus.value = "Analyzing grayscale luminance and synthesizing true-to-life colors..."
            delay(1200)
            _imageResultStatus.value = "Colorized! Natural skin tones, lush environments, and historical accuracy."
            _isImageProcessing.value = false
        }
    }

    fun runColorCorrection() {
        viewModelScope.launch {
            _isImageProcessing.value = true
            _imageResultStatus.value = "Balancing contrast, dynamic range, and vibrant saturation..."
            delay(900)
            _imageResultStatus.value = "AI Color Correction applied: balanced shadows, vivid highlights."
            _isImageProcessing.value = false
        }
    }

    fun runImageBgRemoval() {
        viewModelScope.launch {
            _isImageProcessing.value = true
            _imageResultStatus.value = "Đang tách chủ thể với độ chính xác cao chỉ với 1 chạm..."
            delay(1000)
            _imageResultStatus.value = "Đã xóa nền ảnh thành công! Sẵn sàng xuất ảnh trong suốt hoặc ghép nền mới."
            _isImageProcessing.value = false
        }
    }

    fun runAiBgGeneration(backdropTheme: String) {
        viewModelScope.launch {
            _isImageProcessing.value = true
            _imageResultStatus.value = "Đang tạo phông nền AI $backdropTheme với ánh sáng và bóng đổ hòa hợp..."
            delay(1300)
            _imageResultStatus.value = "Đã ghép nền AI $backdropTheme chân thực với bóng đổ tiếp xúc tự nhiên!"
            _isImageProcessing.value = false
        }
    }

    fun runImageResizer(targetPlatform: String) {
        viewModelScope.launch {
            _isImageProcessing.value = true
            _imageResultStatus.value = "Đang vẽ bù viền thông minh và tối ưu kích thước cho $targetPlatform..."
            delay(900)
            _imageResultStatus.value = "Đã tối ưu hóa kích thước cho $targetPlatform với phần viền mở rộng mượt mà!"
            _isImageProcessing.value = false
        }
    }

    fun runObjectRemover() {
        viewModelScope.launch {
            _isImageProcessing.value = true
            _imageResultStatus.value = "Đang xóa vùng quét bằng cọ vẽ và tái tạo chi tiết nền xung quanh..."
            delay(1100)
            _imageResultStatus.value = "Đã xóa sạch vật thể/chữ không mong muốn mà không để lại vết mờ!"
            _isImageProcessing.value = false
        }
    }

    fun runAiModelFit() {
        viewModelScope.launch {
            _isImageProcessing.value = true
            _imageResultStatus.value = "Đang ướm trang phục lookbook lên người mẫu AI ảo với hiệu ứng nếp nhăn vải chân thực..."
            delay(1400)
            _imageResultStatus.value = "Đã tạo ảnh người mẫu ảo lookbook hoàn tất với ánh sáng đồng bộ tuyệt đối!"
            _isImageProcessing.value = false
        }
    }

    fun runProductStudio() {
        viewModelScope.launch {
            _isImageProcessing.value = true
            _imageResultStatus.value = "Đang đặt sản phẩm vào không gian studio quảng cáo với đá cẩm thạch và mặt nước phản chiếu..."
            delay(1300)
            _imageResultStatus.value = "Đã tạo ảnh quảng cáo studio thương mại với ánh sáng softbox chuyên nghiệp!"
            _isImageProcessing.value = false
        }
    }

    fun runAiDesignLayout() {
        viewModelScope.launch {
            _isImageProcessing.value = true
            _imageResultStatus.value = "Đang tự động sắp xếp bố cục chữ, tiêu đề phân cấp và huy hiệu ưu đãi..."
            delay(1200)
            _imageResultStatus.value = "Đã tạo thiết kế poster quảng cáo với các khối chữ và huy hiệu có thể chỉnh sửa!"
            _isImageProcessing.value = false
        }
    }

    // --- AI AUDIO & VOICE STUDIO STATE ---
    private val _ttsInputText = MutableStateFlow("Chào mừng bạn đến với Hendy Vietsub – Ứng dụng sáng tạo video, dịch phụ đề tự động và biên tập đa phương tiện đỉnh cao.")
    val ttsInputText: StateFlow<String> = _ttsInputText.asStateFlow()

    private val _selectedVoice = MutableStateFlow(voiceProfiles.first())
    val selectedVoice: StateFlow<VoiceProfile> = _selectedVoice.asStateFlow()

    private val _ttsSpeed = MutableStateFlow(1.0f)
    val ttsSpeed: StateFlow<Float> = _ttsSpeed.asStateFlow()

    private val _ttsPitch = MutableStateFlow(1.0f)
    val ttsPitch: StateFlow<Float> = _ttsPitch.asStateFlow()

    private val _ttsEmotion = MutableStateFlow("Điện ảnh truyền cảm")
    val ttsEmotion: StateFlow<String> = _ttsEmotion.asStateFlow()

    private val _isAudioProcessing = MutableStateFlow(false)
    val isAudioProcessing: StateFlow<Boolean> = _isAudioProcessing.asStateFlow()

    private val _audioResultStatus = MutableStateFlow<String?>(null)
    val audioResultStatus: StateFlow<String?> = _audioResultStatus.asStateFlow()

    // Stem Isolation sliders
    private val _vocalIsolationLevel = MutableStateFlow(90f)
    val vocalIsolationLevel: StateFlow<Float> = _vocalIsolationLevel.asStateFlow()

    private val _bgmLevel = MutableStateFlow(30f)
    val bgmLevel: StateFlow<Float> = _bgmLevel.asStateFlow()

    private val _noiseReductionLevel = MutableStateFlow(85f)
    val noiseReductionLevel: StateFlow<Float> = _noiseReductionLevel.asStateFlow()

    fun setTtsInputText(t: String) { _ttsInputText.value = t }
    fun selectVoice(v: VoiceProfile) { _selectedVoice.value = v }
    fun setTtsSpeed(s: Float) { _ttsSpeed.value = s }
    fun setTtsPitch(p: Float) { _ttsPitch.value = p }
    fun setTtsEmotion(e: String) { _ttsEmotion.value = e }
    fun setVocalIsolation(v: Float) { _vocalIsolationLevel.value = v }
    fun setBgmLevel(b: Float) { _bgmLevel.value = b }
    fun setNoiseReduction(n: Float) { _noiseReductionLevel.value = n }

    fun generateSpeechTTS() {
        viewModelScope.launch {
            _isAudioProcessing.value = true
            _audioResultStatus.value = "Đang tổng hợp giọng nói với ${_selectedVoice.value.name} [${_ttsEmotion.value}]..."
            delay(1200)
            _audioResultStatus.value = "Đã chuyển văn bản thành giọng nói tự nhiên với nhịp điệu và ngữ điệu biểu cảm!"
            _isAudioProcessing.value = false
        }
    }

    fun runSoundToText() {
        viewModelScope.launch {
            _isAudioProcessing.value = true
            _audioResultStatus.value = "Đang trích xuất lời thoại từ âm thanh..."
            delay(1100)
            _audioResultStatus.value = "Đã trích xuất thành công 142 từ tiếng Việt, độ chính xác 99.4% kèm mốc thời gian chi tiết."
            _isAudioProcessing.value = false
        }
    }

    fun runAudioStemSeparation() {
        viewModelScope.launch {
            _isAudioProcessing.value = true
            _audioResultStatus.value = "Đang tách các dải âm thanh: Giọng hát (Vocal), Nhạc nền (BGM), Tiếng ồn môi trường..."
            delay(1300)
            _audioResultStatus.value = "Đã tách 3 dải âm thanh! Tăng âm lượng giọng hát, giảm nhạc nền và lọc sạch tiếng ồn."
            _isAudioProcessing.value = false
        }
    }

    // --- AD SCRIPT WRITER STATE (VIẾT KỊCH BẢN QUẢNG CÁO TIẾNG VIỆT) ---
    private val _productName = MutableStateFlow("Bình Giữ Nhiệt Thông Minh OmniGlow")
    val productName: StateFlow<String> = _productName.asStateFlow()

    private val _targetAudience = MutableStateFlow("Giới trẻ Gen Z, người tập gym & sáng tạo nội dung")
    val targetAudience: StateFlow<String> = _targetAudience.asStateFlow()

    private val _scriptTone = MutableStateFlow("Mở đầu kịch tính & Khẩn cấp")
    val scriptTone: StateFlow<String> = _scriptTone.asStateFlow()

    private val _scriptFormat = MutableStateFlow("TikTok / Reels 30s")
    val scriptFormat: StateFlow<String> = _scriptFormat.asStateFlow()

    private val _generatedScript = MutableStateFlow<AdScriptResult?>(
        AdScriptResult(
            hook = "DỪNG LẠI NGAY! Đừng uống nước từ chiếc bình cũ bám vi khuẩn nữa. Đây là chiếc bình tự làm sạch trong 60 giây.",
            problem = "Bạn tập luyện chăm chỉ mỗi ngày nhưng chiếc bình nước thông thường lại bốc mùi khó chịu chỉ sau 2 lần dùng.",
            agitation = "Điều đó khiến bạn phải liên tục mua bình mới và vô tình nạp hàng triệu vi khuẩn vào cơ thể mỗi ngày.",
            solution = "Bình OmniGlow tích hợp công nghệ khử khuẩn tia UV-C và vòng phát sáng OLED theo dõi lượng nước cần uống.",
            callToAction = "Nhấn ngay vào liên kết bên dưới trước khi đợt mở bán giảm giá 40% kết thúc trong hôm nay!",
            visualCues = listOf(
                "[0:00 - 0:03] Cận cảnh chiếc bình cũ bẩn kèm hiệu ứng cảnh báo đỏ giật giật",
                "[0:04 - 0:09] Chuyển cảnh lia nhanh sang chiếc bình OmniGlow rực rỡ mở nắp với hơi sương mát lạnh",
                "[0:10 - 0:18] Vòng sáng OLED màu xanh ngọc phát sáng khi nước đá mát lạnh được rót vào",
                "[0:19 - 0:25] Huy hiệu chữ động: 'Khử khuẩn 99.9% • Tự làm sạch • Giữ nhiệt 48h'",
                "[0:26 - 0:30] Sản phẩm xoay 360 độ kèm banner mã giảm giá 'HENDY50' và mũi tên chỉ vào giỏ hàng"
            ),
            estimatedDuration = "30s"
        )
    )
    val generatedScript: StateFlow<AdScriptResult?> = _generatedScript.asStateFlow()

    private val _isScriptGenerating = MutableStateFlow(false)
    val isScriptGenerating: StateFlow<Boolean> = _isScriptGenerating.asStateFlow()

    fun setProductName(name: String) { _productName.value = name }
    fun setTargetAudience(aud: String) { _targetAudience.value = aud }
    fun setScriptTone(t: String) { _scriptTone.value = t }
    fun setScriptFormat(f: String) { _scriptFormat.value = f }

    fun generateAdScript() {
        viewModelScope.launch {
            _isScriptGenerating.value = true
            delay(1100)
            _generatedScript.value = AdScriptResult(
                hook = "Chờ đã—trước khi bạn tốn thêm tiền cho những món đồ kém chất lượng, hãy xem hết video này.",
                problem = "Rất nhiều người gặp khó khăn vì công cụ hiện tại không bắt kịp tốc độ sáng tạo và tham vọng của bạn.",
                agitation = "Bạn lãng phí hàng giờ chỉnh sửa thủ công và thử lại liên tục thay vì tạo ra những tác phẩm triệu view.",
                solution = "${_productName.value} mang lại lợi thế cạnh tranh vượt trội được thiết kế riêng cho ${_targetAudience.value}.",
                callToAction = "Nhận ngay ưu đãi trải nghiệm độc quyền ngay bây giờ trước khi giá trở về mức ban đầu!",
                visualCues = listOf(
                    "[0:00 - 0:04] Thu phóng nhanh vào biểu cảm bất lực kèm âm thanh Glitch giật giật",
                    "[0:05 - 0:12] Xuất hiện sản phẩm chủ đạo với hiệu ứng quét ánh sáng hào quang",
                    "[0:13 - 0:22] So sánh song song hai màn hình cho thấy tốc độ nhanh gấp 3 lần",
                    "[0:23 - 0:30] Thẻ kêu gọi hành động chuyển động lớn kèm mã ưu đãi 'VIETSUB50'"
                ),
                estimatedDuration = _scriptFormat.value
            )
            _isScriptGenerating.value = false
        }
    }
}
