package com.smartkdfarm.app.core.data

import com.smartkdfarm.app.core.domain.model.IdGenerator
import com.smartkdfarm.app.core.domain.model.SmsDeliveryReport
import com.smartkdfarm.app.core.domain.model.SmsGatewayConfig
import com.smartkdfarm.app.core.domain.model.SmsProvider
import com.smartkdfarm.app.core.domain.model.TimeProvider
import com.smartkdfarm.app.core.domain.service.SmsGatewayService
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * HTTP-based SMS delivery implementation.
 *
 * Supports MSG91, Fast2SMS, Twilio and a generic CUSTOM_HTTP webhook.
 * Firestore is NOT used here — delivery reports are returned to callers
 * who can persist them via the [OuterCenterRepository] notification record.
 */
class SmsGatewayServiceImpl : SmsGatewayService {

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    override suspend fun sendSms(
        farmId: String,
        notificationId: String,
        toPhoneNumber: String,
        message: String,
        config: SmsGatewayConfig,
    ): SmsDeliveryReport {
        return when (config.provider) {
            SmsProvider.MSG91     -> sendViaMsg91(farmId, notificationId, toPhoneNumber, message, config)
            SmsProvider.FAST2SMS  -> sendViaFast2Sms(farmId, notificationId, toPhoneNumber, message, config)
            SmsProvider.TWILIO    -> sendViaTwilio(farmId, notificationId, toPhoneNumber, message, config)
            SmsProvider.CUSTOM_HTTP -> sendViaCustomHttp(farmId, notificationId, toPhoneNumber, message, config)
        }
    }

    // ─── MSG91 ────────────────────────────────────────────────────────────────

    private suspend fun sendViaMsg91(
        farmId: String,
        notificationId: String,
        to: String,
        message: String,
        config: SmsGatewayConfig,
    ): SmsDeliveryReport {
        return try {
            val url = "https://api.msg91.com/api/sendhttp.php" +
                "?authkey=${config.apiKey}" +
                "&mobiles=91${to.trimStart('+').trimStart('9','1')}" +
                "&message=${message.encodeUrl()}" +
                "&sender=${config.senderId}" +
                "&route=${config.routeId ?: "4"}"

            val response = httpClient.get(url)
            val body = response.bodyAsText()
            val success = response.status == HttpStatusCode.OK && body.startsWith("0")
            buildReport(farmId, notificationId, to, config.provider, body, response.status.value, success)
        } catch (e: Exception) {
            buildErrorReport(farmId, notificationId, to, config.provider, e.message)
        }
    }

    // ─── Fast2SMS ─────────────────────────────────────────────────────────────

    private suspend fun sendViaFast2Sms(
        farmId: String,
        notificationId: String,
        to: String,
        message: String,
        config: SmsGatewayConfig,
    ): SmsDeliveryReport {
        return try {
            val url = "https://www.fast2sms.com/dev/bulkV2" +
                "?authorization=${config.apiKey}" +
                "&message=${message.encodeUrl()}" +
                "&language=english" +
                "&route=v3" +
                "&numbers=${to.trimStart('+').takeLast(10)}"

            val response = httpClient.get(url) {
                header("cache-control", "no-cache")
            }
            val body = response.bodyAsText()
            val success = response.status == HttpStatusCode.OK && body.contains("\"return\":true")
            buildReport(farmId, notificationId, to, config.provider, body, response.status.value, success)
        } catch (e: Exception) {
            buildErrorReport(farmId, notificationId, to, config.provider, e.message)
        }
    }

    // ─── Twilio ───────────────────────────────────────────────────────────────

