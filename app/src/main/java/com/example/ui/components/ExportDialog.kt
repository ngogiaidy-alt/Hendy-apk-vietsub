package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
import com.example.util.FileImportExportHelper
import com.example.viewmodel.StudioViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ExportDialog(
    projectTitle: String,
    viewModel: StudioViewModel? = null,
    onDismiss: () -> Unit,
    onExportComplete: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var activeExportTab by remember { mutableIntStateOf(0) }
    val exportTabs = listOf("Xuất Video 4K", "Xuất Phụ đề Vietsub", "Sao lưu Dự án")

    // Video export settings
    var format by remember { mutableStateOf("MP4") }
    var resolution by remember { mutableStateOf("4K") }
    var fps by remember { mutableStateOf(60) }
    var isWatermarkFree by remember { mutableStateOf(true) }
    var isExporting by remember { mutableStateOf(false) }
    var exportProgress by remember { mutableFloatStateOf(0f) }
    var exportStage by remember { mutableStateOf("Sẵn sàng mã hóa") }
    var isFinished by remember { mutableStateOf(false) }

    // Subtitle export settings
    var subtitleFormat by remember { mutableStateOf("SRT") }
    val subtitleContent = remember(subtitleFormat) {
        viewModel?.exportSubtitles(subtitleFormat) ?: "1\n00:00:01,000 --> 00:00:04,500\n⚡ BỨT PHÁ GIỚI HẠN SÁNG TẠO ⚡\n"
    }

    // Project backup content
    val projectBackupJson = remember {
        viewModel?.exportProjectBackupJson() ?: "{\n  \"app\": \"Hendy Vietsub\",\n  \"project\": { \"title\": \"$projectTitle\" }\n}"
    }

    // File save launchers
    val saveVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(if (format == "MP4") "video/mp4" else "video/quicktime")
    ) { uri: Uri? ->
        uri?.let {
            viewModel?.saveExportedFileToUri(context, it, "", isMedia = true)
            onExportComplete()
            onDismiss()
        }
    }

    val saveSubtitleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri: Uri? ->
        uri?.let {
            viewModel?.saveExportedFileToUri(context, it, subtitleContent, isMedia = false)
            onExportComplete()
            onDismiss()
        }
    }

    val saveProjectJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            viewModel?.saveExportedFileToUri(context, it, projectBackupJson, isMedia = false)
            onExportComplete()
            onDismiss()
        }
    }

    Dialog(onDismissRequest = { if (!isExporting) onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = StudioSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Trung Tâm Xuất Tệp",
                            color = StudioWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = projectTitle,
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    if (!isExporting) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("close_export_dialog")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Đóng",
                                tint = StudioTextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Export Type Tabs
                if (!isExporting && !isFinished) {
                    ScrollableTabRow(
                        selectedTabIndex = activeExportTab,
                        containerColor = StudioSurfaceVariant,
                        contentColor = NeonCyan,
                        edgePadding = 4.dp,
                        divider = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        exportTabs.forEachIndexed { idx, label ->
                            Tab(
                                selected = activeExportTab == idx,
                                onClick = { activeExportTab = idx },
                                text = {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (activeExportTab == idx) FontWeight.Bold else FontWeight.Normal,
                                        color = if (activeExportTab == idx) NeonCyan else StudioTextMuted
                                    )
                                },
                                modifier = Modifier.testTag("export_tab_$idx")
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                when (activeExportTab) {
                    0 -> {
                        // TAB 0: VIDEO EXPORT
                        if (!isFinished && !isExporting) {
                            // Format selection
                            Text(
                                text = "Định Dạng Video",
                                color = StudioTextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("MP4 (H.264 / HEVC)", "MOV (ProRes)").forEach { fmt ->
                                    val key = if (fmt.startsWith("MP4")) "MP4" else "MOV"
                                    val isSelected = format == key
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) HyperViolet.copy(alpha = 0.3f) else StudioSurfaceVariant,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) NeonCyan else StudioCardBorder
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { format = key }
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(
                                                text = key,
                                                color = if (isSelected) NeonCyan else StudioWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = if (key == "MP4") "Phổ biến, tối ưu mạng xã hội" else "Chất lượng dựng phim tối đa",
                                                color = StudioTextMuted,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Resolution
                            Text(
                                text = "Độ Phân Giải",
                                color = StudioTextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("4K UHD", "2K QHD", "1080p FHD", "720p HD").forEach { res ->
                                    val key = res.substringBefore(" ")
                                    val isSelected = resolution == key
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) HyperViolet.copy(alpha = 0.35f) else StudioSurfaceVariant,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) NeonCyan else StudioCardBorder
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { resolution = key }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = key,
                                                color = if (isSelected) NeonCyan else StudioWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // FPS selection
                            Text(
                                text = "Tốc Độ Khung Hình (FPS)",
                                color = StudioTextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(60, 30, 24).forEach { f ->
                                    val isSelected = fps == f
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) HyperViolet.copy(alpha = 0.35f) else StudioSurfaceVariant,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) NeonCyan else StudioCardBorder
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { fps = f }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "${f}fps",
                                                color = if (isSelected) NeonCyan else StudioWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Watermark toggle
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = StudioSurfaceElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "Xuất Video Không Hình Mờ",
                                                color = StudioWhite,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = MintGreen.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = "MIỄN PHÍ",
                                                    color = MintGreen,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "Không chèn logo Hendy Vietsub hay hình mờ",
                                            color = StudioTextMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Switch(
                                        checked = isWatermarkFree,
                                        onCheckedChange = { isWatermarkFree = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = NeonCyan,
                                            checkedTrackColor = HyperViolet
                                        ),
                                        modifier = Modifier.testTag("toggle_watermark_free")
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Start Export Button
                            Button(
                                onClick = {
                                    isExporting = true
                                    coroutineScope.launch {
                                        exportStage = "Biên dịch bố cục đa luồng..."
                                        delay(500)
                                        exportProgress = 0.25f
                                        exportStage = "Kết xuất biến đổi Keyframe & bộ đệm $resolution..."
                                        delay(700)
                                        exportProgress = 0.60f
                                        exportStage = "Tổng hợp âm thanh 48kHz & làm nét Vietsub..."
                                        delay(700)
                                        exportProgress = 0.88f
                                        exportStage = "Đóng gói container $format ($resolution @ ${fps}fps)..."
                                        delay(600)
                                        exportProgress = 1.0f
                                        exportStage = "Hoàn tất mã hóa xuất sắc!"
                                        delay(300)
                                        isExporting = false
                                        isFinished = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonCyan,
                                    contentColor = StudioBackground
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("start_export_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "Xuất",
                                    tint = StudioBackground,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Bắt đầu xuất $resolution @ ${fps}fps ($format)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        } else if (isExporting) {
                            // Export Progress
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VideoFile,
                                    contentDescription = "Đang mã hóa",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Đang kết xuất Video $resolution $format",
                                    color = StudioWhite,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = exportStage,
                                    color = StudioTextMuted,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                LinearProgressIndicator(
                                    progress = { exportProgress },
                                    color = NeonCyan,
                                    trackColor = StudioSurfaceElevated,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${(exportProgress * 100).toInt()}%",
                                    color = NeonCyan,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            // Finished state
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Hoàn tất",
                                    tint = MintGreen,
                                    modifier = Modifier.size(54.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Xuất Video Thành Công!",
                                    color = StudioWhite,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$projectTitle • $resolution • ${fps}fps • Không hình mờ",
                                    color = StudioTextMuted,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            val ext = if (format == "MP4") "mp4" else "mov"
                                            saveVideoLauncher.launch("${projectTitle.take(20)}_4K.$ext")
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = NeonCyan,
                                            contentColor = StudioBackground
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("save_video_file_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Save,
                                            contentDescription = "Lưu",
                                            tint = StudioBackground,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Lưu vào máy", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = {
                                            FileImportExportHelper.shareText(
                                                context,
                                                "Xem video $projectTitle xuất chuẩn $resolution ${fps}fps từ Hendy Vietsub Studio!",
                                                projectTitle
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = HyperViolet,
                                            contentColor = StudioWhite
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("share_video_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Chia sẻ",
                                            tint = StudioWhite,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Chia sẻ tệp", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        // TAB 1: SUBTITLE EXPORT (.SRT / .VTT)
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Định Dạng Phụ Đề",
                                color = StudioTextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("SRT (SubRip chuẩn)", "VTT (WebVTT mạng)").forEach { fmt ->
                                    val key = if (fmt.startsWith("SRT")) "SRT" else "VTT"
                                    val isSelected = subtitleFormat == key
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) HyperViolet.copy(alpha = 0.35f) else StudioSurfaceVariant,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) NeonCyan else StudioCardBorder
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { subtitleFormat = key }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = fmt,
                                                color = if (isSelected) NeonCyan else StudioWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Preview subtitle content
                            Text(
                                text = "Xem trước nội dung phụ đề:",
                                color = StudioTextMuted,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = StudioSurfaceElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                            ) {
                                Text(
                                    text = subtitleContent.ifEmpty { "(Chưa có phụ đề trong dòng thời gian)" },
                                    color = AmberGlow,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val ext = if (subtitleFormat == "SRT") "srt" else "vtt"
                                        saveSubtitleLauncher.launch("${projectTitle.take(20)}_vietsub.$ext")
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NeonCyan,
                                        contentColor = StudioBackground
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("save_subtitle_file_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Save,
                                        contentDescription = "Lưu phụ đề",
                                        tint = StudioBackground,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Lưu tệp .$subtitleFormat", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        FileImportExportHelper.shareText(
                                            context,
                                            subtitleContent,
                                            "Phụ đề $projectTitle ($subtitleFormat)"
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = HyperViolet,
                                        contentColor = StudioWhite
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("share_subtitle_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Chia sẻ",
                                        tint = StudioWhite,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Chia sẻ", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    2 -> {
                        // TAB 2: PROJECT BACKUP (.JSON)
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = StudioSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Sao lưu dự án Hendy Vietsub",
                                        color = StudioWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Tệp sao lưu chứa toàn bộ các lớp video, âm thanh, phụ đề Vietsub, hiệu ứng và khung hình khóa (keyframes) để phục hồi trên bất kỳ thiết bị nào.",
                                        color = StudioTextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        saveProjectJsonLauncher.launch("${projectTitle.take(20)}_backup.hendy.json")
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NeonCyan,
                                        contentColor = StudioBackground
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("save_project_json_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Save,
                                        contentDescription = "Lưu dự án",
                                        tint = StudioBackground,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Lưu tệp .JSON", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        FileImportExportHelper.shareText(
                                            context,
                                            projectBackupJson,
                                            "Sao lưu dự án $projectTitle"
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = HyperViolet,
                                        contentColor = StudioWhite
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("share_project_json_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Chia sẻ",
                                        tint = StudioWhite,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Chia sẻ tệp", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
