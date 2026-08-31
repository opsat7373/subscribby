package com.opsat.subscribity.testing

import com.opsat.subscribity.domain.repository.IconStorage
import java.io.File
import java.util.UUID

class FakeIconStorage : IconStorage {
    val saved = mutableMapOf<String, ByteArray>()
    val deleted = mutableListOf<String>()

    override suspend fun savePhoto(bytes: ByteArray): String {
        val path = "subscription_icons/${UUID.randomUUID()}.jpg"
        saved[path] = bytes
        return path
    }

    override fun photoFile(relativePath: String): File = File(relativePath)

    override suspend fun deletePhoto(relativePath: String) {
        saved.remove(relativePath)
        deleted += relativePath
    }
}
