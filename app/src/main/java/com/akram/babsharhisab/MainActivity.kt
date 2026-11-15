package com.akram.babsharhisab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akram.babsharhisab.core.security.SecureConfigStore
import com.akram.babsharhisab.ui.navigation.AppNavHost
import com.akram.babsharhisab.ui.theme.BabsharHisabTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var secureConfigStore: SecureConfigStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val config by secureConfigStore.configFlow.collectAsStateWithLifecycle()
            BabsharHisabTheme(useClassicFont = config.useClassicFont) {
                AppNavHost()
            }
        }
    }
}
