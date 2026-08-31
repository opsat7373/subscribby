package com.opsat.subscribity.domain.model

data class NotificationSettings(
    val enabled: Boolean = true,
    val daysBefore: Int = 3,
    val hour: Int = 10,
    val minute: Int = 0,
)
