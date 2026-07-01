package com.todoapp.ui.navigation

sealed class NavDestinations(val route: String) {
    data object TaskList : NavDestinations("task_list")
    data object TaskDetail : NavDestinations("task_detail/{taskId}") {
        fun createRoute(taskId: Long) = "task_detail/$taskId"
    }
    data object AddEditTask : NavDestinations("add_edit_task?taskId={taskId}") {
        fun createRoute(taskId: Long? = null) = if (taskId != null) {
            "add_edit_task?taskId=$taskId"
        } else {
            "add_edit_task"
        }
    }
    data object Settings : NavDestinations("settings")
}
