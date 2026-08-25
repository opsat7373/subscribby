package com.opsat.subscribity.presentation.common

import com.opsat.subscribity.domain.model.CustomPeriodUnit

fun customPeriodText(count: Int, unit: CustomPeriodUnit): String {
    val unitWord = when (unit) {
        CustomPeriodUnit.DAYS -> if (count == 1) "day" else "days"
        CustomPeriodUnit.WEEKS -> if (count == 1) "week" else "weeks"
        CustomPeriodUnit.MONTHS -> if (count == 1) "month" else "months"
    }
    return if (count == 1) "Every $unitWord" else "Every $count $unitWord"
}
