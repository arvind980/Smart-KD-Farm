package com.smartkdfarm.app.core.domain.service

import com.smartkdfarm.app.core.domain.model.SmsDeliveryReport
import com.smartkdfarm.app.core.domain.model.SmsGatewayConfig

interface SmsGatewayService {
    /**
     * Send an SMS to [toPhoneNumber] using the supplied [config].
     * Returns a [SmsDeliveryReport] indicating success or failure.
     */
    suspend fun sendSms(
        farmId: String,
        notificationId: String,
        toPhoneNumber: String,
        message: String,
        config: SmsGatewayConfig,
    ): SmsDeliveryReport
}
