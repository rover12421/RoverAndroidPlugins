package com.rover12421.android.plugins.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rover12421.android.plugins.app.ui.theme.RoverAndroidPluginsTheme
import com.rover12421.android.plugins.namehash.core.HashName

class MainActivity : ComponentActivity() {

    @HashName("onCreate", "TestOncreate", "mService", "mTargetSdkVersion")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RoverAndroidPluginsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }

    @HashName
    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        var info by remember { mutableStateOf("") }

        info += "This Class Annotation Info: \n"
        this::class.java.annotations.forEach {
            info += it.toString() + "\n"
        }

        info += "\n\n"

        info += "GreetingPreview Annotation info: \n"
        val method = this::class.java.methods.first { it.name == "GreetingPreview" }
        method.annotations.forEach {
            info += it.toString() + "\n"
        }

        Text(
            text = info,
            modifier = modifier
        )
    }

    @HashName("Abcdefg")
    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        RoverAndroidPluginsTheme {
            Greeting("Android")
        }
    }
}

