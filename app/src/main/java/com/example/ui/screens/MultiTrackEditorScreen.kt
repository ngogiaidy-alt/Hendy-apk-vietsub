package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Keyframe
import com.example.model.TimelineClip
import com.example.model.TrackType
import com.example.ui.components.ExportDialog
import com.example.ui.components.ParameterSlider
import com.example.ui.theme.AmberGlow
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
import com.example.ui.theme.TrackAudioColor
import com.example.ui.theme.TrackEffectColor
import com.example.ui.theme.TrackTextColor
import com.example.ui.theme.TrackVideoColor
import com.example.ui.theme.VividMagenta
import com.example.viewmodel.StudioScreen
import com.example.viewmodel.StudioViewModel
import kotlin.math.roundToInt

@Composable
fun MultiTrackEditorScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val project by viewModel.activeProject.collectAsState()
    val clips by viewModel.timelineClips.collectAsState()
    val keyframes by viewModel.keyframes.collectAsState()
    val playheadSec by viewModel.playheadSec.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val selectedClipId by viewModel.selectedClipId.collectAsState()
    val activeFilter by viewModel.activeFilter.collectAsState()
    val activeAspectRatio by viewModel.activeAspectRatio.collectAsState()
    val isCloudSynced by viewModel.isCloudSynced.collectAsState()

    var showExportDialog by remember { mutableStateOf(false) }
    var showKeyframePanel by remember { mutableStateOf(false) }
    var showFilterPicker by remember { mutableStateOf(false) }

    // Keyframe slider values
    var kfScale by remember { mutableFloatStateOf(1.0f) }
    var kfPosX by remember { mutableFloatStateOf(0f) }
    var kfPosY by remember { mutableFloatStateOf(0f) }
    var kfRot by remember { mutableFloatStateOf(0f) }
    var kfOpacity by remember { mutableFloatStateOf(1.0f) }

    // Interpolate keyframe transform for preview at playheadSec
    val currentTransform = remember(playheadSec, keyframes) {
        interpolateKeyframe(playheadSec, keyframes)
    }

    // Determine active subtitle text at playhead
    val activeText = remember(playheadSec, clips) {
        clips.firstOrNull { it.trackType == TrackType.TEXT && playheadSec >= it.startTimeSec && playheadSec <= (it.startTimeSec + it.durationSec) }?.textContent
    }

    val totalDuration = project?.durationSeconds ?: 18.0f

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.navigateTo(StudioScreen.EDIT) },
                    modifier = Modifier.testTag("editor_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = StudioWhite
                    )
                }
                Column {
                    Text(
                        text = project?.title ?: "Cyberpunk Neo Tokyo Cinematic",
                        color = StudioWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(180.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = HyperViolet.copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = activeAspectRatio,
                                color = NeonCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Text(
                            text = if (isCloudSynced) "Auto-saved to Cloud" else "Syncing...",
                            color = if (isCloudSynced) MintGreen else AmberGlow,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Export Button
            Button(
                onClick = { showExportDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = StudioBackground
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("editor_export_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Export",
                    tint = StudioBackground,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export 4K", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Live Video Player Preview Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black)
                .border(1.dp, StudioCardBorder, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Video Frame with Keyframe Transforms & Filter Effects
            val previewRatio = when (activeAspectRatio) {
                "16:9" -> 16f / 9f
                "1:1" -> 1f
                "4:5" -> 4f / 5f
                else -> 9f / 16f
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight(0.92f)
                    .aspectRatio(previewRatio)
                    .clip(RoundedCornerShape(8.dp))
                    .background(StudioSurface)
                    .graphicsLayer {
                        scaleX = currentTransform.scale
                        scaleY = currentTransform.scale
                        translationX = currentTransform.positionX * 2.5f
                        translationY = currentTransform.positionY * 2.5f
                        rotationZ = currentTransform.rotation
                        alpha = currentTransform.opacity
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(
                        id = when (project?.thumbnailKey) {
                            "model" -> R.drawable.lookbook_ai_model
                            "product" -> R.drawable.product_studio_demo
                            else -> R.drawable.hero_studio_banner
                        }
                    ),
                    contentDescription = "Video Track Preview",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    colorFilter = when (activeFilter) {
                        "Noir B&W" -> ColorFilter.colorMatrix(androidx.compose.ui.graphics.ColorMatrix().apply { setToSaturation(0f) })
                        "Teal & Orange" -> ColorFilter.tint(NeonCyan.copy(alpha = 0.2f), androidx.compose.ui.graphics.BlendMode.ColorBurn)
                        "VHS Retro" -> ColorFilter.tint(VividMagenta.copy(alpha = 0.25f), androidx.compose.ui.graphics.BlendMode.Overlay)
                        "Cyberpunk" -> ColorFilter.tint(HyperViolet.copy(alpha = 0.3f), androidx.compose.ui.graphics.BlendMode.Screen)
                        else -> null
                    }
                )

                // Subtitle / Text overlay
                if (!activeText.isNullOrEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.75f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 20.dp, start = 8.dp, end = 8.dp)
                    ) {
                        Text(
                            text = activeText,
                            color = NeonCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                // Glitch / 3D Zoom scanlines canvas overlay
                if (activeFilter == "Glitch" || activeFilter == "3D Zoom" || activeFilter == "VHS Retro") {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val stroke = 2.dp.toPx()
                        var y = 0f
                        while (y < size.height) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.08f),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = stroke
                            )
                            y += 8.dp.toPx()
                        }
                    }
                }
            }

            // Player controls overlay (Bottom)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play / Pause button
                IconButton(
                    onClick = { viewModel.togglePlayPause() },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        .testTag("timeline_play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Timecode
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.7f)
                ) {
                    Text(
                        text = "${formatTimecode(playheadSec)} / ${formatTimecode(totalDuration)}",
                        color = StudioWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Filter badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = HyperViolet.copy(alpha = 0.5f),
                    modifier = Modifier.clickable { showFilterPicker = !showFilterPicker }
                ) {
                    Text(
                        text = "FX: $activeFilter",
                        color = StudioWhite,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // Toolbar Actions (Keyframe, FX, Add Clip, Split, Delete)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showKeyframePanel = !showKeyframePanel },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showKeyframePanel) HyperViolet else StudioSurfaceVariant,
                    contentColor = StudioWhite
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("toggle_keyframe_panel")
            ) {
                Icon(Icons.Default.Animation, contentDescription = "Keyframe", modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Keyframes", fontSize = 11.sp)
            }

            Button(
                onClick = { showFilterPicker = !showFilterPicker },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showFilterPicker) HyperViolet else StudioSurfaceVariant,
                    contentColor = StudioWhite
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("toggle_filter_panel")
            ) {
                Icon(Icons.Default.Filter, contentDescription = "FX", modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Filters", fontSize = 11.sp)
            }

            Button(
                onClick = { viewModel.navigateTo(StudioScreen.CAMERA_RECORD) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = HyperViolet.copy(alpha = 0.25f),
                    contentColor = NeonCyan
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .testTag("editor_record_button")
            ) {
                Icon(Icons.Default.Videocam, contentDescription = "Record", tint = NeonCyan, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text("Record", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    viewModel.addClipToTrack(TrackType.TEXT, "New Artistic Text", 3.5f, "✨ NEW TEXT")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = StudioSurfaceVariant,
                    contentColor = NeonCyan
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.TextFields, contentDescription = "Text", modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text("Text", fontSize = 10.sp)
            }

            if (selectedClipId != null) {
                IconButton(
                    onClick = { viewModel.deleteClip(selectedClipId!!) },
                    modifier = Modifier
                        .size(34.dp)
                        .background(VividMagenta.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = VividMagenta, modifier = Modifier.size(18.dp))
                }
            }
        }

        // Keyframe Animation Panel (Expandable)
        AnimatedVisibility(visible = showKeyframePanel) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .border(1.dp, HyperViolet.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Keyframe Animation Engine", color = StudioWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Button(
                            onClick = {
                                viewModel.addKeyframeAtCurrentTime(kfScale, kfPosX, kfPosY, kfRot, kfOpacity)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = StudioBackground),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add KF", tint = StudioBackground, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Set Keyframe (${formatTimecode(playheadSec)})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            ParameterSlider(
                                label = "Scale / Zoom",
                                value = kfScale,
                                onValueChange = { kfScale = it },
                                valueRange = 0.5f..2.5f,
                                displayValue = String.format("%.2f", kfScale),
                                unit = "x"
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            ParameterSlider(
                                label = "Rotation",
                                value = kfRot,
                                onValueChange = { kfRot = it },
                                valueRange = -90f..90f,
                                displayValue = "${kfRot.toInt()}",
                                unit = "°"
                            )
                        }
                    }

                    // Keyframe count summary
                    Text(
                        text = "${keyframes.size} Keyframes on timeline • Active: Scale ${String.format("%.2f", currentTransform.scale)}x",
                        color = HyperViolet,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Filter / FX Picker (Expandable)
        AnimatedVisibility(visible = showFilterPicker) {
            val filters = listOf("Normal", "Glitch", "3D Zoom", "Cyberpunk", "Blur", "VHS Retro", "Teal & Orange", "Noir B&W")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { flt ->
                    val isSelected = activeFilter == flt
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) HyperViolet.copy(alpha = 0.35f) else StudioSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NeonCyan else StudioCardBorder),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.setFilter(flt) }
                    ) {
                        Text(
                            text = flt,
                            color = if (isSelected) NeonCyan else StudioWhite,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Multi-Track Timeline Section
        Text(
            text = "MULTI-TRACK TIMELINE",
            color = StudioTextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 6.dp, bottom = 2.dp)
        )

        // Timeline Scrubber & Tracks
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.1f)
                .background(StudioSurface)
                .border(androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder))
        ) {
            val timelineScrollState = rememberScrollState()
            val pixelsPerSecond = 40.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(timelineScrollState)
                    .padding(horizontal = 16.dp)
            ) {
                // Timecode Ruler
                Row(
                    modifier = Modifier
                        .height(24.dp)
                        .padding(top = 4.dp)
                ) {
                    var sec = 0f
                    while (sec <= totalDuration) {
                        Box(
                            modifier = Modifier.width(pixelsPerSecond * 2)
                        ) {
                            Text(
                                text = "${sec.toInt()}s",
                                color = StudioTextMuted,
                                fontSize = 9.sp,
                                modifier = Modifier.align(Alignment.CenterStart)
                            )
                        }
                        sec += 2f
                    }
                }

                // Track 1: Video Track
                TimelineTrackRow(
                    trackType = TrackType.VIDEO,
                    trackName = "Video 1",
                    clips = clips.filter { it.trackType == TrackType.VIDEO },
                    selectedClipId = selectedClipId,
                    onSelectClip = { viewModel.selectClip(it) },
                    pixelsPerSecond = pixelsPerSecond
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Track 2: Audio Track
                TimelineTrackRow(
                    trackType = TrackType.AUDIO,
                    trackName = "Audio / BGM",
                    clips = clips.filter { it.trackType == TrackType.AUDIO },
                    selectedClipId = selectedClipId,
                    onSelectClip = { viewModel.selectClip(it) },
                    pixelsPerSecond = pixelsPerSecond
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Track 3: Text / Captions Track
                TimelineTrackRow(
                    trackType = TrackType.TEXT,
                    trackName = "Captions",
                    clips = clips.filter { it.trackType == TrackType.TEXT },
                    selectedClipId = selectedClipId,
                    onSelectClip = { viewModel.selectClip(it) },
                    pixelsPerSecond = pixelsPerSecond
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Track 4: Effects / Filters Track
                TimelineTrackRow(
                    trackType = TrackType.EFFECT,
                    trackName = "FX & Filters",
                    clips = clips.filter { it.trackType == TrackType.EFFECT },
                    selectedClipId = selectedClipId,
                    onSelectClip = { viewModel.selectClip(it) },
                    pixelsPerSecond = pixelsPerSecond
                )
            }

            // Playhead indicator (Red vertical line)
            val playheadOffset = (playheadSec * 40).dp + 16.dp - (timelineScrollState.value / 2.7f).dp
            Box(
                modifier = Modifier
                    .offset(x = playheadOffset.coerceAtLeast(0.dp))
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(NeonCyan)
            ) {
                // Playhead needle head
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(NeonCyan, CircleShape)
                        .align(Alignment.TopCenter)
                )
            }
        }
    }

    // Export Dialog modal
    if (showExportDialog) {
        ExportDialog(
            projectTitle = project?.title ?: "OmniCut AI Video",
            onDismiss = { showExportDialog = false },
            onExportComplete = {
                showExportDialog = false
            }
        )
    }
}

@Composable
fun TimelineTrackRow(
    trackType: TrackType,
    trackName: String,
    clips: List<TimelineClip>,
    selectedClipId: String?,
    onSelectClip: (String) -> Unit,
    pixelsPerSecond: androidx.compose.ui.unit.Dp
) {
    Row(
        modifier = Modifier
            .height(42.dp)
            .fillMaxWidth()
            .background(StudioSurfaceVariant, RoundedCornerShape(6.dp))
            .border(1.dp, StudioCardBorder.copy(alpha = 0.5f), RoundedCornerShape(6.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Track Header Label
        Surface(
            shape = RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp),
            color = StudioSurfaceElevated,
            modifier = Modifier
                .width(74.dp)
                .fillMaxHeight()
        ) {
            Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 6.dp)) {
                Text(
                    text = trackName,
                    color = when (trackType) {
                        TrackType.VIDEO -> TrackVideoColor
                        TrackType.AUDIO -> TrackAudioColor
                        TrackType.TEXT -> TrackTextColor
                        TrackType.EFFECT -> TrackEffectColor
                        else -> StudioWhite
                    },
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }

        // Timeline Clip blocks
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            clips.forEach { clip ->
                val clipWidth = (clip.durationSec * 40).dp
                val clipOffset = (clip.startTimeSec * 40).dp
                val isSelected = clip.id == selectedClipId

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(clip.colorHex).copy(alpha = if (isSelected) 0.9f else 0.7f),
                    border = androidx.compose.foundation.BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) NeonCyan else Color.White.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .offset(x = clipOffset)
                        .width(clipWidth)
                        .height(34.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onSelectClip(clip.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = clip.title,
                            color = StudioWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// Keyframe math interpolation
fun interpolateKeyframe(currentTimeSec: Float, keyframes: List<Keyframe>): KeyframeTransform {
    if (keyframes.isEmpty()) {
        return KeyframeTransform(1f, 0f, 0f, 0f, 1f)
    }
    if (keyframes.size == 1) {
        val single = keyframes.first()
        return KeyframeTransform(single.scale, single.positionX, single.positionY, single.rotation, single.opacity)
    }

    // Sort by timestamp
    val sorted = keyframes.sortedBy { it.timeSec }
    if (currentTimeSec <= sorted.first().timeSec) {
        val first = sorted.first()
        return KeyframeTransform(first.scale, first.positionX, first.positionY, first.rotation, first.opacity)
    }
    if (currentTimeSec >= sorted.last().timeSec) {
        val last = sorted.last()
        return KeyframeTransform(last.scale, last.positionX, last.positionY, last.rotation, last.opacity)
    }

    // Find bounding pair
    var prev = sorted.first()
    var next = sorted.last()
    for (i in 0 until sorted.size - 1) {
        if (currentTimeSec >= sorted[i].timeSec && currentTimeSec <= sorted[i + 1].timeSec) {
            prev = sorted[i]
            next = sorted[i + 1]
            break
        }
    }

    val span = next.timeSec - prev.timeSec
    val progress = if (span > 0f) ((currentTimeSec - prev.timeSec) / span).coerceIn(0f, 1f) else 0f

    val scale = prev.scale + (next.scale - prev.scale) * progress
    val posX = prev.positionX + (next.positionX - prev.positionX) * progress
    val posY = prev.positionY + (next.positionY - prev.positionY) * progress
    val rot = prev.rotation + (next.rotation - prev.rotation) * progress
    val opacity = prev.opacity + (next.opacity - prev.opacity) * progress

    return KeyframeTransform(scale, posX, posY, rot, opacity)
}

data class KeyframeTransform(
    val scale: Float,
    val positionX: Float,
    val positionY: Float,
    val rotation: Float,
    val opacity: Float
)

fun formatTimecode(seconds: Float): String {
    val mins = (seconds / 60).toInt()
    val secs = (seconds % 60).toInt()
    val millis = ((seconds - seconds.toInt()) * 100).toInt()
    return String.format("%02d:%02d.%02d", mins, secs, millis)
}
