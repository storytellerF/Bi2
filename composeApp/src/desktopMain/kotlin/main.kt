import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.storyteller_f.bi.gs.LoginInfoState
import com.storyteller_f.bi.gs.UserInfoState

fun main() {
    LoginInfoState.restore()
    UserInfoState.restoreUserInfo()
    application {
        Window(onCloseRequest = ::exitApplication, title = "A") {
            com.storyteller_f.bi.App()
        }
    }
}
