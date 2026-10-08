package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioWhite
import com.example.ui.theme.VividMagenta
import com.example.viewmodel.StudioViewModel

@Composable
fun MediaImportDialog(
    viewModel: StudioViewModel,
    asNewProject: Boolean = false,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // Video Picker Launcher (Multi)
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.importMediaFiles(context, uris, asNewProject)
            onDismiss()
        }
    }

    // Photo/Image Picker Launcher (Multi)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.importMediaFiles(context, uris, asNewProject)
            onDismiss()
        }
    }

    // Audio Document Picker Launcher
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.importAudioFile(context, it)
            onDismiss()
        }
    }

    // Subtitle (.srt / .vtt) Picker Launcher
    val subtitlePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.importSubtitleFile(context, it)
            onDismiss()
        }
    }

    // Project Backup JSON Picker Launcher
    val projectBackupPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.importProjectBackup(context, it)
            onDismiss()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
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
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (asNewProject) "Nhập tệp tạo Dự án mới" else "Nhập Tệp & Phương Tiện",
                            color = StudioWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Chọn định dạng phương tiện từ bộ nhớ thiết bị",
                            color = StudioTextMuted,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_import_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = StudioTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Import Options List
                ImportOptionItem(
                    icon = Icons.Default.Movie,
                    title = "Nhập Video (MP4, MOV, MKV)",
                    subtitle = "Thêm đoạn video vào luồng Video 1 / Lớp phủ",
                    accentColor = NeonCyan,
                    tag = "import_video_option",
                    onClick = {
                        videoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                ImportOptionItem(
                    icon = Icons.Default.Image,
                    title = "Nhập Hình Ảnh (JPG, PNG, WEBP)",
                    subtitle = "Thêm ảnh tĩnh, logo, b-roll hoặc sticker đồ họa",
                    accentColor = HyperViolet,
                    tag = "import_image_option",
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                ImportOptionItem(
                    icon = Icons.Default.Audiotrack,
                    title = "Nhập Âm Thanh / Nhạc (MP3, WAV, M4A)",
                    subtitle = "Chèn nhạc nền, beat hoặc hiệu ứng âm thanh SFX",
                    accentColor = MintGreen,
                    tag = "import_audio_option",
                    onClick = {
                        audioPickerLauncher.launch(
                            arrayOf("audio/*", "audio/mpeg", "audio/wav", "audio/mp4", "audio/aac")
                        )
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                ImportOptionItem(
                    icon = Icons.Default.Subtitles,
                    title = "Nhập Tệp Phụ Đề (.SRT, .VTT)",
                    subtitle = "Tải phụ đề và đồng bộ hóa dòng thời gian tự động",
                    accentColor = AmberGlow,
                    tag = "import_subtitle_option",
                    onClick = {
                        subtitlePickerLauncher.launch(
                            arrayOf("*/*", "text/plain", "text/vtt", "application/x-subrip")
                        )
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                ImportOptionItem(
                    icon = Icons.Default.Description,
                    title = "Khôi phục Tệp Dự Án (.JSON)",
                    subtitle = "Mở bản sao lưu dự án Hendy Vietsub đầy đủ",
                    accentColor = VividMagenta,
                    tag = "import_project_json_option",
                    onClick = {
                        projectBackupPickerLauncher.launch(
                            arrayOf("*/*", "application/json", "text/plain")
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ImportOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: androidx.compose.ui.graphics.Color,
    tag: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = StudioSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = accentColor.copy(alpha = 0.2f),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = StudioWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = StudioTextMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}
