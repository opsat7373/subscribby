package com.opsat.subscribity.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.opsat.subscribity.presentation.addsubscription.AddSubscriptionRoute
import com.opsat.subscribity.presentation.subscriptionlist.SubscriptionListRoute

private object Routes {
    const val SUBSCRIPTION_LIST = "subscriptionList"
    const val ADD_SUBSCRIPTION = "addSubscription"
}

@Composable
fun SubscribityNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.SUBSCRIPTION_LIST) {
        composable(Routes.SUBSCRIPTION_LIST) {
            SubscriptionListRoute(onAddClick = { navController.navigate(Routes.ADD_SUBSCRIPTION) })
        }
        composable(Routes.ADD_SUBSCRIPTION) {
            AddSubscriptionRoute(onNavigateBack = { navController.popBackStack() })
        }
    }
}
