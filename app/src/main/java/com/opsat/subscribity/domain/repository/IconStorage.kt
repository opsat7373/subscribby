package com.opsat.subscribity.domain.repository

import java.io.File

interface IconStorage {
    /** Saves [bytes] as a new photo file and returns its path, relative to internal storage. */
    suspend fun savePhoto(bytes: ByteArray): String

    /** Resolves a path previously returned by [savePhoto] to a readable [File]. */
    fun photoFile(relativePath: String): File

    suspend fun deletePhoto(relativePath: String)
}
