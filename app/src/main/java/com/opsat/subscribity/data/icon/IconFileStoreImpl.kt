package com.opsat.subscribity.data.icon

import android.content.Context
import com.opsat.subscribity.domain.repository.IconStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

private const val ICON_DIRECTORY_NAME = "subscription_icons"

class IconFileStoreImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : IconStorage {

    override suspend fun savePhoto(bytes: ByteArray): String = withContext(Dispatchers.IO) {
        val directory = File(context.filesDir, ICON_DIRECTORY_NAME).apply { mkdirs() }
        val relativePath = "$ICON_DIRECTORY_NAME/${UUID.randomUUID()}.jpg"
        File(context.filesDir, relativePath).writeBytes(bytes)
        relativePath
    }

    override fun photoFile(relativePath: String): File = File(context.filesDir, relativePath)

    override suspend fun deletePhoto(relativePath: String) {
        withContext(Dispatchers.IO) {
            photoFile(relativePath).delete()
        }
    }
}
