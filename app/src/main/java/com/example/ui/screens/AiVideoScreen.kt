package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.AspectRatioSelector
import com.example.ui.components.ModelBadge
import com.example.ui.components.ParameterSlider
import com.example.ui.components.StatusFeedbackBanner
import com.example.ui.components.StudioHeader
import com.example.ui.theme.HyperViolet
import com.example.ui.theme.MintGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioWhite
import com.example.ui.theme.VividMagenta
import com.example.viewmodel.StudioViewModel

@Composable
fun AiVideoScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "Kling & VEO Suite",
        "BG Remover",
        "Auto Reframe",
        "4K Upscaler",
        "Translation & Dub",
        "Auto Captions"
    )

    val selectedModel by viewModel.selectedVideoModel.collectAsState()
    val videoPrompt by viewModel.videoPrompt.collectAsState()
    val cameraMotion by viewModel.videoCameraMotion.collectAsState()
    val motionDynamics by viewModel.videoMotionDynamics.collectAsState()
    val durationSec by viewModel.videoDurationSec.collectAsState()
    val audioSyncEnabled by viewModel.videoAudioSyncEnabled.collectAsState()
    val isGenerating by viewModel.isVideoGenerating.collectAsState()
    val genProgress by viewModel.videoGenProgress.collectAsState()
    val genStatusText by viewModel.videoGenStatusText.collectAsState()
    val activeRatio by viewModel.activeAspectRatio.collectAsState()
    val videoUtilStatus by viewModel.videoUtilStatus.collectAsState()

    val cameraPresets = listOf("Orbit Left", "Push In", "Dolly Zoom", "Drone Sweep", "Static Lock")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            StudioHeader(
                title = "AI Video Generation",
                subtitle = "Kling 3.0 • VEO 3.1 • Sora 2 • Seedance 2.5",
                isCloudSynced = true
            )
        }

        // Subtool Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = StudioSurface,
                contentColor = NeonCyan,
                edgePadding = 16.dp,
                divider = {},
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) NeonCyan else StudioTextMuted
                            )
                        },
                        modifier = Modifier.testTag("video_tab_$index")
                    )
                }
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // Kling & VEO Main Suite
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "Neural Video Model",
                            color = StudioWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            viewModel.videoModels.forEach { model ->
                                ModelBadge(
                                    modelName = model,
                                    isSelected = selectedModel == model,
                                    onClick = { viewModel.setVideoModel(model) },
                                    badgeText = when {
                                        model.contains("3.0") -> "v3.0"
                                        model.contains("3.1") -> "AUDIO"
                                        model.contains("Sora") -> "HQ"
                                        else -> "Fast"
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Model feature banner
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = StudioSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (selectedModel.contains("VEO")) Icons.Default.SurroundSound else Icons.Default.Movie,
                                    contentDescription = "Model Feature",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when {
                                        selectedModel.contains("Kling 3.0") -> "Kling 3.0: High temporal stability, cinematic physics & fluid motion dynamics."
                                        selectedModel.contains("Kling 2.6") -> "Kling 2.6: Expressive artistic text & image-to-video rendering."
                                        selectedModel.contains("VEO 3.1") -> "VEO 3.1: Ultra cinematic generator with procedural synchronized audio synthesis."
                                        selectedModel.contains("Sora 2") -> "Sora 2: Long sequence multi-shot coherence with physical world simulation."
                                        else -> "Seedance 2.5: High speed action sequences with dynamic beat pacing."
                                    },
                                    color = StudioWhite,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Prompt input
                        Text(
                            text = "Cinematic Scene Description",
                            color = StudioWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = videoPrompt,
                            onValueChange = { viewModel.setVideoPrompt(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("video_prompt_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = StudioCardBorder,
                                focusedContainerColor = StudioSurfaceVariant,
                                unfocusedContainerColor = StudioSurfaceVariant,
                                focusedTextColor = StudioWhite,
                                unfocusedTextColor = StudioWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 3,
                            maxLines = 5
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Camera Motion presets
                        Text(
                            text = "Camera Trajectory",
                            color = StudioWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            cameraPresets.forEach { preset ->
                                val isSelected = cameraMotion == preset
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) HyperViolet.copy(alpha = 0.3f) else StudioSurfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) NeonCyan else StudioCardBorder
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { viewModel.setCameraMotion(preset) }
                                        .testTag("camera_$preset")
                                ) {
                                    Text(
                                        text = preset,
                                        color = if (isSelected) NeonCyan else StudioTextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Aspect ratio selector
                        AspectRatioSelector(
                            selectedRatio = activeRatio,
                            onSelectRatio = { viewModel.setAspectRatio(it) }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Motion Dynamics slider
                        ParameterSlider(
                            label = "Motion Dynamics & Speed",
                            value = motionDynamics,
                            onValueChange = { viewModel.setMotionDynamics(it) },
                            valueRange = 1f..10f,
                            displayValue = String.format("%.1f", motionDynamics),
                            unit = "/ 10",
                            testTag = "slider_motion_dynamics"
                        )

                        // VEO Smart Audio Sync toggle
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = StudioSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.SurroundSound,
                                        contentDescription = "Audio Sync",
                                        tint = if (audioSyncEnabled) NeonCyan else StudioTextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Smart Audio Synchronization",
                                            color = StudioWhite,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Auto-generates matched acoustics & spatial sound effects",
                                            color = StudioTextMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                Switch(
                                    checked = audioSyncEnabled,
                                    onCheckedChange = { viewModel.toggleVideoAudioSync() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = NeonCyan,
                                        checkedTrackColor = HyperViolet
                                    ),
                                    modifier = Modifier.testTag("toggle_audio_sync")
                                )
                            }
                        }

                        // Generation progress or button
                        if (isGenerating) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            ) {
                                Text(
                                    text = genStatusText,
                                    color = NeonCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { genProgress },
                                    color = NeonCyan,
                                    trackColor = StudioSurfaceElevated,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                )
                            }
                        } else {
                            Button(
                                onClick = { viewModel.generateAIVideo() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonCyan,
                                    contentColor = StudioBackground
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .padding(top = 8.dp)
                                    .testTag("generate_video_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Generate",
                                    tint = StudioBackground,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Generate with $selectedModel (4K 60fps)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Generated preview card
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Latest Rendered Video",
                            color = StudioWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, StudioCardBorder, RoundedCornerShape(14.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.hero_studio_banner),
                                contentDescription = "Generated Video",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.Black.copy(alpha = 0.75f),
                                modifier = Modifier
                                    .padding(10.dp)
                                    .align(Alignment.TopStart)
                            ) {
                                Text(
                                    text = "$selectedModel • 4K 60fps",
                                    color = NeonCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                            Button(
                                onClick = {
                                    viewModel.addClipToTrack(
                                        com.example.model.TrackType.VIDEO,
                                        "$selectedModel Render.mp4",
                                        durationSec.toFloat()
                                    )
                                    viewModel.navigateTo(com.example.viewmodel.StudioScreen.EDITOR)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = HyperViolet,
                                    contentColor = StudioWhite
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .padding(10.dp)
                                    .align(Alignment.BottomEnd)
                                    .testTag("add_video_to_timeline")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Edit in Timeline",
                                    tint = StudioWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit in Timeline", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            1 -> {
                // Video Background Remover
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Video Background Remover (Zero Green Screen)",
                            color = StudioWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "High-precision neural segmentation isolates actors or subjects with clean edge matting without green screen.",
                            color = StudioTextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        // Before / After Preview Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.lookbook_ai_model),
                                contentDescription = "Subject Isolation",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.Black.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "Alpha Mask: Subject Extracted",
                                    color = MintGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        StatusFeedbackBanner(statusText = videoUtilStatus)

                        Button(
                            onClick = { viewModel.runVideoBackgroundRemover() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonCyan,
                                contentColor = StudioBackground
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("run_video_bg_remove")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = "Remove BG",
                                tint = StudioBackground,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Extract Subject & Remove Video BG", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
            2 -> {
                // Auto Reframe
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Auto Reframe with Smart Subject Tracking",
                            color = StudioWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Converts widescreen horizontal 16:9 videos into 9:16 vertical TikTok/Reels format while keeping the subject dynamically centered.",
                            color = StudioTextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        AspectRatioSelector(
                            selectedRatio = activeRatio,
                            onSelectRatio = { viewModel.setAspectRatio(it) }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        StatusFeedbackBanner(statusText = videoUtilStatus)

                        Button(
                            onClick = { viewModel.runAutoReframe(activeRatio) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonCyan,
                                contentColor = StudioBackground
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("run_auto_reframe")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CenterFocusStrong,
                                contentDescription = "Reframe",
                                tint = StudioBackground,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Auto Reframe Video to $activeRatio", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
            3 -> {
                // Video Upscaler
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Video Upscaler & Detail Restoration",
                            color = StudioWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Deep neural super-resolution restores blurred motion, reduces compression artifacts, and upscales footage up to 4K 60fps.",
                            color = StudioTextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = StudioSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Enhancement Targets:", color = StudioWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("• Target Resolution: 3840 x 2160 UHD (4K)", color = NeonCyan, fontSize = 12.sp)
                                Text("• Temporal Frame Rate: 60 fps Smooth Motion Flow", color = NeonCyan, fontSize = 12.sp)
                                Text("• Texture Synthesis: Neural edge sharpening & denoising", color = NeonCyan, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        StatusFeedbackBanner(statusText = videoUtilStatus)

                        Button(
                            onClick = { viewModel.runVideoUpscaler() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonCyan,
                                contentColor = StudioBackground
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("run_video_upscaler")
                        ) {
                            Icon(
                                imageVector = Icons.Default.HighQuality,
                                contentDescription = "Upscale",
                                tint = StudioBackground,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Upscale Video to 4K 60fps", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
            4 -> {
                // Video Translation & Lip-sync
                item {
                    var selectedLang by remember { mutableStateOf("Spanish") }
                    val languages = listOf("Spanish", "Japanese", "French", "German", "Mandarin", "Portuguese")

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Video Translation & Neural Lip-Sync",
                            color = StudioWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Translates dialogue into other languages with automatic voice cloning and accurate lip movement synthesis.",
                            color = StudioTextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        Text("Target Dubbing Language", color = StudioWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            languages.forEach { lang ->
                                val isSelected = selectedLang == lang
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) HyperViolet.copy(alpha = 0.3f) else StudioSurfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) NeonCyan else StudioCardBorder
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedLang = lang }
                                        .testTag("lang_$lang")
                                ) {
                                    Text(
                                        text = lang,
                                        color = if (isSelected) NeonCyan else StudioTextMuted,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        StatusFeedbackBanner(statusText = videoUtilStatus)

                        Button(
                            onClick = { viewModel.runVideoTranslation(selectedLang) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonCyan,
                                contentColor = StudioBackground
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("run_video_translation")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = "Translate",
                                tint = StudioBackground,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Translate & Lip-Sync to $selectedLang", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
            5 -> {
                // Auto Captions
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Auto Captions & Kinetic Subtitles",
                            color = StudioWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Auto-detects spoken audio with 99.4% speech recognition accuracy and generates synchronized animated subtitles directly on your timeline.",
                            color = StudioTextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        // Subtitle Style Preview
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = StudioSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("SAMPLE ANIMATED CAPTION", color = StudioTextMuted, fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "✨ CREATE WITHOUT LIMITS ✨",
                                    color = NeonCyan,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Kinetic Bounce • Word-by-Word Highlight", color = HyperViolet, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        StatusFeedbackBanner(statusText = videoUtilStatus)

                        Button(
                            onClick = { viewModel.runAutoCaptions() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonCyan,
                                contentColor = StudioBackground
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("run_auto_captions")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ClosedCaption,
                                contentDescription = "Captions",
                                tint = StudioBackground,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Auto Captions to Timeline", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
