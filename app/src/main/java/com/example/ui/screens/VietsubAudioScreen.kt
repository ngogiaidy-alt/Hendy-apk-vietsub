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
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.mutableStateOf
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
import com.example.viewmodel.StudioScreen
import com.example.viewmodel.StudioViewModel

@Composable
fun VietsubAudioScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "Dịch Vietsub & LipSync",
        "Phụ đề tự động",
        "Giọng đọc AI (200+)",
        "Tách lời & Tạp âm",
        "Kho nhạc & SFX"
    )

    val ttsInput by viewModel.ttsInputText.collectAsState()
    val selectedVoice by viewModel.selectedVoice.collectAsState()
    val ttsSpeed by viewModel.ttsSpeed.collectAsState()
    val ttsPitch by viewModel.ttsPitch.collectAsState()
    val ttsEmotion by viewModel.ttsEmotion.collectAsState()
    val isAudioProcessing by viewModel.isAudioProcessing.collectAsState()
    val audioStatus by viewModel.audioResultStatus.collectAsState()
    val videoUtilStatus by viewModel.videoUtilStatus.collectAsState()

    val vocalLevel by viewModel.vocalIsolationLevel.collectAsState()
    val bgmLevel by viewModel.bgmLevel.collectAsState()
    val noiseLevel by viewModel.noiseReductionLevel.collectAsState()

    val voices = viewModel.voiceProfiles
    val audioLibrary = viewModel.royaltyFreeAudio

    val emotions = listOf(
        "Điện ảnh truyền cảm",
        "Hype sôi động TikTok",
        "MC thời sự trang trọng",
        "Thì thầm êm dịu",
        "Kể chuyện lôi cuốn"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            StudioHeader(
                title = "Dịch Vietsub & Âm Thanh",
                subtitle = "Khớp khẩu hình LipSync • Phụ đề tiếng Việt 99% • 200+ Giọng đọc",
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
                        modifier = Modifier.testTag("vietsub_tab_$index")
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> {
                // Dịch Video Vietsub & LipSync
                item {
                    var sourceLang by remember { mutableStateOf("Tiếng Anh (Mỹ)") }
                    var targetLang by remember { mutableStateOf("Tiếng Việt") }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Dịch Video Đa Ngôn Ngữ & Khớp Khẩu Hình (LipSync)",
                            color = StudioWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tự động nhận diện lời thoại gốc, dịch sang tiếng Việt và điều chỉnh chuyển động cơ môi của nhân vật khớp hoàn toàn với bản dịch.",
                            color = StudioTextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        // Language Selection
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = StudioSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Ngôn ngữ gốc", color = StudioTextMuted, fontSize = 10.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(sourceLang, color = StudioWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = HyperViolet.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Dịch sang", color = NeonCyan, fontSize = 10.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(targetLang, color = StudioWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Features included
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = StudioSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Khả năng xử lý của Hendy Vietsub:", color = StudioWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("✓ Giữ nguyên âm sắc và cảm xúc của diễn viên gốc", color = MintGreen, fontSize = 11.sp)
                                Text("✓ Đồng bộ chuyển động khẩu hình môi theo thời gian thực (Neural LipSync)", color = MintGreen, fontSize = 11.sp)
                                Text("✓ Xuất video kèm phụ đề Vietsub chạy song ngữ hoặc đơn ngữ", color = MintGreen, fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        StatusFeedbackBanner(statusText = videoUtilStatus)

                        Button(
                            onClick = { viewModel.runVideoTranslation("tiếng Việt") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonCyan,
                                contentColor = StudioBackground
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_run_vietsub_translate")
                        ) {
                            Icon(Icons.Default.Translate, contentDescription = "Dịch", tint = StudioBackground, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Bắt đầu Dịch & Khớp Khẩu Hình Vietsub", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
            1 -> {
                // Phụ đề tự động tiếng Việt
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Phụ Đề Tự Động Tiếng Việt (Auto Captions)",
                            color = StudioWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Trích xuất phụ đề tự động bằng giọng tiếng Việt chuẩn xác 99.4%, phân tách từng câu và tạo hiệu ứng chữ động nhảy nhót.",
                            color = StudioTextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        // Style preview
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
                                Text("PHONG CÁCH PHỤ ĐỀ MẶC ĐỊNH", color = StudioTextMuted, fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "✨ BỨT PHÁ GIỚI HẠN SÁNG TẠO ✨",
                                    color = NeonCyan,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Hiệu ứng Giật Chữ Karaoke • Phát sáng Neon Vietsub", color = HyperViolet, fontSize = 11.sp)
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
                                .testTag("btn_run_vietsub_captions")
                        ) {
                            Icon(Icons.Default.ClosedCaption, contentDescription = "Phụ đề", tint = StudioBackground, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tạo Phụ Đề Tự Động Vào Dòng Thời Gian", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
            2 -> {
                // Text to Speech (200+ Giọng đọc)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Văn bản cần đọc", color = StudioWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = ttsInput,
                            onValueChange = { viewModel.setTtsInputText(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("tts_text_input_vn"),
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

                        Text("Chọn Giọng Đọc AI (Bắc, Trung, Nam & Quốc Tế)", color = StudioWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
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

                        Text("Sắc thái cảm xúc", color = StudioWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
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
                            label = "Tốc độ đọc",
                            value = ttsSpeed,
                            onValueChange = { viewModel.setTtsSpeed(it) },
                            valueRange = 0.5f..2.0f,
                            displayValue = String.format("%.2f", ttsSpeed),
                            unit = "x",
                            testTag = "slider_tts_speed_vn"
                        )

                        ParameterSlider(
                            label = "Cao độ giọng nói",
                            value = ttsPitch,
                            onValueChange = { viewModel.setTtsPitch(it) },
                            valueRange = 0.5f..1.5f,
                            displayValue = String.format("%.2f", ttsPitch),
                            unit = "",
                            testTag = "slider_tts_pitch_vn"
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
                                    .testTag("generate_tts_button_vn")
                            ) {
                                Icon(Icons.Default.RecordVoiceOver, contentDescription = "TTS", tint = StudioBackground, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Tạo Giọng Đọc", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Button(
                                onClick = {
                                    viewModel.addClipToTrack(TrackType.AUDIO, "Giọng đọc: ${selectedVoice.name} ($ttsEmotion)", 6.0f)
                                    viewModel.navigateTo(StudioScreen.EDITOR)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = HyperViolet, contentColor = StudioWhite),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("add_tts_to_timeline_vn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Thêm", tint = StudioWhite, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Đưa vào Timeline", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
            3 -> {
                // Tách lời & Tạp âm
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Tách Lời Hát, Nhạc Nền & Lọc Tạp Âm", color = StudioWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Tách riêng giọng nói hoặc giọng hát khỏi nhạc nền, khử tiếng ồn xe cộ và tiếng gió rít.",
                            color = StudioTextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        ParameterSlider(
                            label = "Mức độ Tách Giọng Hát (Vocal)",
                            value = vocalLevel,
                            onValueChange = { viewModel.setVocalIsolation(it) },
                            valueRange = 0f..100f,
                            displayValue = "${vocalLevel.toInt()}",
                            unit = "%"
                        )

                        ParameterSlider(
                            label = "Âm lượng Nhạc Nền (BGM)",
                            value = bgmLevel,
                            onValueChange = { viewModel.setBgmLevel(it) },
                            valueRange = 0f..100f,
                            displayValue = "${bgmLevel.toInt()}",
                            unit = "%"
                        )

                        ParameterSlider(
                            label = "Mức độ Khử Tiếng Ồn & Gió",
                            value = noiseLevel,
                            onValueChange = { viewModel.setNoiseReduction(it) },
                            valueRange = 0f..100f,
                            displayValue = "${noiseLevel.toInt()}",
                            unit = "%"
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
                                .testTag("btn_run_stem_separation")
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = "Tách", tint = StudioBackground, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Bắt đầu Tách Dải Âm Thanh", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
            4 -> {
                // Kho nhạc & SFX
                item {
                    Text(
                        text = "Kho Nhạc Thịnh Hành & Hiệu Ứng Âm Thanh",
                        color = StudioWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                    )
                    Text(
                        text = "Nhạc viral TikTok và hiệu ứng chuyển cảnh, vỗ tay, tiếng cười miễn phí bản quyền.",
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
                            viewModel.navigateTo(StudioScreen.EDITOR)
                        }
                    )
                }
            }
        }
    }
}
