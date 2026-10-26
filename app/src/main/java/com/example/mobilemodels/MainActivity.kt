package com.example.mobilemodels

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mobilemodels.presentation.MobileModelsAppScreen
import com.example.mobilemodels.presentation.theme.MobileModelsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MobileModelsTheme { MobileModelsAppScreen() } }
    }
}
