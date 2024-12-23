package com.novaservices.netwalk.ui.auth

import android.Manifest
import android.R.attr.bitmap
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.novaservices.netwalk.R
import com.novaservices.netwalk.ui.auth.ui.theme.NetWalkTheme
import com.plcoding.cameraxguide.CameraPreview
import com.plcoding.cameraxguide.MainViewModel
import com.plcoding.cameraxguide.PhotoBottomSheetContent
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


class CameraXActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    private lateinit var longstr: String;
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(
                this, CAMERAX_PERMISSIONS, 0
            )
        }
        setContent {
            NetWalkTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                    AndroidView(factory = { context->
                        View.inflate(context, R.layout.camera,null)
                    }, update = { view ->

                        // Here we bind a variable with findviewbyid to access compose

                        val composeView: ComposeView = view.findViewById(R.id.compose_view)
                        composeView.setContent {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "Hello World Compose", color = Color.Black, fontSize = 32.sp)
                            }

                            val scope = rememberCoroutineScope()
                            val scaffoldState = rememberBottomSheetScaffoldState()
                            val controller = remember {
                                LifecycleCameraController(applicationContext).apply {
                                    setEnabledUseCases(
                                        CameraController.IMAGE_CAPTURE or
                                                CameraController.VIDEO_CAPTURE
                                    )
                                }
                            }
                            val viewModel = viewModel<MainViewModel>()
                            val bitmaps by viewModel.bitmaps.collectAsState()

                            BottomSheetScaffold(
                                scaffoldState = scaffoldState,
                                sheetPeekHeight = 0.dp,
                                sheetContent = {
                                    PhotoBottomSheetContent(
                                        bitmaps = bitmaps,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                }
                            ) { padding ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(padding)
                                ) {
                                    CameraPreview(
                                        controller = controller,
                                        modifier = Modifier
                                            .fillMaxSize()
                                    )

                                    IconButton(
                                        onClick = {
                                            controller.cameraSelector =
                                                if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                                    CameraSelector.DEFAULT_FRONT_CAMERA
                                                } else CameraSelector.DEFAULT_BACK_CAMERA
                                        },
                                        modifier = Modifier
                                            .offset(16.dp, 16.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Cameraswitch,
                                            contentDescription = "Switch camera"
                                        )
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.BottomCenter)
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        IconButton(
                                            onClick = {
                                                scope.launch {
                                                    scaffoldState.bottomSheetState.expand()
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Photo,
                                                contentDescription = "Open gallery"
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                findViewById<TextView>(R.id.my).setText(longstr)
                                                takePhoto(
                                                    controller = controller,
                                                    onPhotoTaken = viewModel::onTakePhoto
                                                )
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PhotoCamera,
                                                contentDescription = "Take photo"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    })
                }
            }
        }
    }
    private fun takePhoto(
        controller: LifecycleCameraController,
        onPhotoTaken: (Bitmap) -> Unit,

    ) {
        controller.takePicture(
            ContextCompat.getMainExecutor(applicationContext),
            object : OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    val matrix = Matrix().apply {
                        postRotate(image.imageInfo.rotationDegrees.toFloat())
                    }
                    val rotatedBitmap = Bitmap.createBitmap(
                        image.toBitmap(),
                        0,
                        0,
                        image.width,
                        image.height,
                        matrix,
                        true
                    )

                    Log.i("new bitmap", rotatedBitmap.toString())
                    val byteArrayOutputStream: ByteArrayOutputStream = ByteArrayOutputStream()
                    rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                    val encoded: String = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    Log.i("new bitmap", encoded)
                    longstr = encoded


                    onPhotoTaken(rotatedBitmap)
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("Camera", "Couldn't take photo: ", exception)
                }
            }
        )
    }

    private fun hasRequiredPermissions(): Boolean {
        return CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val CAMERAX_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NetWalkTheme {
        Greeting("Android")
    }
}