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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.ModelBadge
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
fun AiImageScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "Text to Image",
        "Style Transfer",
        "4K Upscale & Restore",
        "BG Remover & Gen",
        "Object & Text Remover",
        "AI Model & Product",
        "AI Design & Poster"
    )

    val prompt by viewModel.imagePrompt.collectAsState()
    val model by viewModel.imageModel.collectAsState()
    val style by viewModel.imageStyle.collectAsState()
    val isProcessing by viewModel.isImageProcessing.collectAsState()
    val resultStatus by viewModel.imageResultStatus.collectAsState()

    val modelsList = listOf("Seedream 5.0", "GPT Image 2.5", "Kling Vision 3.0", "DALL-E HD")
    val stylesList = listOf("Photorealistic", "Anime 4K", "3D Pixar Art", "Cyberpunk", "Watercolor", "Oil Painting")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            StudioHeader(
                title = "AI Image & Design Studio",
                subtitle = "Seedream 5.0 • GPT Image 2.5 • Lookbook & Product Studio",
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
                        modifier = Modifier.testTag("image_tab_$index")
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> {
                // Text to Image (Seedream 5.0, GPT Image 2.5)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Generative Model", color = StudioWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            modelsList.forEach { m ->
                                ModelBadge(
                                    modelName = m,
                                    isSelected = model == m,
                                    onClick = { viewModel.setImageModel(m) },
                                    badgeText = if (m.contains("5.0")) "v5.0" else "HD"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Artistic Style", color = StudioWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            stylesList.forEach { s ->
                                val isSelected = style == s
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) HyperViolet.copy(alpha = 0.3f) else StudioSurfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NeonCyan else StudioCardBorder),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { viewModel.setImageStyle(s) }
                                ) {
                                    Text(
                                        text = s,
                                        color = if (isSelected) NeonCyan else StudioTextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Image Prompt", color = StudioWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = prompt,
                            onValueChange = { viewModel.setImagePrompt(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("image_prompt_input"),
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

                        Spacer(modifier = Modifier.height(12.dp))

                        StatusFeedbackBanner(statusText = resultStatus, isLoading = isProcessing)

                        Button(
                            onClick = { viewModel.generateTextImage() },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = StudioBackground),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("generate_image_button")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Gen", tint = StudioBackground, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate 4K Image with $model", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        // Preview result
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, StudioCardBorder, RoundedCornerShape(14.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.product_studio_demo),
                                contentDescription = "Sample Render",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.Black.copy(alpha = 0.75f),
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "$model • $style • 4K",
                                    color = NeonCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
            1 -> {
                // Style Transfer (Image to Image)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Image-to-Image Style Transfer", color = StudioWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Transforms original source photos into anime, cyberpunk, 3D clay, or illustration while keeping facial structure.",
                            color = StudioTextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf("Anime 4K", "3D Pixar Art", "Cyberpunk Neon", "Oil Painting").forEach { s ->
                                Button(
                                    onClick = { viewModel.runImageToImage(s) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = StudioSurfaceVariant,
                                        contentColor = StudioWhite
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .border(1.dp, StudioCardBorder, RoundedCornerShape(10.dp))
                                ) {
                                    Text(s, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        StatusFeedbackBanner(statusText = resultStatus, isLoading = isProcessing)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, StudioCardBorder, RoundedCornerShape(14.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.lookbook_ai_model),
                                contentDescription = "Source",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
            2 -> {
                // 4K Upscaler, Old Photo Restoration, Photo Colorizer, AI Color Correction
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("4K Upscaler & Photo Restoration Suite", color = StudioWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Restore damaged old photos, colorize black & white memories, and upscale resolution to 4K UHD.",
                            color = StudioTextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        // 4 Tool Actions
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            ToolActionCard(
                                title = "Image Upscaler (4K UHD)",
                                description = "Sharpens blurry or grainy images with 4x super-resolution.",
                                icon = Icons.Default.HighQuality,
                                onClick = { viewModel.runImageUpscaler() }
                            )
                            ToolActionCard(
                                title = "Old Photo Restoration",
                                description = "Automatically detects and removes scratches, tears, and dust marks.",
                                icon = Icons.Default.HistoryEdu,
                                onClick = { viewModel.runOldPhotoRestoration() }
                            )
                            ToolActionCard(
                                title = "Photo Colorizer",
                                description = "Transforms vintage black-and-white photos into natural vibrant colors.",
                                icon = Icons.Default.ColorLens,
                                onClick = { viewModel.runPhotoColorizer() }
                            )
                            ToolActionCard(
                                title = "AI Color Correction",
                                description = "Balances exposure, vibrant saturation, and HDR dynamic tone mapping.",
                                icon = Icons.Default.Palette,
                                onClick = { viewModel.runColorCorrection() }
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        StatusFeedbackBanner(statusText = resultStatus, isLoading = isProcessing)
                    }
                }
            }
            3 -> {
                // Background Remover, AI Background Generator, Image Resizer
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Image BG Remover, Generator & Resizer", color = StudioWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "1-click clean subject extraction, custom generative background synthesis, and social media auto-resizing.",
                            color = StudioTextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.runImageBgRemoval() },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = StudioBackground),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                            ) {
                                Icon(Icons.Default.Layers, contentDescription = "Remove", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Remove BG", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { viewModel.runAiBgGeneration("Luxury Marble Studio") },
                                colors = ButtonDefaults.buttonColors(containerColor = HyperViolet, contentColor = StudioWhite),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                            ) {
                                Icon(Icons.Default.Wallpaper, contentDescription = "Gen BG", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Generate AI BG", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Social Media Resizer Presets", color = StudioWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("TikTok (9:16)", "IG Post (1:1)", "YouTube (16:9)").forEach { platform ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = StudioSurfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { viewModel.runImageResizer(platform) }
                                ) {
                                    Text(
                                        text = platform,
                                        color = StudioWhite,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        StatusFeedbackBanner(statusText = resultStatus, isLoading = isProcessing)
                    }
                }
            }
            4 -> {
                // AI Object & Text Remover (Inpainting Canvas)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("AI Object & Watermark Remover", color = StudioWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Interactive neural inpainting brush: highlight photobombers, text, logos, or wires to erase without seams.",
                            color = StudioTextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        // Inpainting Canvas Simulation
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, HyperViolet, RoundedCornerShape(14.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.lookbook_ai_model),
                                contentDescription = "Inpaint Canvas",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Brush mask indicator overlay
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = VividMagenta.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .padding(start = 60.dp, top = 80.dp)
                                    .size(80.dp, 40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("Erase Area", color = StudioWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.Black.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "Brush Size: 32px • Smart Edge Snap",
                                    color = NeonCyan,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        StatusFeedbackBanner(statusText = resultStatus, isLoading = isProcessing)

                        Button(
                            onClick = { viewModel.runObjectRemover() },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = StudioBackground),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("run_object_remover")
                        ) {
                            Icon(Icons.Default.Brush, contentDescription = "Erase", tint = StudioBackground, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Erase Highlighted Object / Text", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
            5 -> {
                // AI Model & Product Photos
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("AI Virtual Model & Product Photography", color = StudioWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Superimpose lookbook fashion onto virtual models or generate luxury commercial studio lighting for products.",
                            color = StudioTextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, StudioCardBorder, RoundedCornerShape(12.dp))
                                    .clickable { viewModel.runAiModelFit() }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Icon(Icons.Default.Face, contentDescription = "Model", tint = NeonCyan)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Virtual Model Fit", color = StudioWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Fit lookbook outfits onto realistic virtual human models.", color = StudioTextMuted, fontSize = 11.sp)
                                }
                            }
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, StudioCardBorder, RoundedCornerShape(12.dp))
                                    .clickable { viewModel.runProductStudio() }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Icon(Icons.Default.PhotoCamera, contentDescription = "Product", tint = HyperViolet)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Product Studio", color = StudioWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Generate luxury water ripples, marble & studio lighting.", color = StudioTextMuted, fontSize = 11.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        StatusFeedbackBanner(statusText = resultStatus, isLoading = isProcessing)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.lookbook_ai_model),
                                contentDescription = "Model",
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Image(
                                painter = painterResource(id = R.drawable.product_studio_demo),
                                contentDescription = "Product",
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
            6 -> {
                // AI Design (Posters & Ad Banners)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("AI Design: Auto Ad Posters & Social Banners", color = StudioWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Automatically formats layout, typography, discount badges, and background elements into high-converting graphic designs.",
                            color = StudioTextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        StatusFeedbackBanner(statusText = resultStatus, isLoading = isProcessing)

                        Button(
                            onClick = { viewModel.runAiDesignLayout() },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = StudioBackground),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("run_ai_design")
                        ) {
                            Icon(Icons.Default.Style, contentDescription = "Design", tint = StudioBackground, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate AI Poster Layout", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, StudioCardBorder, RoundedCornerShape(14.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.hero_studio_banner),
                                contentDescription = "Poster Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = VividMagenta
                                ) {
                                    Text(
                                        text = "LIMITED SUMMER SALE 50% OFF",
                                        color = StudioWhite,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "FUTURE ARRIVES NOW",
                                    color = StudioWhite,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ToolActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = StudioSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = HyperViolet.copy(alpha = 0.2f),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = title, tint = NeonCyan, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = StudioWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(description, color = StudioTextMuted, fontSize = 11.sp, lineHeight = 14.sp)
            }
        }
    }
}
