package com.storyteller_f.bi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.storyteller_f.bi.components.*
import com.storyteller_f.bi.player.PlayerSession
import com.storyteller_f.bi.ui.theme.BiTheme
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.serialization.Serializable

object StaticObject {
    init {
        Napier.base(DebugAntilog())
    }
}

interface AppNav {
    fun gotoLogin()

    fun gotoHome()

    fun gotoVideo(videoSession: PlayerSession.VideoSession)

    fun gotoBangumi(bangumiSession: PlayerSession.BangumiSession)

    fun gotoFavorite(id: Int)

    companion object {
        val EMPTY = object : AppNav {
            override fun gotoLogin() {
                TODO("Not yet implemented")
            }

            override fun gotoHome() {
                TODO("Not yet implemented")
            }

            override fun gotoVideo(videoSession: PlayerSession.VideoSession) {
                TODO("Not yet implemented")
            }

            override fun gotoBangumi(bangumiSession: PlayerSession.BangumiSession) {
                TODO("Not yet implemented")
            }

            override fun gotoFavorite(id: Int) {
                TODO("Not yet implemented")
            }
        }
    }
}

val LocalAppNav = compositionLocalOf {
    AppNav.EMPTY
}

@Serializable
object HomeScreen

@Serializable
class VideoScreen(val id: String, val business: String, val progress: Long)

@Serializable
class BangumiScreen(val id: String, val seasonId: String, val business: String, val progress: Long)

@Serializable
class FavoriteScreen(val id: Int)

@Serializable
object LoginScreen

@Composable
fun App() {
    StaticObject
    BiTheme {
        val navigator = rememberNavController()
        CompositionLocalProvider(
            LocalAppNav provides buildAppNavFromNavigator(navigator)
        ) {
            NavHost(
                navController = navigator,
                startDestination = HomeScreen,
            ) {
                AppContent()
            }
        }
    }
}

private fun buildAppNavFromNavigator(navigator: NavHostController) = object : AppNav {
    override fun gotoLogin() {
        navigator.navigate(LoginScreen)
    }

    override fun gotoHome() {
        if (!navigator.popBackStack(HomeScreen, false)) {
            navigator.navigate(HomeScreen)
        }
    }

    override fun gotoVideo(videoSession: PlayerSession.VideoSession) {
        navigator.navigate(VideoScreen(videoSession.id, videoSession.business, videoSession.progress))
    }

    override fun gotoBangumi(bangumiSession: PlayerSession.BangumiSession) {
        navigator.navigate(
            BangumiScreen(
                bangumiSession.id,
                bangumiSession.seasonId,
                bangumiSession.business,
                bangumiSession.progress
            )
        )
    }

    override fun gotoFavorite(id: Int) {
        navigator.navigate(FavoriteScreen(id))
    }
}

private fun NavGraphBuilder.AppContent() {
    composable<HomeScreen> {
        HomePage()
    }

    composable<VideoScreen> {
        val screen = it.toRoute<VideoScreen>()
        VideoPage(PlayerSession.VideoSession(screen.id, screen.business, screen.progress))
    }

    composable<BangumiScreen> {
        val screen = it.toRoute<BangumiScreen>()
        BangumiPage(
            PlayerSession.BangumiSession(
                screen.id,
                screen.seasonId,
                screen.business,
                screen.progress
            )
        )
    }

    composable<LoginScreen> {
        LoginPage()
    }

    composable<FavoriteScreen> {
        val id = it.toRoute<FavoriteScreen>().id
        FavoriteDetailPage(id) {}
    }
}

fun <T> List<T>.safeSubList(startIndex: Int, endIndex: Int): List<T> {
    if (startIndex >= size) return emptyList()
    if (endIndex <= startIndex) return emptyList()
    return subList(startIndex, endIndex)
}
