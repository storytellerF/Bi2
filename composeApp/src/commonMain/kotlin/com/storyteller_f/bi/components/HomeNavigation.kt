package com.storyteller_f.bi.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

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

@Composable
fun NavItemIcon(screen: HomeNavigationConfig) {
    when {
        screen.icon != null -> {
//            Icon(
//                painter = painterResource(DrawableResource(screen.icon)),
//                contentDescription = screen.route
//            )
        }

        screen.vector != null -> Icon(
            screen.vector,
            contentDescription = screen.route
        )
    }
}
