package com.akram.babsharhisab.ui.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akram.babsharhisab.BuildConfig
import com.akram.babsharhisab.core.update.GithubAutoUpdater
import com.akram.babsharhisab.core.update.GithubReleaseInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppUpdateUiState(
    val currentVersionName: String = BuildConfig.VERSION_NAME,
    val isChecking: Boolean = false,
    val isDownloading: Boolean = false,
    val downloadProgressPercent: Int = 0,
    val availableRelease: GithubReleaseInfo? = null,
    val statusMessageBn: String? = null
)

@HiltViewModel
class AppUpdateViewModel @Inject constructor(
    private val githubAutoUpdater: GithubAutoUpdater
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppUpdateUiState())
    val uiState: StateFlow<AppUpdateUiState> = _uiState.asStateFlow()

    init {
        checkForUpdatesSilently()
    }

    fun checkForUpdatesSilently() {
        viewModelScope.launch {
            val result = githubAutoUpdater.checkForUpdate()
            val release = result.getOrNull()
            if (release != null) {
                _uiState.update { it.copy(availableRelease = release) }
            }
        }
    }

    fun checkForUpdatesManually() {
        if (_uiState.value.isChecking || _uiState.value.isDownloading) return
        _uiState.update {
            it.copy(
                isChecking = true,
                statusMessageBn = "GitHub (akram1871) থেকে নতুন ভার্সন চেক করা হচ্ছে..."
            )
        }
        viewModelScope.launch {
            val result = githubAutoUpdater.checkForUpdate()
            result.fold(
                onSuccess = { release ->
                    if (release != null) {
                        _uiState.update {
                            it.copy(
                                isChecking = false,
                                availableRelease = release,
                                statusMessageBn = "নতুন ভার্সন পাওয়া গেছে: ${release.versionTag} (${release.repoFullName})"
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isChecking = false,
                                statusMessageBn = "আপনার অ্যাপটি সর্বশেষ ভার্সনে (${BuildConfig.VERSION_NAME}) আছে!"
                            )
                        }
                    }
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            isChecking = false,
                            statusMessageBn = "আপডেট চেক করতে ইন্টারনেট সংযোগ প্রয়োজন (${err.localizedMessage ?: "ত্রুটি"})"
                        )
                    }
                }
            )
        }
    }

    fun startDownloadAndInstall() {
        val release = _uiState.value.availableRelease ?: return
        if (_uiState.value.isDownloading) return

        _uiState.update {
            it.copy(
                isDownloading = true,
                downloadProgressPercent = 0,
                statusMessageBn = "নতুন ভার্সন (${release.versionTag}) ডাউনলোড হচ্ছে..."
            )
        }

        viewModelScope.launch {
            val res = githubAutoUpdater.downloadAndInstallApk(
                apkDownloadUrl = release.apkDownloadUrl,
                onProgressPercent = { pct ->
                    _uiState.update { it.copy(downloadProgressPercent = pct) }
                }
            )
            res.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isDownloading = false,
                            downloadProgressPercent = 100,
                            statusMessageBn = "ডাউনলোড সম্পন্ন! ইনস্টলার স্ক্রিনে 'Update / Install' চাপুন।"
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            isDownloading = false,
                            statusMessageBn = "ডাউনলোড ব্যর্থ: ${err.localizedMessage ?: "ইন্টারনেট চেক করুন"}"
                        )
                    }
                }
            )
        }
    }

    fun dismissUpdateDialog() {
        _uiState.update { it.copy(availableRelease = null) }
    }
}
