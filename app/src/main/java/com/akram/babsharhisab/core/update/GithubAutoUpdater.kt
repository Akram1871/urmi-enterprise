package com.akram.babsharhisab.core.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.akram.babsharhisab.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

data class GithubReleaseInfo(
    val versionTag: String,
    val releaseTitle: String,
    val releaseNotes: String,
    val apkDownloadUrl: String,
    val repoFullName: String
)

@Singleton
class GithubAutoUpdater @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()
    companion object {
        const val GITHUB_USERNAME = "akram1871"
        val CANDIDATE_REPOS = listOf(
            "urmi-enterprise",
            "baba_buisness",
            "baba-business"
        )
    }

    /**
     * Checks `https://api.github.com/repos/akram1871/{repo}/releases/latest`
     * across candidate repositories and returns [GithubReleaseInfo] if a newer
     * version than [BuildConfig.VERSION_NAME] is available.
     */
    suspend fun checkForUpdate(): Result<GithubReleaseInfo?> = withContext(Dispatchers.IO) {
        try {
            val currentVersion = BuildConfig.VERSION_NAME
            for (repo in CANDIDATE_REPOS) {
                val apiUrl = "https://api.github.com/repos/$GITHUB_USERNAME/$repo/releases/latest"
                val request = Request.Builder()
                    .url(apiUrl)
                    .header("Accept", "application/vnd.github+json")
                    .header("User-Agent", "UrmiEnterprise-Android/${BuildConfig.VERSION_NAME}")
                    .get()
                    .build()

                okHttpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        return@use
                    }
                    val bodyStr = response.body?.string().orEmpty()
                    if (bodyStr.isBlank()) return@use

                    val json = JSONObject(bodyStr)
                    val tagName = json.optString("tag_name", "").trim()
                    if (tagName.isBlank()) return@use

                    val releaseTitle = json.optString("name", tagName).ifBlank { tagName }
                    val releaseNotes = json.optString("body", "নতুন ফিচার ও পারফরম্যান্স আপডেট যুক্ত করা হয়েছে।")

                    // Find the first .apk asset in the release
                    val assets = json.optJSONArray("assets")
                    var apkUrl = ""
                    if (assets != null) {
                        for (i in 0 until assets.length()) {
                            val asset = assets.getJSONObject(i)
                            val name = asset.optString("name", "")
                            val downloadUrl = asset.optString("browser_download_url", "")
                            if (name.endsWith(".apk", ignoreCase = true) && downloadUrl.isNotBlank()) {
                                apkUrl = downloadUrl
                                break
                            }
                        }
                    }

                    if (apkUrl.isNotBlank() && isRemoteVersionNewer(tagName, currentVersion)) {
                        return@withContext Result.success(
                            GithubReleaseInfo(
                                versionTag = tagName,
                                releaseTitle = releaseTitle,
                                releaseNotes = releaseNotes,
                                apkDownloadUrl = apkUrl,
                                repoFullName = "$GITHUB_USERNAME/$repo"
                            )
                        )
                    } else if (apkUrl.isNotBlank()) {
                        // Found valid release repo, already up to date
                        return@withContext Result.success(null)
                    }
                }
            }
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Downloads the APK from [apkDownloadUrl] into cache/updates/ and triggers
     * the Android system package installer.
     */
    suspend fun downloadAndInstallApk(
        apkDownloadUrl: String,
        onProgressPercent: (Int) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(apkDownloadUrl)
                .header("User-Agent", "UrmiEnterprise-Android/${BuildConfig.VERSION_NAME}")
                .get()
                .build()

            val updateDir = File(context.cacheDir, "updates")
            if (!updateDir.exists()) {
                updateDir.mkdirs()
            }
            val apkFile = File(updateDir, "urmi-enterprise-update.apk")
            if (apkFile.exists()) {
                apkFile.delete()
            }

            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(
                        IllegalStateException("APK ডাউনলোড ব্যর্থ হয়েছে (HTTP ${response.code})")
                    )
                }
                val body = response.body ?: return@withContext Result.failure(
                    IllegalStateException("খালি APK ফাইল পাওয়া গেছে")
                )
                val totalBytes = body.contentLength()
                body.byteStream().use { input ->
                    FileOutputStream(apkFile).use { output ->
                        val buffer = ByteArray(8 * 1024)
                        var downloadedBytes = 0L
                        var read: Int
                        while (input.read(buffer).also { read = it } != -1) {
                            output.write(buffer, 0, read)
                            downloadedBytes += read
                            if (totalBytes > 0L) {
                                val pct = ((downloadedBytes * 100L) / totalBytes).toInt().coerceIn(0, 100)
                                onProgressPercent(pct)
                            }
                        }
                        output.flush()
                    }
                }
            }

            withContext(Dispatchers.Main) {
                promptInstallApk(apkFile)
            }
            Result.success(apkFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun promptInstallApk(apkFile: File) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                val permissionIntent = Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:${context.packageName}")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(permissionIntent)
                return
            }
        }

        val apkUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )
        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(installIntent)
    }

    internal fun isRemoteVersionNewer(remoteTag: String, currentVersion: String): Boolean {
        val remoteParts = remoteTag.trim().removePrefix("v").removePrefix("V")
            .split(".", "-")
            .mapNotNull { it.toIntOrNull() }
        val localParts = currentVersion.trim().removePrefix("v").removePrefix("V")
            .split(".", "-")
            .mapNotNull { it.toIntOrNull() }

        val maxLen = maxOf(remoteParts.size, localParts.size)
        for (i in 0 until maxLen) {
            val r = remoteParts.getOrElse(i) { 0 }
            val l = localParts.getOrElse(i) { 0 }
            if (r > l) return true
            if (r < l) return false
        }
        return false
    }
}
