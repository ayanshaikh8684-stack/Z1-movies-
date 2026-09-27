package com.example.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CinemaBlack
import com.example.ui.theme.CinemaSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainTab

@Composable
fun Z1BottomNavigation(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = CinemaBlack,
        tonalElevation = 8.dp
    ) {
        val tabs = listOf(
            Triple(MainTab.HOME, "Home", Pair(Icons.Filled.Home, Icons.Outlined.Home)),
            Triple(MainTab.MOVIES, "Movies", Pair(Icons.Filled.Movie, Icons.Outlined.Movie)),
            Triple(MainTab.LIVE_TV, "Live TV", Pair(Icons.Filled.Tv, Icons.Outlined.Tv)),
            Triple(MainTab.CATEGORIES, "Categories", Pair(Icons.Filled.Category, Icons.Outlined.Category)),
            Triple(MainTab.DOWNLOADS, "Downloads", Pair(Icons.Filled.Download, Icons.Outlined.Download)),
            Triple(MainTab.PROFILE, "Profile", Pair(Icons.Filled.Person, Icons.Outlined.Person))
        )

        tabs.forEach { (tab, label, icons) ->
            val isSelected = selectedTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) icons.first else icons.second,
                        contentDescription = label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CinemaBlack,
                    selectedTextColor = ElectricBlue,
                    indicatorColor = ElectricBlue,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                )
            )
        }
    }
}
