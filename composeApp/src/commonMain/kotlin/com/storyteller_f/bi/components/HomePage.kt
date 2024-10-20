package com.storyteller_f.bi.components

import com.storyteller_f.bi.player.PlayerSession
import androidx.compose.foundation.layout.*
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.storyteller_f.bi.gs.UserInfoState
import com.storyteller_f.bi.ui.UserAware
import moe.tlaster.precompose.navigation.*
import moe.tlaster.precompose.navigation.transition.NavTransition

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun HomePage(
    openVideoOuter: (PlayerSession) -> Unit,
    startLogin: () -> Unit
) {

    var adaptiveVideo by remember {
        mutableStateOf<PlayerSession?>(null)
    }
    val calculateWindowSizeClass = calculateWindowSizeClass()
    val wideMode =
        calculateWindowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact
    val openVideo: (PlayerSession) -> Unit = {
        if (wideMode) {
            adaptiveVideo = it
        } else {
            openVideoOuter(it)
        }
    }
    val user by UserInfoState.state.collectAsState()
    val navigator = rememberNavigator()
    val current by navigator.currentEntry.collectAsState(null)
    val currentRoute by remember {
        derivedStateOf {
            HomeNavigationConfig.bottomNavigationItems.firstOrNull {
                it.route == current?.route?.route
            }
        }
    }
    val selectRoute = { config: HomeNavigationConfig ->
        navigator.navigate(config.route)
    }
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            HomeNavigationConfig.bottomNavigationItems.forEach {
                item(currentRoute?.route == it.route, onClick = {
                    selectRoute(it)
                }, icon = { NavItemIcon(screen = it) }, label = {
                    Text(it.title)
                })
            }
        }, layoutType = if (wideMode) {
            NavigationSuiteType.NavigationRail
        } else {
            NavigationSuiteType.NavigationBar
        }
    ) {
        Row(modifier = if (wideMode) Modifier.statusBarsPadding() else Modifier) {
            Box(modifier = Modifier.weight(1f)) {
                SearchPage(
                    modifier = Modifier.align(Alignment.TopCenter),
                    userInfo = user,
                    dockMode = wideMode,
                    login = startLogin,
                )
                // Define a navigator, which is a replacement for Jetpack Navigation's NavController
                NavHost(
                    navigator = navigator,
                    navTransition = NavTransition(),
                    modifier = Modifier.padding(top = 72.dp),
                    initialRoute = HomeNavigationConfig.History.route,
                ) {
                    homeContent(openVideo, navigator)
                }

            }
            adaptiveVideo?.let {
                Box(modifier = Modifier.weight(1f)) {
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
}

private fun RouteBuilder.homeContent(
    openVideo: (PlayerSession) -> Unit,
    navigator: Navigator
) {
    scene(
        route = "/histories",
        navTransition = NavTransition(),
    ) {
        UserAware {
            HistoryPage(openVideo)
        }
    }
    scene(
        route = "/moments",
        navTransition = NavTransition()
    ) {
        UserAware {
            MomentsPage(openVideo)
        }
    }

    scene(
        route = "/favorites",
        navTransition = NavTransition()
    ) {
        UserAware {
            FavoritePage {
                navigator.navigate("/favorite/${it.id}")
            }
        }
    }

    scene(
        route = "/favorite/{id}",
        navTransition = NavTransition()
    ) {
        val id = it.path<String>("id")!!
        UserAware {
            FavoriteDetailPage(id, openVideo)
        }
    }

    scene(
        route = "/playlist",
        navTransition = NavTransition()
    ) {
        UserAware {
            PlaylistPage(openVideo)
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
                icon = { NavItemIcon(screen = it) })
        }
    }
}

