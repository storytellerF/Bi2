package com.storyteller_f.bi.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

sealed class HomeNavigationConfig(
    val route: String,
    val title: String,
    val vector: ImageVector,
) {

    data object History : HomeNavigationConfig("/histories", "History", vector = Icons.Filled.History)

    data object Moments : HomeNavigationConfig("/moments", "Moments", vector = Icons.Filled.Explore)

    data object Playlist : HomeNavigationConfig("/playlist", "Playlist", vector = Icons.Filled.PlayArrow)

    data object Favorite : HomeNavigationConfig("/favorites", "Favorites", vector = Icons.Filled.Favorite)

    companion object {

        val bottomNavigationItems: List<HomeNavigationConfig>
            get() = listOf(
                History,
                Moments,
                Playlist,
                Favorite,
            )
    }
}

@Composable
fun NavItemIcon(screen: HomeNavigationConfig) {
    Icon(
        screen.vector,
        contentDescription = screen.route
    )
}
