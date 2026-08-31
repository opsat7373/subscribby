package com.opsat.subscribity.presentation.common

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/** Decodes and displays a local image [file] (e.g. a saved subscription icon photo), off the main thread. */
@Composable
fun LocalFileImage(file: File, contentDescription: String?, modifier: Modifier = Modifier) {
    val bitmapState = produceState<ImageBitmap?>(initialValue = null, file) {
        value = withContext(Dispatchers.IO) {
            runCatching { BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap() }.getOrNull()
        }
    }
    bitmapState.value?.let { bitmap ->
        Image(
            bitmap = bitmap,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop,
        )
    }
}
