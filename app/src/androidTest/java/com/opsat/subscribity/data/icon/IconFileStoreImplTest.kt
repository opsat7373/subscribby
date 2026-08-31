package com.opsat.subscribity.data.icon

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IconFileStoreImplTest {

    private val store = IconFileStoreImpl(ApplicationProvider.getApplicationContext())

    @Test
    fun savedPhotoRoundTripsAndCanBeDeleted() = runBlocking {
        val bytes = byteArrayOf(1, 2, 3, 4, 5)

        val relativePath = store.savePhoto(bytes)
        val file = store.photoFile(relativePath)

        assertArrayEquals(bytes, file.readBytes())

        store.deletePhoto(relativePath)

        assertFalse(file.exists())
    }
}
