package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioItem
import com.example.model.TrackType
import com.example.model.VoiceProfile
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
import com.example.viewmodel.StudioViewModel

@Composable
fun AiAudioScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Text to Speech (200+)", "Sound to Text", "Audio Isolation Stems", "Royalty-Free Audio")

    val ttsInput by viewModel.ttsInputText.collectAsState()
    val selectedVoice by viewModel.selectedVoice.collectAsState()
    val ttsSpeed by viewModel.ttsSpeed.collectAsState()
    val ttsPitch by viewModel.ttsPitch.collectAsState()
    val ttsEmotion by viewModel.ttsEmotion.collectAsState()
    val isAudioProcessing by viewModel.isAudioProcessing.collectAsState()
    val audioStatus by viewModel.audioResultStatus.collectAsState()

    val vocalLevel by viewModel.vocalIsolationLevel.collectAsState()
    val bgmLevel by viewModel.bgmLevel.collectAsState()
    val noiseLevel by viewModel.noiseReductionLevel.collectAsState()

    val voices = viewModel.voiceProfiles
    val audioLibrary = viewModel.royaltyFreeAudio

    val emotions = listOf("Cinematic", "Hype Commercial", "Narrative", "Calm & Whisper", "Energetic")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            StudioHeader(
                title = "AI Voice & Audio Studio",
                subtitle = "200+ Natural Voices • 3-Stem Isolation • SFX Library",
                isCloudSynced = true
            )
        }

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
                        modifier = Modifier.testTag("audio_tab_$index")
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> {
                // Text to Speech
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Voice Script Text", color = StudioWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = ttsInput,
                            onValueChange = { viewModel.setTtsInputText(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("tts_text_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = StudioCardBorder,
                                focusedContainerColor = StudioSurfaceVariant,
                                unfocusedContainerColor = StudioSurfaceVariant,
                                focusedTextColor = StudioWhite,
                                unfocusedTextColor = StudioWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 3
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Select Natural Voice (200+ Catalog)", color = StudioWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            voices.forEach { voice ->
                                val isSelected = selectedVoice.id == voice.id
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) HyperViolet.copy(alpha = 0.3f) else StudioSurfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NeonCyan else StudioCardBorder),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { viewModel.selectVoice(voice) }
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(voice.name, color = if (isSelected) NeonCyan else StudioWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(voice.language, color = StudioTextMuted, fontSize = 10.sp)
                                        Text(voice.category, color = HyperViolet, fontSize = 9.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Emotional Inflection", color = StudioWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            emotions.forEach { em ->
                                val isSelected = ttsEmotion == em
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) HyperViolet.copy(alpha = 0.3f) else StudioSurfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NeonCyan else StudioCardBorder),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { viewModel.setTtsEmotion(em) }
                                ) {
                                    Text(
                                        text = em,
                                        color = if (isSelected) NeonCyan else StudioTextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        ParameterSlider(
                            label = "Speech Speed",
                            value = ttsSpeed,
                            onValueChange = { viewModel.setTtsSpeed(it) },
                            valueRange = 0.5f..2.0f,
                            displayValue = String.format("%.2f", ttsSpeed),
                            unit = "x",
                            testTag = "slider_tts_speed"
                        )

                        ParameterSlider(
                            label = "Voice Pitch",
                            value = ttsPitch,
                            onValueChange = { viewModel.setTtsPitch(it) },
                            valueRange = 0.5f..1.5f,
                            displayValue = String.format("%.2f", ttsPitch),
                            unit = "",
                            testTag = "slider_tts_pitch"
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        StatusFeedbackBanner(statusText = audioStatus, isLoading = isAudioProcessing)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.generateSpeechTTS() },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = StudioBackground),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("generate_tts_button")
                            ) {
                                Icon(Icons.Default.RecordVoiceOver, contentDescription = "TTS", tint = StudioBackground, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Synthesize Speech", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Button(
                                onClick = {
                                    viewModel.addClipToTrack(TrackType.AUDIO, "Voice: ${selectedVoice.name} ($ttsEmotion)", 6.0f)
                                    viewModel.navigateTo(com.example.viewmodel.StudioScreen.EDITOR)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = HyperViolet, contentColor = StudioWhite),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("add_tts_to_timeline")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add", tint = StudioWhite, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add to Timeline", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
            1 -> {
                // Sound to Text
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Sound to Text & Speech Dialogue Extractor", color = StudioWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Extracts spoken dialogue from audio tracks with high accuracy to generate subtitles or script exports.",
                            color = StudioTextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        StatusFeedbackBanner(statusText = audioStatus, isLoading = isAudioProcessing)

                        Button(
                            onClick = { viewModel.runSoundToText() },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = StudioBackground),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("run_sound_to_text")
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = "Extract", tint = StudioBackground, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Extract Spoken Dialogue", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = StudioSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("EXTRACTED TRANSCRIPT (SRT)", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "00:00:01.200 --> 00:00:04.500\n\"Welcome to OmniCut AI Studio, where your story begins.\"\n\n00:00:04.800 --> 00:00:08.200\n\"Experience 4K 60fps export and seamless multi-track editing.\"",
                                    color = StudioWhite,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
            2 -> {
                // AI Audio Isolation (Vocals, BGM, Ambient Noise)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("AI Audio Stem Isolation & De-Noiser", color = StudioWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Separates vocals from background music stems or completely removes wind, rumble, and ambient hiss.",
                            color = StudioTextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        ParameterSlider(
                            label = "Vocal Boost & Isolation",
                            value = vocalLevel,
                            onValueChange = { viewModel.setVocalIsolation(it) },
                            valueRange = 0f..100f,
                            displayValue = "${vocalLevel.toInt()}",
                            unit = "%",
                            testTag = "slider_vocal_isolation"
                        )

                        ParameterSlider(
                            label = "Background Music (BGM) Level",
                            value = bgmLevel,
                            onValueChange = { viewModel.setBgmLevel(it) },
                            valueRange = 0f..100f,
                            displayValue = "${bgmLevel.toInt()}",
                            unit = "%",
                            testTag = "slider_bgm_level"
                        )

                        ParameterSlider(
                            label = "Ambient Noise Reduction (De-Noise)",
                            value = noiseLevel,
                            onValueChange = { viewModel.setNoiseReduction(it) },
                            valueRange = 0f..100f,
                            displayValue = "${noiseLevel.toInt()}",
                            unit = "%",
                            testTag = "slider_noise_reduction"
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        StatusFeedbackBanner(statusText = audioStatus, isLoading = isAudioProcessing)

                        Button(
                            onClick = { viewModel.runAudioStemSeparation() },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = StudioBackground),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("run_audio_isolation")
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = "Isolate", tint = StudioBackground, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Separate Stems & Clean Audio", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
            3 -> {
                // Royalty-Free Audio Library
                item {
                    Text(
                        text = "Royalty-Free Audio & Sound FX Library",
                        color = StudioWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                    )
                    Text(
                        text = "Trending background music and sound effects (laughter, swooshes, transitions). Commercial use permitted.",
                        color = StudioTextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                    )
                }

                items(audioLibrary, key = { it.id }) { audio ->
                    AudioLibraryItemRow(
                        item = audio,
                        onAddToTimeline = {
                            viewModel.addClipToTrack(TrackType.AUDIO, audio.title, 8.0f)
                            viewModel.navigateTo(com.example.viewmodel.StudioScreen.EDITOR)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AudioLibraryItemRow(
    item: AudioItem,
    onAddToTimeline: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = StudioSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (item.isSFX) MintGreen.copy(alpha = 0.2f) else HyperViolet.copy(alpha = 0.2f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (item.isSFX) Icons.Default.Audiotrack else Icons.Default.MusicNote,
                        contentDescription = "Audio",
                        tint = if (item.isSFX) MintGreen else HyperViolet,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, color = StudioWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("${item.artistOrCategory} • ${item.duration}", color = StudioTextMuted, fontSize = 11.sp)
            }
            Button(
                onClick = onAddToTimeline,
                colors = ButtonDefaults.buttonColors(containerColor = StudioSurfaceElevated, contentColor = NeonCyan),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = NeonCyan, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add", fontSize = 11.sp)
            }
        }
    }
}
