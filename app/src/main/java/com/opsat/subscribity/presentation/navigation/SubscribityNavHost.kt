package com.opsat.subscribity.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.opsat.subscribity.presentation.addsubscription.AddSubscriptionRoute
import com.opsat.subscribity.presentation.settings.SettingsRoute
import com.opsat.subscribity.presentation.subscriptionlist.SubscriptionListRoute

const val SUBSCRIPTION_ID_ARG = "subscriptionId"

private object Routes {
    const val SUBSCRIPTION_LIST = "subscriptionList"
    const val SUBSCRIPTION_FORM_BASE = "subscriptionForm"
    const val SUBSCRIPTION_FORM_ROUTE = "$SUBSCRIPTION_FORM_BASE?$SUBSCRIPTION_ID_ARG={$SUBSCRIPTION_ID_ARG}"
    const val SETTINGS = "settings"
}

private fun NavHostController.navigateToTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun SubscribityNavHost(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute == Routes.SUBSCRIPTION_LIST || currentRoute == Routes.SETTINGS) {
                SubscribityBottomBar(
                    isListSelected = currentRoute == Routes.SUBSCRIPTION_LIST,
                    onListClick = { navController.navigateToTopLevel(Routes.SUBSCRIPTION_LIST) },
                    onAddClick = { navController.navigate(Routes.SUBSCRIPTION_FORM_BASE) },
                    onSettingsClick = { navController.navigateToTopLevel(Routes.SETTINGS) },
                )
            }
        },
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SUBSCRIPTION_LIST,
            modifier = Modifier.padding(contentPadding),
        ) {
            composable(
                route = Routes.SUBSCRIPTION_LIST,
                exitTransition = { fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { -it / 4 } },
                popEnterTransition = { fadeIn(tween(200)) + slideInHorizontally(tween(200)) { -it / 4 } },
            ) {
                SubscriptionListRoute(
                    onNavigateToEditSubscription = { id ->
                        navController.navigate("${Routes.SUBSCRIPTION_FORM_BASE}?$SUBSCRIPTION_ID_ARG=$id")
                    },
                )
            }
            composable(
                route = Routes.SUBSCRIPTION_FORM_ROUTE,
                arguments = listOf(navArgument(SUBSCRIPTION_ID_ARG) { type = NavType.LongType; defaultValue = 0L }),
                enterTransition = { fadeIn(tween(250)) + slideInHorizontally(tween(250)) { it } },
                popExitTransition = { fadeOut(tween(250)) + slideOutHorizontally(tween(250)) { it } },
            ) {
                AddSubscriptionRoute(onNavigateBack = { navController.popBackStack() })
            }
            composable(
                route = Routes.SETTINGS,
                enterTransition = { fadeIn(tween(250)) + slideInHorizontally(tween(250)) { it } },
                popExitTransition = { fadeOut(tween(250)) + slideOutHorizontally(tween(250)) { it } },
            ) {
                SettingsRoute()
            }
        }
    }
}
