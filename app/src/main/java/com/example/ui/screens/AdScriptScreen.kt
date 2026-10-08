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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TrackType
import com.example.ui.components.StudioHeader
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.HyperViolet
import com.example.ui.theme.MintGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioCardBorder
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.StudioTextMuted
import com.example.ui.theme.StudioWhite
import com.example.ui.theme.VividMagenta
import com.example.viewmodel.StudioScreen
import com.example.viewmodel.StudioViewModel

@Composable
fun AdScriptScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val productName by viewModel.productName.collectAsState()
    val targetAudience by viewModel.targetAudience.collectAsState()
    val scriptTone by viewModel.scriptTone.collectAsState()
    val scriptFormat by viewModel.scriptFormat.collectAsState()
    val generatedScript by viewModel.generatedScript.collectAsState()
    val isGenerating by viewModel.isScriptGenerating.collectAsState()

    val tones = listOf("Viral Hook & Urgency", "Luxury & Minimalist", "Educational & Founder", "Humorous & Casual")
    val formats = listOf("TikTok / Reels 30s", "YouTube Shorts 60s", "Commercial 15s")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            StudioHeader(
                title = "AI Ad Script Writer",
                subtitle = "High-Converting Viral Hooks, Visual Cues & Dialogue",
                isCloudSynced = true
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Product input
                Text("Product / Brand Name", color = StudioWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = productName,
                    onValueChange = { viewModel.setProductName(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ad_product_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedContainerColor = StudioSurfaceVariant,
                        unfocusedContainerColor = StudioSurfaceVariant,
                        focusedTextColor = StudioWhite,
                        unfocusedTextColor = StudioWhite
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Target audience input
                Text("Target Demographic / Audience", color = StudioWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = targetAudience,
                    onValueChange = { viewModel.setTargetAudience(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ad_audience_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedContainerColor = StudioSurfaceVariant,
                        unfocusedContainerColor = StudioSurfaceVariant,
                        focusedTextColor = StudioWhite,
                        unfocusedTextColor = StudioWhite
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Tone selection
                Text("Marketing Tone", color = StudioWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tones.forEach { tone ->
                        val isSelected = scriptTone == tone
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) HyperViolet.copy(alpha = 0.3f) else StudioSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NeonCyan else StudioCardBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.setScriptTone(tone) }
                        ) {
                            Text(
                                text = tone,
                                color = if (isSelected) NeonCyan else StudioTextMuted,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Format selection
                Text("Video Format Duration", color = StudioWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    formats.forEach { fmt ->
                        val isSelected = scriptFormat == fmt
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) HyperViolet.copy(alpha = 0.3f) else StudioSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) NeonCyan else StudioCardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.setScriptFormat(fmt) }
                        ) {
                            Text(
                                text = fmt,
                                color = if (isSelected) NeonCyan else StudioTextMuted,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.generateAdScript() },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = StudioBackground),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("generate_script_button")
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = StudioBackground, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Drafting Viral Script...", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Gen", tint = StudioBackground, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate AI Ad Script & Visual Cues", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Script output
        if (generatedScript != null) {
            val script = generatedScript!!
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Text("Generated Production Script (${script.estimatedDuration})", color = StudioWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    ScriptBlockCard(label = "01. HOOK (0-3s)", content = script.hook, color = VividMagenta)
                    Spacer(modifier = Modifier.height(8.dp))
                    ScriptBlockCard(label = "02. PROBLEM", content = script.problem, color = AmberGlow)
                    Spacer(modifier = Modifier.height(8.dp))
                    ScriptBlockCard(label = "03. SOLUTION", content = script.solution, color = MintGreen)
                    Spacer(modifier = Modifier.height(8.dp))
                    ScriptBlockCard(label = "04. CALL TO ACTION (CTA)", content = script.callToAction, color = NeonCyan)

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Visual Cues & Camera Direction", color = StudioWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, StudioCardBorder, RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            script.visualCues.forEach { cue ->
                                Text("• $cue", color = StudioTextMuted, fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.padding(vertical = 2.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 1-Click Send Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.setVideoPrompt("High-energy commercial video: ${script.hook}")
                                viewModel.navigateTo(StudioScreen.AI_VIDEO)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HyperViolet, contentColor = StudioWhite),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("send_script_to_video")
                        ) {
                            Icon(Icons.Default.Movie, contentDescription = "Video", tint = StudioWhite, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Send to Video AI", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.setTtsInputText("${script.hook} ${script.solution} ${script.callToAction}")
                                viewModel.navigateTo(StudioScreen.AI_AUDIO)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StudioSurfaceElevated, contentColor = NeonCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("send_script_to_tts")
                        ) {
                            Icon(Icons.Default.RecordVoiceOver, contentDescription = "Voice", tint = NeonCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Generate Voiceover", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScriptBlockCard(
    label: String,
    content: String,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, StudioCardBorder, RoundedCornerShape(10.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("\"$content\"", color = StudioWhite, fontSize = 12.sp, lineHeight = 17.sp)
        }
    }
}
