package com.example.ui.screens

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Project
import com.example.ui.components.StudioHeader
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
import com.example.ui.theme.VividMagenta
import com.example.viewmodel.StudioScreen
import com.example.viewmodel.StudioViewModel

@Composable
fun HomeScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.projects.collectAsState()
    val isCloudSynced by viewModel.isCloudSynced.collectAsState()
    val cloudSyncText by viewModel.cloudSyncText.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // CapCut App Header
        item {
            StudioHeader(
                title = "Hendy Vietsub",
                subtitle = "Chỉnh sửa video & dịch phụ đề chuyên nghiệp",
                isCloudSynced = isCloudSynced,
                onSyncClick = { viewModel.triggerCloudSync() }
            )
        }

        // Signature CapCut "+ Dự án mới" Hero Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(HyperViolet.copy(alpha = 0.35f), NeonCyan.copy(alpha = 0.2f))
                        )
                    )
                    .border(1.dp, NeonCyan.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                    .clickable { viewModel.createNewProject("9:16", "Dự án mới Hendy Vietsub") }
                    .testTag("capcut_new_project_hero")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = NeonCyan,
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tạo dự án mới",
                                tint = StudioBackground,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Dự án mới",
                            color = StudioWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Nhập video/ảnh để bắt đầu chỉnh sửa đa luồng 4K",
                            color = StudioTextMuted,
                            fontSize = 12.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = VividMagenta.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VividMagenta)
                    ) {
                        Text(
                            text = "4K 60FPS",
                            color = StudioWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // CapCut Quick Tools Grid (Iconic circular tool buttons)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CapCutToolIcon(
                        icon = Icons.Default.Videocam,
                        label = "Máy ảnh",
                        tint = NeonCyan,
                        onClick = { viewModel.navigateTo(StudioScreen.CAMERA_RECORD) },
                        testTag = "tool_camera"
                    )
                    CapCutToolIcon(
                        icon = Icons.Default.Translate,
                        label = "Dịch Vietsub",
                        tint = VividMagenta,
                        onClick = { viewModel.navigateTo(StudioScreen.VIETSUB_AUDIO) },
                        testTag = "tool_vietsub"
                    )
                    CapCutToolIcon(
                        icon = Icons.Default.ClosedCaption,
                        label = "Phụ đề tự động",
                        tint = AmberGlow,
                        onClick = { viewModel.navigateTo(StudioScreen.VIETSUB_AUDIO) },
                        testTag = "tool_captions"
                    )
                    CapCutToolIcon(
                        icon = Icons.Default.Layers,
                        label = "Xóa nền",
                        tint = MintGreen,
                        onClick = { viewModel.navigateTo(StudioScreen.AI_SUITE) },
                        testTag = "tool_bg_remove"
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CapCutToolIcon(
                        icon = Icons.Default.CenterFocusStrong,
                        label = "Định khung lại",
                        tint = ElectricBlueColor,
                        onClick = { viewModel.navigateTo(StudioScreen.AI_SUITE) },
                        testTag = "tool_reframe"
                    )
                    CapCutToolIcon(
                        icon = Icons.Default.HighQuality,
                        label = "Nâng cấp 4K",
                        tint = HyperViolet,
                        onClick = { viewModel.navigateTo(StudioScreen.AI_SUITE) },
                        testTag = "tool_upscale"
                    )
                    CapCutToolIcon(
                        icon = Icons.Default.AutoAwesome,
                        label = "Mẫu AI",
                        tint = NeonCyan,
                        onClick = { viewModel.navigateTo(StudioScreen.TEMPLATES) },
                        testTag = "tool_templates"
                    )
                    CapCutToolIcon(
                        icon = Icons.Default.TextFields,
                        label = "Kịch bản AI",
                        tint = VividMagenta,
                        onClick = { viewModel.navigateTo(StudioScreen.AI_SUITE) },
                        testTag = "tool_script"
                    )
                }
            }
        }

        // Cloud Storage Banner (Đám mây CapCut)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(14.dp))
                    .clickable { viewModel.navigateTo(StudioScreen.ME_CLOUD) }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = "Cloud",
                                tint = MintGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = cloudSyncText,
                                color = StudioWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "Tự động sao lưu",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { 0.142f },
                        color = NeonCyan,
                        trackColor = StudioSurfaceElevated,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                }
            }
        }

        // Recent Projects (Dự án gần đây)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dự án gần đây",
                    color = StudioWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${projects.size} dự án",
                    color = StudioTextMuted,
                    fontSize = 12.sp
                )
            }
        }

        if (projects.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = "Trống",
                            tint = StudioTextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Chưa có dự án nào", color = StudioWhite, fontSize = 14.sp)
                        Text(
                            "Nhấn '+ Dự án mới' hoặc 'Mẫu AI' để bắt đầu chỉnh sửa",
                            color = StudioTextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            items(projects, key = { it.id }) { proj ->
                ProjectListItem(
                    project = proj,
                    onClick = { viewModel.openProjectInEditor(proj) }
                )
            }
        }
    }
}

val ElectricBlueColor = Color(0xFF38BDF8)

@Composable
fun CapCutToolIcon(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(76.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Surface(
            shape = CircleShape,
            color = tint.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(1.dp, tint.copy(alpha = 0.4f)),
            modifier = Modifier.size(50.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = tint,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = StudioWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ProjectListItem(
    project: Project,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .border(1.dp, StudioCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("project_item_${project.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 80.dp, height = 56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(HyperViolet.copy(alpha = 0.5f), NeonCyan.copy(alpha = 0.3f))
                        )
                    )
                    .border(0.5.dp, StudioCardBorder, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Movie,
                    contentDescription = null,
                    tint = StudioWhite,
                    modifier = Modifier.size(24.dp)
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(3.dp)
                ) {
                    Text(
                        text = project.aspectRatio,
                        color = StudioWhite,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.title,
                    color = StudioWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${project.durationSeconds.toInt()}s • ${project.resolution} • ${project.fps}fps • ${project.trackCount} luồng",
                    color = StudioTextMuted,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = "Đã đồng bộ",
                        tint = NeonCyan,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Đã lưu đám mây CapCut",
                        color = NeonCyan,
                        fontSize = 10.sp
                    )
                }
            }

            IconButton(
                onClick = onClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Mở dự án",
                    tint = NeonCyan,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
