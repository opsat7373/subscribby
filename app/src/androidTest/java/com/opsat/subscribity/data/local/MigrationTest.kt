package com.opsat.subscribity.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val dbName = "migration-test.db"
    private val context: Context = ApplicationProvider.getApplicationContext()

    @After
    fun tearDown() {
        context.deleteDatabase(dbName)
    }

    @Test
    fun migration1To2ConvertsCustomDaysIntoCountAndUnit() = runBlocking {
        context.deleteDatabase(dbName)

        val v1Db = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(dbName), null)
        v1Db.execSQL(
            """
            CREATE TABLE subscriptions (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                icon TEXT NOT NULL,
                periodType TEXT NOT NULL,
                periodCustomDays INTEGER,
                price TEXT NOT NULL,
                currencyCode TEXT NOT NULL,
                nextPaymentDate INTEGER NOT NULL,
                isTrial INTEGER NOT NULL,
                trialPeriodType TEXT,
                trialPeriodCustomDays INTEGER,
                trialPrice TEXT,
                isSharedWithOthers INTEGER NOT NULL,
                personsCount INTEGER NOT NULL
            )
            """.trimIndent(),
        )
        v1Db.execSQL(
            """
            INSERT INTO subscriptions (
                name, icon, periodType, periodCustomDays, price, currencyCode, nextPaymentDate,
                isTrial, trialPeriodType, trialPeriodCustomDays, trialPrice, isSharedWithOthers, personsCount
            ) VALUES (
                'Gym', 'gym', 'CUSTOM', 45, '40', 'UAH', 20000, 0, NULL, NULL, NULL, 0, 1
            )
            """.trimIndent(),
        )
        v1Db.version = 1
        v1Db.close()

        val database = Room.databaseBuilder(context, SubscribityDatabase::class.java, dbName)
            .addMigrations(MIGRATION_1_2)
            .build()

        val migrated = database.subscriptionDao().observeSubscriptions().first().single()
        assertEquals("CUSTOM", migrated.periodType)
        assertEquals(45, migrated.periodCustomCount)
        assertEquals("DAYS", migrated.periodCustomUnit)

        database.close()
    }

    @Test
    fun migration2To3AddsNotificationsEnabledColumnDefaultingToTrue() = runBlocking {
        context.deleteDatabase(dbName)

        val v2Db = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(dbName), null)
        v2Db.execSQL(
            """
            CREATE TABLE subscriptions (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                icon TEXT NOT NULL,
                periodType TEXT NOT NULL,
                periodCustomCount INTEGER,
                periodCustomUnit TEXT,
                price TEXT NOT NULL,
                currencyCode TEXT NOT NULL,
                nextPaymentDate INTEGER NOT NULL,
                isTrial INTEGER NOT NULL,
                trialPeriodType TEXT,
                trialPeriodCustomCount INTEGER,
                trialPeriodCustomUnit TEXT,
                trialPrice TEXT,
                isSharedWithOthers INTEGER NOT NULL,
                personsCount INTEGER NOT NULL
            )
            """.trimIndent(),
        )
        v2Db.execSQL(
            """
            INSERT INTO subscriptions (
                name, icon, periodType, periodCustomCount, periodCustomUnit, price, currencyCode,
                nextPaymentDate, isTrial, trialPeriodType, trialPeriodCustomCount, trialPeriodCustomUnit,
                trialPrice, isSharedWithOthers, personsCount
            ) VALUES (
                'Netflix', 'netflix', 'MONTHLY', NULL, NULL, '15.99', 'USD', 20000, 0, NULL, NULL, NULL, NULL, 0, 1
            )
            """.trimIndent(),
        )
        v2Db.version = 2
        v2Db.close()

        val database = Room.databaseBuilder(context, SubscribityDatabase::class.java, dbName)
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()

        val migrated = database.subscriptionDao().observeSubscriptions().first().single()
        assertEquals(true, migrated.notificationsEnabled)

        database.close()
    }
}
