package com.akram.babsharhisab.data.remote

import com.akram.babsharhisab.data.remote.dto.SheetsBackupPayloadDto
import com.akram.babsharhisab.data.remote.dto.SheetsBackupResponseDto
import com.akram.babsharhisab.data.remote.dto.SheetsPullRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles HTTPS POST delivery to a Google Apps Script Web App (`doPost(e)`).
 * Specifically handles Google Apps Script's HTTP 302 redirect behavior:
 * When Apps Script finishes `doPost(e)`, it responds with 302 Found pointing to
 * `script.googleusercontent.com/macros/echo?...` where a GET retrieves the JSON output.
 */
@Singleton
class SheetsWebhookClient @Inject constructor() {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(25, TimeUnit.SECONDS)
        .writeTimeout(25, TimeUnit.SECONDS)
        .readTimeout(35, TimeUnit.SECONDS)
        .followRedirects(false)
        .followSslRedirects(false)
        .build()

    suspend fun postBackupPayload(
        webAppUrl: String,
        payload: SheetsBackupPayloadDto
    ): Result<SheetsBackupResponseDto> = withContext(Dispatchers.IO) {
        runCatching {
            val trimmedUrl = webAppUrl.trim()
            require(trimmedUrl.startsWith("https://")) {
                "সঠিক গুগল শিট ওয়েব অ্যাপ লিংক (https://...) প্রদান করুন"
            }

            val bodyJson = json.encodeToString(payload)
            val requestBody = bodyJson.toRequestBody("application/json; charset=utf-8".toMediaType())

            val initialRequest = Request.Builder()
                .url(trimmedUrl)
                .post(requestBody)
                .header("Accept", "application/json")
                .build()

            httpClient.newCall(initialRequest).execute().use { response ->
                when (response.code) {
                    in 200..299 -> {
                        val respText = response.body?.string().orEmpty()
                        parseResponseOrDefault(respText)
                    }
                    301, 302, 303, 307, 308 -> {
                        val redirectLocation = response.header("Location")
                            ?: throw IllegalStateException("গুগল শিট সার্ভার থেকে রিডাইরেক্ট লিংক পাওয়া যায়নি")
                        val getRequest = Request.Builder()
                            .url(redirectLocation)
                            .get()
                            .header("Accept", "application/json")
                            .build()
                        httpClient.newCall(getRequest).execute().use { redirectResp ->
                            if (!redirectResp.isSuccessful) {
                                throw IllegalStateException("গুগল শিট রেসপন্স ত্রুটি: HTTP ${redirectResp.code}")
                            }
                            val respText = redirectResp.body?.string().orEmpty()
                            parseResponseOrDefault(respText)
                        }
                    }
                    else -> {
                        throw IllegalStateException("গুগল শিট ব্যাকআপ ব্যর্থ হয়েছে (HTTP ${response.code})")
                    }
                }
            }
        }
    }

    /**
     * Pulls all backed-up records (Transactions, Shopkeepers, Mahajans, Assets) from the Google Sheet
     * when setting up a new phone.
     */
    suspend fun fetchRestorePayload(
        webAppUrl: String,
        secretToken: String
    ): Result<SheetsBackupResponseDto> = withContext(Dispatchers.IO) {
        runCatching {
            val trimmedUrl = webAppUrl.trim()
            require(trimmedUrl.startsWith("https://")) {
                "সঠিক গুগল শিট ওয়েব অ্যাপ লিংক (https://...) প্রদান করুন"
            }

            val pullReq = SheetsPullRequestDto(action = "PULL_ALL", secretToken = secretToken)
            val bodyJson = json.encodeToString(pullReq)
            val requestBody = bodyJson.toRequestBody("application/json; charset=utf-8".toMediaType())

            val initialRequest = Request.Builder()
                .url(trimmedUrl)
                .post(requestBody)
                .header("Accept", "application/json")
                .build()

            httpClient.newCall(initialRequest).execute().use { response ->
                when (response.code) {
                    in 200..299 -> {
                        val respText = response.body?.string().orEmpty()
                        parseResponseOrDefault(respText)
                    }
                    301, 302, 303, 307, 308 -> {
                        val redirectLocation = response.header("Location")
                            ?: throw IllegalStateException("গুগল শিট সার্ভার থেকে রিডাইরেক্ট লিংক পাওয়া যায়নি")
                        val getRequest = Request.Builder()
                            .url(redirectLocation)
                            .get()
                            .header("Accept", "application/json")
                            .build()
                        httpClient.newCall(getRequest).execute().use { redirectResp ->
                            if (!redirectResp.isSuccessful) {
                                throw IllegalStateException("গুগল শিট রেসপন্স ত্রুটি: HTTP ${redirectResp.code}")
                            }
                            val respText = redirectResp.body?.string().orEmpty()
                            parseResponseOrDefault(respText)
                        }
                    }
                    else -> {
                        throw IllegalStateException("গুগল শিট থেকে ডাটা ডাউনলোড ব্যর্থ হয়েছে (HTTP ${response.code})")
                    }
                }
            }
        }
    }

    private fun parseResponseOrDefault(rawBody: String): SheetsBackupResponseDto {
        if (rawBody.isBlank()) {
            return SheetsBackupResponseDto(status = "SUCCESS", message = "ব্যাকআপ সম্পন্ন হয়েছে")
        }
        return try {
            val parsed = json.decodeFromString<SheetsBackupResponseDto>(rawBody)
            if (parsed.status.equals("ERROR", ignoreCase = true)) {
                throw IllegalStateException(parsed.message.ifBlank { "গুগল শিট স্ক্রিপ্টে ত্রুটি ঘটেছে" })
            }
            parsed
        } catch (e: IllegalStateException) {
            throw e
        } catch (_: Exception) {
            SheetsBackupResponseDto(status = "SUCCESS", message = "ব্যাকআপ সম্পন্ন হয়েছে")
        }
    }
}
