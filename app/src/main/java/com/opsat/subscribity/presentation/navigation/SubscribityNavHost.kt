package com.opsat.subscribity.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.opsat.subscribity.presentation.addsubscription.AddSubscriptionRoute
import com.opsat.subscribity.presentation.subscriptionlist.SubscriptionListRoute

const val SUBSCRIPTION_ID_ARG = "subscriptionId"

private object Routes {
    const val SUBSCRIPTION_LIST = "subscriptionList"
    const val SUBSCRIPTION_FORM_BASE = "subscriptionForm"
    const val SUBSCRIPTION_FORM_ROUTE = "$SUBSCRIPTION_FORM_BASE?$SUBSCRIPTION_ID_ARG={$SUBSCRIPTION_ID_ARG}"
}

@Composable
fun SubscribityNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.SUBSCRIPTION_LIST) {
        composable(
            route = Routes.SUBSCRIPTION_LIST,
            exitTransition = { fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { -it / 4 } },
            popEnterTransition = { fadeIn(tween(200)) + slideInHorizontally(tween(200)) { -it / 4 } },
        ) {
            SubscriptionListRoute(
                onAddClick = { navController.navigate(Routes.SUBSCRIPTION_FORM_BASE) },
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
    }
}
