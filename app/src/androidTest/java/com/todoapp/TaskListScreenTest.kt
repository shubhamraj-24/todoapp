package com.todoapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.todoapp.data.database.AppDatabase
import com.todoapp.domain.model.Priority
import com.todoapp.domain.model.Task
import com.todoapp.domain.repository.TaskRepository
import com.todoapp.ui.screens.tasklist.TaskListScreen
import com.todoapp.ui.theme.TodoAppTheme
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TaskListScreenTest {

    @get:Rule(order = 0)
    val hiltRule = dagger.hilt.android.testing.HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Inject
    lateinit var taskRepository: TaskRepository

    @Inject
    lateinit var database: AppDatabase

    @Before
    fun setup() {
        hiltRule.inject()
        database.clearAllTables()
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

        // Wait for potential loading to finish
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText("No tasks yet").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("No tasks yet").assertIsDisplayed()
    }

    @Test
    fun taskListScreen_withTasks_displaysTasks() = runTest {
        val task = Task(
            title = "Test Task",
            priority = Priority.MEDIUM
        )
        taskRepository.insertTask(task)

        composeTestRule.setContent {
            TodoAppTheme {
                TaskListScreen(
                    onAddTaskClick = {},
                    onTaskClick = {},
                    onSettingsClick = {}
                )
            }
        }

        // Wait for task to be loaded and displayed
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText("Test Task").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Test Task").assertIsDisplayed()
    }
}
