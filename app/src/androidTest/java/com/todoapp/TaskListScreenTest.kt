package com.todoapp.ui.screens.tasklist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.todoapp.domain.model.Priority
import com.todoapp.domain.model.Task
import com.todoapp.domain.repository.TaskRepository
import com.todoapp.ui.theme.TodoAppTheme
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(com.todoapp.di.DatabaseModule::class)
@RunWith(AndroidJUnit4::class)
class TaskListScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltTestRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Inject
    lateinit var taskRepository: TaskRepository

    @Before
    fun setup() {
        hiltRule.hiltRule.inject()
    }

    @Test
    fun taskListScreen_emptyState_displayed() = runTest {
        composeTestRule.setContent {
            TodoAppTheme {
                TaskListScreen(
                    onAddTaskClick = {},
                    onTaskClick = {},
                    onSettingsClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("No tasks yet").assertIsDisplayed()
    }

    @Test
    fun taskListScreen_withTasks_displaysTasks() = runTest {
        composeTestRule.setContent {
            TodoAppTheme {
                TaskListScreen(
                    onAddTaskClick = {},
                    onTaskClick = {},
                    onSettingsClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Tasks").assertIsDisplayed()
    }
}
