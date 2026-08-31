package com.opsat.subscribity.domain.usecase

import com.opsat.subscribity.domain.repository.NotificationPreferencesRepository
import javax.inject.Inject

class SetReminderTimeUseCase @Inject constructor(
    private val repository: NotificationPreferencesRepository,
) {
    suspend operator fun invoke(hour: Int, minute: Int) = repository.setReminderTime(hour, minute)
}