    private suspend fun sendViaTwilio(
        farmId: String,
        notificationId: String,
        to: String,
        message: String,
        config: SmsGatewayConfig,
    ): SmsDeliveryReport {
        return try {
            // config.apiKey = "ACXXXXX:authToken"
            val parts = config.apiKey.split(":")
            val accountSid = parts.getOrNull(0) ?: error("Missing Twilio Account SID")
            val authToken  = parts.getOrNull(1) ?: error("Missing Twilio Auth Token")
            val url = "https://api.twilio.com/2010-04-01/Accounts/$accountSid/Messages.json"

            val response = httpClient.post(url) {
                header("Authorization", "Basic " + "$accountSid:$authToken".encodeBase64())
                contentType(ContentType.Application.FormUrlEncoded)
                setBody("From=${config.senderId.encodeUrl()}&To=${to.encodeUrl()}&Body=${message.encodeUrl()}")
            }
            val body = response.bodyAsText()
            val success = response.status.value in 200..299
            buildReport(farmId, notificationId, to, config.provider, body, response.status.value, success)
        } catch (e: Exception) {
            buildErrorReport(farmId, notificationId, to, config.provider, e.message)
        }
    }

    // ─── Custom HTTP webhook ──────────────────────────────────────────────────

    private suspend fun sendViaCustomHttp(
        farmId: String,
        notificationId: String,
        to: String,
        message: String,
        config: SmsGatewayConfig,
    ): SmsDeliveryReport {
        val baseUrl = config.baseUrl ?: return buildErrorReport(
            farmId, notificationId, to, config.provider, "Custom HTTP baseUrl not configured"
        )
        return try {
            val response = httpClient.post(baseUrl) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer ${config.apiKey}")
                setBody("""{"to":"$to","message":"${message.replace("\"","\\\"")}", "sender":"${config.senderId}"}""")
            }
            val body = response.bodyAsText()
            val success = response.status.value in 200..299
            buildReport(farmId, notificationId, to, config.provider, body, response.status.value, success)
        } catch (e: Exception) {
            buildErrorReport(farmId, notificationId, to, config.provider, e.message)
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private fun buildReport(
        farmId: String,
        notificationId: String,
        to: String,
        provider: SmsProvider,
        rawBody: String,
        statusCode: Int,
        isSuccess: Boolean,
    ) = SmsDeliveryReport(
        id = IdGenerator.newId(prefix = "sms_report"),
        farmId = farmId,
        notificationId = notificationId,
        toPhoneNumber = to,
        provider = provider,
        externalMessageId = rawBody.take(80),
        statusCode = statusCode,
        isSuccess = isSuccess,
        sentAtEpochMillis = TimeProvider.nowEpochMillis(),
    )

    private fun buildErrorReport(
        farmId: String,
        notificationId: String,
        to: String,
        provider: SmsProvider,
        error: String?,
    ) = SmsDeliveryReport(
        id = IdGenerator.newId(prefix = "sms_report"),
        farmId = farmId,
        notificationId = notificationId,
        toPhoneNumber = to,
        provider = provider,
        statusCode = 0,
        isSuccess = false,
        errorMessage = error ?: "Unknown error",
        sentAtEpochMillis = TimeProvider.nowEpochMillis(),
    )

    private fun String.encodeUrl(): String =
        this.replace(" ", "+")
            .replace("&", "%26")
            .replace("=", "%3D")
            .replace("?", "%3F")

    private fun String.encodeBase64(): String {
        val bytes = this.encodeToByteArray()
        val table = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
        val sb = StringBuilder()
        var i = 0
        while (i < bytes.size) {
            val b0 = bytes[i].toInt() and 0xFF
            val b1 = if (i + 1 < bytes.size) bytes[i + 1].toInt() and 0xFF else 0
            val b2 = if (i + 2 < bytes.size) bytes[i + 2].toInt() and 0xFF else 0
            sb.append(table[(b0 shr 2)])
            sb.append(table[((b0 and 3) shl 4) or (b1 shr 4)])
            sb.append(if (i + 1 < bytes.size) table[((b1 and 15) shl 2) or (b2 shr 6)] else '=')
            sb.append(if (i + 2 < bytes.size) table[b2 and 63] else '=')
            i += 3
        }
        return sb.toString()
    }
}
