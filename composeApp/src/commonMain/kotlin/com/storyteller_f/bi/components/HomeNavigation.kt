package com.storyteller_f.bi.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.storyteller.bi2.resources.Res
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
fun NavItemIcon(screen: HomeNavigationConfig) {
    Icon(
        screen.vector,
        contentDescription = screen.route
    )
}
