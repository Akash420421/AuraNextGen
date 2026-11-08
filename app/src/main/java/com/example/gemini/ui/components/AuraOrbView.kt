package com.example.gemini.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.gemini.data.jarvis.JarvisController
import com.example.gemini.data.jarvis.JarvisController.Phase

class AuraOrbBridge(
    private val onOrbClick: () -> Unit
) {
    @JavascriptInterface
    fun onOrbTapped() {
        onOrbClick()
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AuraOrbView(
    modifier: Modifier = Modifier,
    phase: Phase = Phase.IDLE,
    onOrbTapped: () -> Unit = {}
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    // Sync phase to webview
    LaunchedEffect(phase, webViewRef) {
        val wv = webViewRef ?: return@LaunchedEffect
        val mode = when (phase) {
            Phase.LISTENING -> "listening"
            Phase.THINKING -> "thinking"
            Phase.SPEAKING -> "speaking"
            else -> "idle"
        }
        wv.evaluateJavascript("if (window.setAuraMode) { window.setAuraMode('$mode'); }", null)
    }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    setBackgroundColor(Color.TRANSPARENT)
                    setLayerType(WebView.LAYER_TYPE_HARDWARE, null)
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        allowFileAccess = true
                        allowContentAccess = true
                        cacheMode = WebSettings.LOAD_NO_CACHE
                    }
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            val mode = when (phase) {
                                Phase.LISTENING -> "listening"
                                Phase.THINKING -> "thinking"
                                Phase.SPEAKING -> "speaking"
                                else -> "idle"
                            }
                            view?.evaluateJavascript("if (window.setAuraMode) { window.setAuraMode('$mode'); }", null)
                        }
                    }
                    addJavascriptInterface(
                        AuraOrbBridge {
                            post { onOrbTapped() }
                        },
                        "AndroidBridge"
                    )
                    loadUrl("file:///android_asset/orb/orb.html")
                    webViewRef = this
                }
            },
            update = { wv ->
                webViewRef = wv
            }
        )
    }
}
