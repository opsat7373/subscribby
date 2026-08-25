package com.opsat.subscribity.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class SubscriptionDaoTest {

    private lateinit var database: SubscribityDatabase
    private lateinit var dao: SubscriptionDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SubscribityDatabase::class.java,
        ).build()
        dao = database.subscriptionDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndReadSubscription() = runBlocking {
        val entity = SubscriptionEntity(
            name = "Netflix",
            icon = "netflix",
            periodType = "MONTHLY",
            periodCustomDays = null,
            price = BigDecimal("15.99"),
            currencyCode = "USD",
            nextPaymentDate = LocalDate.of(2026, 9, 1),
            isTrial = false,
            trialPeriodType = null,
            trialPeriodCustomDays = null,
            trialPrice = null,
            isSharedWithOthers = true,
            personsCount = 3,
        )

        val generatedId = dao.insert(entity)

        val stored = dao.getSubscription(generatedId)
        assertNotNull(stored)
        assertEquals(entity.copy(id = generatedId), stored)

        val observed = dao.observeSubscriptions().first()
        assertEquals(listOf(entity.copy(id = generatedId)), observed)
    }

    @Test
    fun insertCustomPeriodAndUpdate() = runBlocking {
        val entity = SubscriptionEntity(
            name = "Gym",
            icon = "gym",
            periodType = "CUSTOM",
            periodCustomDays = 45,
            price = BigDecimal("40"),
            currencyCode = "UAH",
            nextPaymentDate = LocalDate.of(2026, 10, 15),
            isTrial = true,
            trialPeriodType = "WEEKLY",
            trialPeriodCustomDays = null,
            trialPrice = BigDecimal.ZERO,
            isSharedWithOthers = false,
            personsCount = 1,
        )
        val generatedId = dao.insert(entity)

        val updated = entity.copy(id = generatedId, price = BigDecimal("45"))
        dao.update(updated)

        val stored = dao.getSubscription(generatedId)
        assertEquals(updated, stored)
    }

    @Test
    fun deleteByIdRemovesTheRow() = runBlocking {
        val entity = SubscriptionEntity(
            name = "Spotify",
            icon = "spotify",
            periodType = "MONTHLY",
            periodCustomDays = null,
            price = BigDecimal("9.99"),
            currencyCode = "USD",
            nextPaymentDate = LocalDate.of(2026, 9, 6),
            isTrial = false,
            trialPeriodType = null,
            trialPeriodCustomDays = null,
            trialPrice = null,
            isSharedWithOthers = false,
            personsCount = 1,
        )
        val generatedId = dao.insert(entity)

        dao.deleteById(generatedId)

        assertNull(dao.getSubscription(generatedId))
        assertEquals(emptyList<SubscriptionEntity>(), dao.observeSubscriptions().first())
    }
}
