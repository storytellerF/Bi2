package com.storyteller_f.bi

import androidx.compose.runtime.Composable
import com.storyteller_f.bi.components.HomePage
import com.storyteller_f.bi.components.LoginPage
import com.storyteller_f.bi.ui.theme.BiTheme
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition

object StaticObject {
    init {
        Napier.base(DebugAntilog())
    }
}

@Composable
fun App() {
    StaticObject
    BiTheme {
        PreComposeApp {
            val navigator = rememberNavigator()
            NavHost(
                navigator = navigator,
                navTransition = NavTransition(),
                initialRoute = "/home",
            ) {
                scene(
                    route = "/home",
                    navTransition = NavTransition(),
                ) {
                    HomePage({

                    }) {

                    }
                }

                scene(
                    route = "/video",
                    navTransition = NavTransition(),
                ) {

                }

                scene(
                    route = "/bangumi",
                    navTransition = NavTransition(),
                ) {

                }

                scene(
                    route = "/login",
                    navTransition = NavTransition(),
                ) {
                    LoginPage {
                        navigator.goBack()
                    }
                }
            }
        }
    }
}


fun <T> List<T>.safeSub(startIndex: Int, endIndex: Int): List<T> {
    if (startIndex >= size) return emptyList()
    if (endIndex <= startIndex) return emptyList()
    return subList(startIndex, endIndex)
}

fun assert(message: () -> String = { "" }, value: () -> Boolean) {
    if (!value()) {
        throw Exception("assert failed ${message()}")
    }
}
