package com.opsat.subscribity.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDate

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val icon: String,
    val periodType: String,
    val periodCustomDays: Int?,
    val price: BigDecimal,
    val currencyCode: String,
    val nextPaymentDate: LocalDate,
    val isTrial: Boolean,
    val trialPeriodType: String?,
    val trialPeriodCustomDays: Int?,
    val trialPrice: BigDecimal?,
    val isSharedWithOthers: Boolean,
    val personsCount: Int,
)
