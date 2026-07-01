package com.todoapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.todoapp.ui.navigation.NavDestinations
import com.todoapp.ui.navigation.TodoAppNavHost
import com.todoapp.ui.theme.TodoAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    navController = rememberNavController()
                    TodoAppNavHost(navController = navController)
                }
            }
        }

        handleDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        intent?.data?.let { uri ->
            handleUri(uri)
        }
    }

    private fun handleUri(uri: Uri) {
        when (uri.scheme) {
            "taskapp" -> {
                when (uri.host) {
                    "task" -> {
                        val taskId = uri.pathSegments.firstOrNull()?.toLongOrNull()
                        taskId?.let {
                            navController.navigate(NavDestinations.TaskDetail.createRoute(it))
                        }
                    }
                }
            }
        }
    }
}
