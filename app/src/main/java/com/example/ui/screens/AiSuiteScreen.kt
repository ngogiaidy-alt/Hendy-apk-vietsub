package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioWhite
import com.example.viewmodel.StudioViewModel

data class AiSuiteTabItem(
    val title: String,
    val icon: ImageVector
)

@Composable
fun AiSuiteScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        AiSuiteTabItem("Video AI (Kling / VEO / Sora)", Icons.Default.Movie),
        AiSuiteTabItem("Hình ảnh AI (Seedream / GPT)", Icons.Default.Image),
        AiSuiteTabItem("Kịch bản Quảng cáo AI", Icons.Default.TextFields),
        AiSuiteTabItem("Phòng thu Âm thanh AI", Icons.Default.RecordVoiceOver)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
            .statusBarsPadding()
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = StudioSurface,
            contentColor = StudioWhite,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = NeonCyan,
                    height = 3.dp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ai_suite_tab_row")
        ) {
            tabs.forEachIndexed { index, item ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = item.title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) NeonCyan else StudioTextMuted
                        )
                    },
                    modifier = Modifier.testTag("ai_suite_tab_$index")
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            when (selectedTab) {
                0 -> AiVideoScreen(viewModel = viewModel)
                1 -> AiImageScreen(viewModel = viewModel)
                2 -> AdScriptScreen(viewModel = viewModel)
                3 -> AiAudioScreen(viewModel = viewModel)
            }
        }
    }
}
