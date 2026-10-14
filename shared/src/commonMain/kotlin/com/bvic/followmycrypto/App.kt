package com.bvic.followmycrypto

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.bvic.followmycrypto.theme.FollowMyCryptoTheme
import com.bvic.followmycrypto.ui.detail.CryptoDetailScreen
import com.bvic.followmycrypto.ui.list.CryptoListScreen
import kotlinx.serialization.Serializable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

// Chaque écran a une « route » : un objet qui l'identifie et porte ses arguments
@Serializable
object ListRoute

@Serializable
data class DetailRoute(val symbol: String)

@Composable
fun App() {
    val systemDark = isSystemInDarkTheme()
    var darkTheme by rememberSaveable { mutableStateOf(systemDark) }
    val toggleTheme = { darkTheme = !darkTheme }

    FollowMyCryptoTheme(darkTheme = darkTheme) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = ListRoute) {
            composable<ListRoute> {
                CryptoListScreen(
                    onCryptoClick = { symbol -> navController.navigate(DetailRoute(symbol)) },
                    darkTheme = darkTheme,
                    onToggleTheme = toggleTheme,
                )
            }
            composable<DetailRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<DetailRoute>()
                CryptoDetailScreen(
                    symbol = route.symbol,
                    onBack = { navController.popBackStack() },
                    darkTheme = darkTheme,
                    onToggleTheme = toggleTheme,
                )
            }
        }
    }
}
