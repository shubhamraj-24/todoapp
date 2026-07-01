package com.todoapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.todoapp.ui.screens.addedit.AddEditTaskScreen
import com.todoapp.ui.screens.taskdetail.TaskDetailScreen
import com.todoapp.ui.screens.tasklist.TaskListScreen
import com.todoapp.ui.screens.settings.SettingsScreen

@Composable
fun TodoAppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = NavDestinations.TaskList.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(NavDestinations.TaskList.route) {
            TaskListScreen(
                onAddTaskClick = {
                    navController.navigate(NavDestinations.AddEditTask.createRoute())
                },
                onTaskClick = { taskId ->
                    navController.navigate(NavDestinations.TaskDetail.createRoute(taskId))
                },
                onSettingsClick = {
                    navController.navigate(NavDestinations.Settings.route)
                }
            )
        }

        composable(
            route = NavDestinations.TaskDetail.route,
            arguments = listOf(
                navArgument("taskId") { type = NavType.LongType }
            )
        ) {
            TaskDetailScreen(
                onBackClick = { navController.popBackStack() },
                onEditClick = { taskId ->
                    navController.navigate(NavDestinations.AddEditTask.createRoute(taskId))
                },
                onDeleteClick = { navController.popBackStack() }
            )
        }

        composable(
            route = NavDestinations.AddEditTask.route,
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.LongType
                    defaultValue = -1L
                    nullable = false
                }
            )
        ) {
            AddEditTaskScreen(
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable(NavDestinations.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
