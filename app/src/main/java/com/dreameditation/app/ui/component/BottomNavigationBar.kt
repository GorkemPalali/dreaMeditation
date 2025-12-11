package com.dreameditation.app.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dreameditation.app.R
import com.dreameditation.app.ui.navigation.Screen

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == Screen.Home.route,
            onClick = { navController.navigate(Screen.Home.route) },
            icon = { 
                Icon(
                    painter = painterResource(id = R.drawable.ic_home), 
                    contentDescription = stringResource(id = R.string.home_nav)
                ) 
            },
            label = { stringResource(id = R.string.home_nav) }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.MeditationLibraryTab.route,
            onClick = { navController.navigate(Screen.MeditationLibraryTab.route) },
            icon = { 
                Icon(
                    painter = painterResource(id = R.drawable.ic_meditation), 
                    contentDescription = stringResource(id = R.string.meditation_nav)
                ) 
            },
            label = { stringResource(id = R.string.meditation_nav) }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.SleepLibraryTab.route,
            onClick = { navController.navigate(Screen.SleepLibraryTab.route) },
            icon = { 
                Icon(
                    painter = painterResource(id = R.drawable.ic_sleep), 
                    contentDescription = stringResource(id = R.string.sleep_nav)
                ) 
            },
            label = { stringResource(id = R.string.sleep_nav) }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Profile.route,
            onClick = { navController.navigate(Screen.Profile.route) },
            icon = { 
                Icon(
                    painter = painterResource(id = R.drawable.ic_profile), 
                    contentDescription = stringResource(id = R.string.profile_nav)
                ) 
            },
            label = { stringResource(id = R.string.profile_nav) }
        )
    }
}

