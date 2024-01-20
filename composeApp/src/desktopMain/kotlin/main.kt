import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() {
    application {
        Window(onCloseRequest = ::exitApplication) {
            com.storyteller_f.bi.App()
        }
    }
}
