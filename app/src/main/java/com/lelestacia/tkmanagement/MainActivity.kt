package com.lelestacia.tkmanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.lelestacia.tkmanagement.ui.navigation.TkCashNavGraph
import com.lelestacia.tkmanagement.ui.theme.TkCashTheme
import com.lelestacia.tkmanagement.ui.navigation.Navigator
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.compose.navigation3.getEntryProvider
import org.koin.androidx.scope.activityRetainedScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.scope.Scope

/** Aktivitas utama; menjadi host Compose dan menampilkan nav graph aplikasi. */
@OptIn(KoinExperimentalAPI::class)
class MainActivity : ComponentActivity(), AndroidScopeComponent {
    /** Scope Koin yang bertahan selama aktivitas (activity retained scope). */
    override val scope: Scope by activityRetainedScope()
    private val navigator: Navigator by inject()

    /** Menyusun konten Compose: tema, Surface, dan nav graph dengan navigator dari Koin. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TkCashTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TkCashNavGraph(
                        navigator = navigator,
                        entryProvider = getEntryProvider()
                    )
                }
            }
        }
    }
}
