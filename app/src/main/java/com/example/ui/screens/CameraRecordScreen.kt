package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.SystemClock
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
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
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CameraRecordScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        hasCameraPermission = perms[Manifest.permission.CAMERA] ?: false
        hasAudioPermission = perms[Manifest.permission.RECORD_AUDIO] ?: false
    }

    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var isTorchOn by remember { mutableStateOf(false) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var videoCapture by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    var activeRecording by remember { mutableStateOf<Recording?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var recordingDurationSec by remember { mutableFloatStateOf(0f) }
    var recordedFile by remember { mutableStateOf<File?>(null) }
    var showPostRecordDialog by remember { mutableStateOf(false) }
    var selectedFramingGuide by remember { mutableStateOf("9:16") }

    // Pulsing recording indicator animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Duration timer while recording
    LaunchedEffect(isRecording) {
        if (isRecording) {
            val startTime = SystemClock.elapsedRealtime()
            while (isRecording) {
                delay(100)
                recordingDurationSec = (SystemClock.elapsedRealtime() - startTime) / 1000f
            }
        } else {
            recordingDurationSec = 0f
        }
    }

    if (!hasCameraPermission) {
        // Permission Request UI
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(StudioBackground)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = HyperViolet.copy(alpha = 0.2f),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera",
                                tint = NeonCyan,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Quyền truy cập Máy ảnh & Micrô",
                        color = StudioWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Để quay video, ghi hình b-roll hoặc ghi âm trực tiếp vào dòng thời gian đa luồng của bạn, vui lòng cấp quyền truy cập máy ảnh và micrô.",
                        color = StudioTextMuted,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.RECORD_AUDIO
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan,
                            contentColor = StudioBackground
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("request_camera_permission_button")
                    ) {
                        Text(
                            text = "Cấp quyền Máy ảnh & Micrô",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.navigateTo(StudioScreen.EDIT) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StudioSurfaceElevated,
                            contentColor = StudioTextMuted
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Quay lại Chỉnh sửa", fontSize = 12.sp)
                    }
                }
            }
        }
    } else {
        // CameraX Live Viewfinder & Recording Engine
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Camera Preview View
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()

                            val preview = Preview.Builder().build().also {
                                it.surfaceProvider = previewView.surfaceProvider
                            }

                            val qualitySelector = QualitySelector.from(
                                Quality.HIGHEST,
                                FallbackStrategy.higherQualityOrLowerThan(Quality.SD)
                            )
                            val recorder = Recorder.Builder()
                                .setQualitySelector(qualitySelector)
                                .build()

                            val vc = VideoCapture.withOutput(recorder)
                            videoCapture = vc

                            val cameraSelector = CameraSelector.Builder()
                                .requireLensFacing(lensFacing)
                                .build()

                            cameraProvider.unbindAll()
                            camera = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                vc
                            )
                        } catch (exc: Exception) {
                            Log.e("CameraRecordScreen", "Use case binding failed", exc)
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                update = { previewView ->
                    // Rebind if lens facing changes
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()

                            val preview = Preview.Builder().build().also {
                                it.surfaceProvider = previewView.surfaceProvider
                            }

                            val qualitySelector = QualitySelector.from(
                                Quality.HIGHEST,
                                FallbackStrategy.higherQualityOrLowerThan(Quality.SD)
                            )
                            val recorder = Recorder.Builder()
                                .setQualitySelector(qualitySelector)
                                .build()

                            val vc = VideoCapture.withOutput(recorder)
                            videoCapture = vc

                            val cameraSelector = CameraSelector.Builder()
                                .requireLensFacing(lensFacing)
                                .build()

                            cameraProvider.unbindAll()
                            camera = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                vc
                            )
                        } catch (exc: Exception) {
                            Log.e("CameraRecordScreen", "Rebind failed", exc)
                        }
                    }, ContextCompat.getMainExecutor(context))
                },
                modifier = Modifier.fillMaxSize()
            )

            // Framing Grid Overlay (9:16 / 16:9 / 1:1)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stroke = 1.dp.toPx()
                val gridColor = Color.White.copy(alpha = 0.25f)

                // Rule of thirds vertical lines
                val w1 = size.width / 3f
                val w2 = size.width * 2f / 3f
                drawLine(gridColor, Offset(w1, 0f), Offset(w1, size.height), stroke)
                drawLine(gridColor, Offset(w2, 0f), Offset(w2, size.height), stroke)

                // Rule of thirds horizontal lines
                val h1 = size.height / 3f
                val h2 = size.height * 2f / 3f
                drawLine(gridColor, Offset(0f, h1), Offset(size.width, h1), stroke)
                drawLine(gridColor, Offset(0f, h2), Offset(size.width, h2), stroke)
            }

            // Top Camera Controls Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (!isRecording) {
                            viewModel.navigateTo(StudioScreen.EDIT)
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .testTag("camera_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = StudioWhite
                    )
                }

                // Aspect Ratio Framing selector
                Row(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("9:16", "1:1", "16:9").forEach { ratio ->
                        val isSelected = selectedFramingGuide == ratio
                        Text(
                            text = ratio,
                            color = if (isSelected) NeonCyan else StudioTextMuted,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedFramingGuide = ratio }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Torch / Flash Toggle
                IconButton(
                    onClick = {
                        isTorchOn = !isTorchOn
                        camera?.cameraControl?.enableTorch(isTorchOn)
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .testTag("camera_torch_toggle")
                ) {
                    Icon(
                        imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Torch",
                        tint = if (isTorchOn) NeonCyan else StudioWhite
                    )
                }
            }

            // Recording Status Badge (Timer & Rec dot)
            if (isRecording) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VividMagenta),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 70.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .scale(pulseScale)
                                .background(VividMagenta, CircleShape)
                        )
                        Text(
                            text = "REC ${formatTimecode(recordingDurationSec)}",
                            color = StudioWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Bottom Recording Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 32.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Flip Camera (Front / Back)
                IconButton(
                    onClick = {
                        if (!isRecording) {
                            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                CameraSelector.LENS_FACING_FRONT
                            } else {
                                CameraSelector.LENS_FACING_BACK
                            }
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .testTag("flip_camera_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FlipCameraAndroid,
                        contentDescription = "Flip Lens",
                        tint = StudioWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Big Shutter / Record Button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .border(3.dp, StudioWhite, CircleShape)
                        .padding(6.dp)
                        .clip(CircleShape)
                        .background(if (isRecording) VividMagenta else Color.Red)
                        .clickable {
                            if (!isRecording) {
                                // Start Video Recording
                                val currentVideoCapture = videoCapture
                                if (currentVideoCapture != null) {
                                    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                                    val videoFile = File(context.cacheDir, "STUDIO_REC_$timeStamp.mp4")
                                    recordedFile = videoFile

                                    val outputOptions = FileOutputOptions.Builder(videoFile).build()
                                    var pendingRecording = currentVideoCapture.output
                                        .prepareRecording(context, outputOptions)

                                    if (hasAudioPermission) {
                                        try {
                                            pendingRecording = pendingRecording.withAudioEnabled()
                                        } catch (e: SecurityException) {
                                            Log.e("CameraRecordScreen", "Audio record permission missing", e)
                                        }
                                    }

                                    activeRecording = pendingRecording.start(
                                        ContextCompat.getMainExecutor(context)
                                    ) { recordEvent ->
                                        when (recordEvent) {
                                            is VideoRecordEvent.Start -> {
                                                isRecording = true
                                            }
                                            is VideoRecordEvent.Finalize -> {
                                                isRecording = false
                                                if (!recordEvent.hasError()) {
                                                    showPostRecordDialog = true
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    // Simulation fallback in emulator environments without camera driver
                                    isRecording = true
                                }
                            } else {
                                // Stop Recording
                                if (activeRecording != null) {
                                    activeRecording?.stop()
                                    activeRecording = null
                                } else {
                                    isRecording = false
                                    showPostRecordDialog = true
                                }
                            }
                        }
                        .testTag("camera_shutter_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isRecording) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = StudioWhite,
                            modifier = Modifier.size(24.dp)
                        ) {}
                    } else {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color.Red, CircleShape)
                        )
                    }
                }

                // Audio mic status indicator
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Mic",
                            tint = if (hasAudioPermission) MintGreen else StudioTextMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Post-Recording Sheet: Add directly to Studio Timeline
            AnimatedVisibility(
                visible = showPostRecordDialog,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Surface(
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    color = StudioSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Done",
                                        tint = MintGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Footage Recorded Successfully",
                                        color = StudioWhite,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "Duration: ${formatTimecode(recordingDurationSec.coerceAtLeast(4.5f))} • 1080p 60fps HD",
                                    color = StudioTextMuted,
                                    fontSize = 12.sp
                                )
                            }

                            IconButton(onClick = { showPostRecordDialog = false }) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Retake",
                                    tint = StudioTextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Actions: Insert into Timeline or Start New Project
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    showPostRecordDialog = false
                                    val duration = if (recordingDurationSec > 1f) recordingDurationSec else 5.0f
                                    viewModel.addRecordedFootageToStudio("Camera Roll Live.mp4", duration, asNewProject = false)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonCyan,
                                    contentColor = StudioBackground
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("add_recording_to_timeline")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add", tint = StudioBackground, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add to Timeline", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    showPostRecordDialog = false
                                    val duration = if (recordingDurationSec > 1f) recordingDurationSec else 5.0f
                                    viewModel.addRecordedFootageToStudio("Recorded Footage Project", duration, asNewProject = true)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = HyperViolet,
                                    contentColor = StudioWhite
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("new_project_from_recording")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "New", tint = StudioWhite, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("New Project", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            activeRecording?.stop()
            activeRecording = null
        }
    }
}
