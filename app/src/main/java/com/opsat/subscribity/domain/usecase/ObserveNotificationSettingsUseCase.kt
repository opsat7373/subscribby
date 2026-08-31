package com.opsat.subscribity.domain.usecase

import com.opsat.subscribity.domain.model.NotificationSettings
import com.opsat.subscribity.domain.repository.NotificationPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNotificationSettingsUseCase @Inject constructor(
    private val repository: NotificationPreferencesRepository,
) {
    operator fun invoke(): Flow<NotificationSettings> = repository.notificationSettings
}
