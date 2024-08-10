@file:Suppress("unused", "FunctionName")

import androidx.compose.ui.window.ComposeUIViewController
import com.storyteller_f.bi.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    return ComposeUIViewController { App() }
}
