package com.smartpantry.inventory.domain.model

import androidx.annotation.StringRes
import kotlinx.serialization.Serializable

@Serializable
enum class Status(@StringRes val labelRes: Int) {
    AVAILABLE(R.string.status_available),
    OPENED(R.string.status_opened),
    FROZEN(R.string.status_frozen),
    CONSUMED(R.string.status_consumed),
    EXPIRED(R.string.status_expired),
    DONATED(R.string.status_donated),
    DISCARDED(R.string.status_discarded)
}