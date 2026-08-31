package com.opsat.subscribity.domain.usecase

import com.opsat.subscribity.domain.repository.NotificationPreferencesRepository
import javax.inject.Inject

class SetNotificationsEnabledUseCase @Inject constructor(
    private val repository: NotificationPreferencesRepository,
) {
    suspend operator fun invoke(enabled: Boolean) = repository.setEnabled(enabled)
}
