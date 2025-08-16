package com.storyteller_f.bi.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.storyteller_f.bi.AppNav
import com.storyteller_f.bi.LocalAppNav
import com.storyteller_f.bi.entity.UserInfo
import com.storyteller_f.bi.gs.UserInfoState
import com.storyteller_f.bi.player.PlayerSession
import com.storyteller_f.bi.ui.UserHost

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun HomePage() {
    val appNav = LocalAppNav.current
    var adaptiveVideo by remember {
        mutableStateOf<PlayerSession?>(null)
    }
    val windowSizeClass = calculateWindowSizeClass()
    val wideMode =
        windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact
    val user by UserInfoState.state.collectAsState()
    val navigator = rememberNavController()
    val current by navigator.currentBackStackEntryFlow.collectAsState(null)
    val currentRoute by remember {
        derivedStateOf {
            HomeNavigationConfig.bottomNavigationItems.firstOrNull {
                it.route == current?.destination?.route
            }
        }
    }
    val selectRoute = { config: HomeNavigationConfig ->
        navigator.navigate(config.route)
    }
    Scaffold {
        Surface(Modifier.padding(top = it.calculateTopPadding())) {
            CompositionLocalProvider(
                LocalAppNav provides object : AppNav {
                    override fun gotoLogin() = appNav.gotoLogin()

                    override fun gotoHome() = appNav.gotoHome()

                    override fun gotoVideo(videoSession: PlayerSession.VideoSession) {
                        if (wideMode) {
                            adaptiveVideo = videoSession
                        } else {
                            appNav.gotoVideo(videoSession)
                        }
                    }

                    override fun gotoBangumi(bangumiSession: PlayerSession.BangumiSession) {
                        TODO("Not yet implemented")
                    }

                    override fun gotoFavorite(id: Int) = appNav.gotoFavorite(id)
                }
            ) {
                if (wideMode) {
                    Row(modifier = Modifier.displayCutoutPadding()) {
                        RailBar(currentRoute, selectRoute)
                        ContentInternal(user, true, Modifier.weight(1f), navigator)
                        AdaptiveVideo(adaptiveVideo) {
                            adaptiveVideo = null
                        }
                    }
                } else {
                    Column(modifier = Modifier) {
                        ContentInternal(user, false, Modifier.weight(1f), navigator)
                        HomeBottomNavigationBar(currentRoute, selectRoute)
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.AdaptiveVideo(adaptiveVideo: PlayerSession?, closeAdaptive: () -> Unit) {
    adaptiveVideo?.let {
        Column(modifier = Modifier.Companion.weight(1f)) {
            IconButton({
                closeAdaptive()
            }) {
                Icon(Icons.Default.Close, "hide adaptive video")
            }
            Box(modifier = Modifier.Companion.weight(1f)) {
                if (it is PlayerSession.VideoSession) {
                    VideoPage(
                        playerSession = it
                    )
                } else if (it is PlayerSession.BangumiSession) {
                    BangumiPage(it)
                }
            }
        }
    }
}

@Composable
private fun ContentInternal(
    user: UserInfo?,
    wideMode: Boolean,
    modifier: Modifier,
    navigator: NavHostController,
) {
    Column(modifier) {
        CustomSearchBar(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            userInfo = user,
            dockMode = wideMode,
        )
        // Define a navigator, which is a replacement for Jetpack Navigation's NavController
        NavHost(
            navController = navigator,
            modifier = Modifier,
            startDestination = HomeNavigationConfig.History.route,
        ) {
            homeContent()
        }
    }
}

private fun NavGraphBuilder.homeContent() {
    composable(
        route = "/histories",
    ) {
        UserHost {
            HistoryPage()
        }
    }
    composable(
        route = "/moments",
    ) {
        UserHost {
            MomentsPage()
        }
    }

    composable(
        route = "/favorites",
    ) {
        UserHost {
            FavoritePage()
        }
    }

    composable(
        route = "/playlist",
    ) {
        UserHost {
            PlaylistPage()
        }
    }
}

@Composable
private fun RailBar(
    currentRoute: HomeNavigationConfig? = HomeNavigationConfig.Favorite,
    selectRoute: (HomeNavigationConfig) -> Unit,
) {
    NavigationRail {
        HomeNavigationConfig.bottomNavigationItems.forEach {
            NavigationRailItem(
                selected = currentRoute?.route == it.route,
                onClick = { selectRoute(it) },
                icon = { NavItemIcon(screen = it) }
            )
        }
    }
}

@Composable
fun HomeBottomNavigationBar(
    currentRoute: HomeNavigationConfig? = HomeNavigationConfig.Favorite,
    selectRoute: (HomeNavigationConfig) -> Unit = {}
) {
    NavigationBar {
        HomeNavigationConfig.bottomNavigationItems.forEach { screen ->
            NavigationBarItem(selected = currentRoute?.route == screen.route, onClick = {
                selectRoute(screen)
            }, {
                NavItemIcon(screen)
            }, label = {
                Text(text = screen.title)
            })
        }
    }
}
