package com.storyteller_f.bi.components

import com.storyteller_f.bi.player.PlayerSession
import androidx.compose.foundation.layout.*
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moriatsushi.insetsx.safeAreaPadding
import com.storyteller_f.bi.gs.UserInfoState
import com.storyteller_f.bi.ui.UserAware
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition

@Composable
fun HomePage(
    openVideoOuter: (PlayerSession) -> Unit,
    startLogin: () -> Unit
) {

    var adaptiveVideo by remember {
        mutableStateOf<PlayerSession?>(null)
    }
//    val calculateWindowSizeClass = calculateWindowSizeClass()
//
//    val wideMode =
//        calculateWindowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact
    val wideMode = false
    val openVideo: (PlayerSession) -> Unit = {
        if (wideMode) {
            adaptiveVideo = it
        } else {
            openVideoOuter(it)
        }
    }

    Surface {
        Row(
            modifier = Modifier.safeAreaPadding()
        ) {
            if (wideMode)
                RailBar("") {

                }

            HomeContentPage(
                wideMode,
                startLogin,
                openVideo,
            )
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

@Composable
private fun RailBar(
    currentRoute: String,
    selectRoute: (HomeNavigationConfig) -> Unit,
) {
    NavigationRail {
        HomeNavigationConfig.bottomNavigationItems.forEach {
            NavigationRailItem(
                selected = currentRoute == it.route,
                onClick = { selectRoute(it) },
                icon = { NavItemIcon(screen = it) })
        }
    }
}

@Composable
private fun RowScope.HomeContentPage(
    wideMode: Boolean,
    startLogin: () -> Unit,
    openVideo: (PlayerSession) -> Unit,
) {
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
    Box(modifier = Modifier.Companion.weight(1f)) {
        SearchPage(
            modifier = Modifier.align(Alignment.TopCenter),
            userInfo = user,
            dockMode = wideMode,
            login = startLogin,
        )
        Column(modifier = Modifier.padding(top = 72.dp)) {
            // Define a navigator, which is a replacement for Jetpack Navigation's NavController
            NavHost(
                navigator = navigator,
                navTransition = NavTransition(),
                initialRoute = "/history",
            ) {
                scene(
                    route = "/history",
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
                    route = "/favorite",
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
            if (!wideMode)
                HomeBottomNavigationBar(currentRoute, selectRoute)
        }

    }
}