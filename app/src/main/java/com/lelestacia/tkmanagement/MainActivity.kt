package com.lelestacia.tkmanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.lelestacia.tkmanagement.ui.navigation.TkCashNavGraph
import com.lelestacia.tkmanagement.ui.theme.TkCashTheme
import com.lelestacia.tkmanagement.viewmodel.TkCashViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = TkCashViewModelFactory(application)

        setContent {
            TkCashTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TkCashNavGraph(factory = factory)
                }
            }
        }
    }
}
