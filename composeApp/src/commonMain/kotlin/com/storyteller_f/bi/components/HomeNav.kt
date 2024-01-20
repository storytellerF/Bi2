package com.storyteller_f.bi.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector

sealed class HomeNavigationConfig(
    val route: String,
    val title: String,
    val vector: ImageVector? = null,
    val icon: String? = null
) {

    data object History : HomeNavigationConfig("histories", "History", icon = "src/commonMain/commonResources/drawable/baseline_history_24.xml")


    data object Moments : HomeNavigationConfig("moments", "Moments", icon = "src/commonMain/commonResources/drawable/baseline_explore_24.xml")


    data object Playlist : HomeNavigationConfig("playlist", "Playlist", vector = Icons.Filled.PlayArrow)


    data object Favorite : HomeNavigationConfig("favorites", "Favorites", vector = Icons.Filled.Favorite)


    data class FavoriteDetail(val id: String) :
        HomeNavigationConfig("favorite-detail/$id", "Favorite", vector = Icons.Filled.Favorite)

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


sealed class HomePageChild {
    data object History : HomePageChild()
    data object Moments : HomePageChild()
    data object Favorite : HomePageChild()
    data class FavoriteDetail(val id: String) : HomePageChild()
    data object Playlist : HomePageChild()
}
