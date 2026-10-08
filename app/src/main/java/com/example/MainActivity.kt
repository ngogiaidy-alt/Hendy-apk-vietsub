package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AdScriptScreen
import com.example.ui.screens.AiAudioScreen
import com.example.ui.screens.AiImageScreen
import com.example.ui.screens.AiSuiteScreen
import com.example.ui.screens.AiVideoScreen
import com.example.ui.screens.CameraRecordScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MeCloudScreen
import com.example.ui.screens.MultiTrackEditorScreen
import com.example.ui.screens.TemplatesScreen
import com.example.ui.screens.VietsubAudioScreen
import com.example.ui.theme.HyperViolet
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioWhite
import com.example.viewmodel.StudioScreen
import com.example.viewmodel.StudioViewModel

class MainActivity : ComponentActivity() {
    private val studioViewModel: StudioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainStudioApp(viewModel = studioViewModel)
            }
        }
    }
}

@Composable
fun MainStudioApp(viewModel: StudioViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    // Handle back press to always return to Edit (Home)
    if (currentScreen != StudioScreen.EDIT) {
        BackHandler {
            viewModel.navigateTo(StudioScreen.EDIT)
        }
    }

    val isFullScreenMode = currentScreen == StudioScreen.CAMERA_RECORD || currentScreen == StudioScreen.EDITOR

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = StudioBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            // Fullscreen camera and editor mode hide the bottom navigation bar
            if (!isFullScreenMode) {
                StudioBottomNav(
                    currentScreen = currentScreen,
                    onSelectScreen = { viewModel.navigateTo(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isFullScreenMode) WindowInsets(0, 0, 0, 0).asPaddingValues() else innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    StudioScreen.EDIT -> HomeScreen(viewModel = viewModel)
                    StudioScreen.TEMPLATES -> TemplatesScreen(viewModel = viewModel)
                    StudioScreen.AI_SUITE -> AiSuiteScreen(viewModel = viewModel)
                    StudioScreen.VIETSUB_AUDIO -> VietsubAudioScreen(viewModel = viewModel)
                    StudioScreen.ME_CLOUD -> MeCloudScreen(viewModel = viewModel)
                    StudioScreen.CAMERA_RECORD -> CameraRecordScreen(viewModel = viewModel)
                    StudioScreen.EDITOR -> MultiTrackEditorScreen(viewModel = viewModel)
                    StudioScreen.AI_VIDEO -> AiVideoScreen(viewModel = viewModel)
                    StudioScreen.AI_IMAGE -> AiImageScreen(viewModel = viewModel)
                    StudioScreen.AI_AUDIO -> AiAudioScreen(viewModel = viewModel)
                    StudioScreen.AD_SCRIPT -> AdScriptScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun WindowInsets.asPaddingValues() = androidx.compose.foundation.layout.PaddingValues(0.dp)

@Composable
fun StudioBottomNav(
    currentScreen: StudioScreen,
    onSelectScreen: (StudioScreen) -> Unit
) {
    val navItems = listOf(
        NavItem(StudioScreen.EDIT, "Chỉnh sửa", Icons.Default.VideoLibrary),
        NavItem(StudioScreen.TEMPLATES, "Mẫu AI", Icons.Default.AutoAwesome),
        NavItem(StudioScreen.AI_SUITE, "Công cụ AI", Icons.Default.SmartToy),
        NavItem(StudioScreen.VIETSUB_AUDIO, "Dịch & Audio", Icons.Default.Translate),
        NavItem(StudioScreen.ME_CLOUD, "Tôi", Icons.Default.Person)
    )

    NavigationBar(
        containerColor = StudioSurface,
        contentColor = StudioWhite,
        tonalElevation = 8.dp,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("studio_bottom_nav")
    ) {
        navItems.forEach { item ->
            val isSelected = currentScreen == item.screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelectScreen(item.screen) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NeonCyan,
                    selectedTextColor = NeonCyan,
                    indicatorColor = HyperViolet.copy(alpha = 0.35f),
                    unselectedIconColor = StudioTextMuted,
                    unselectedTextColor = StudioTextMuted
                ),
                modifier = Modifier.testTag("nav_item_${item.screen.name.lowercase()}")
            )
        }
    }
}

data class NavItem(
    val screen: StudioScreen,
    val label: String,
    val icon: ImageVector
)
