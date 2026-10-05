package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.GearViewModel
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.AmazonGearTheme

class MainActivity : ComponentActivity() {

    private val viewModel: GearViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AmazonGearTheme {
                HomeScreen(viewModel = viewModel)
            }
        }
    }
}
