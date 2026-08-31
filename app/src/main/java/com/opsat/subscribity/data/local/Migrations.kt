package com.opsat.subscribity.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE subscriptions_new (
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
        db.execSQL(
            """
            INSERT INTO subscriptions_new
            SELECT id, name, icon, periodType, periodCustomDays,
                   CASE WHEN periodType = 'CUSTOM' THEN 'DAYS' END,
                   price, currencyCode, nextPaymentDate, isTrial,
                   trialPeriodType, trialPeriodCustomDays,
                   CASE WHEN trialPeriodType = 'CUSTOM' THEN 'DAYS' END,
                   trialPrice, isSharedWithOthers, personsCount
            FROM subscriptions
            """.trimIndent(),
        )
        db.execSQL("DROP TABLE subscriptions")
        db.execSQL("ALTER TABLE subscriptions_new RENAME TO subscriptions")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE subscriptions ADD COLUMN notificationsEnabled INTEGER NOT NULL DEFAULT 1")
    }
}
