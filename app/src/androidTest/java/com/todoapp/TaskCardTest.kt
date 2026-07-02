package com.todoapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.todoapp.domain.model.Priority
import com.todoapp.domain.model.Task
import com.todoapp.ui.components.TaskCard
import com.todoapp.ui.theme.TodoAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant

@RunWith(AndroidJUnit4::class)
class TaskCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun taskCard_displaysAllInfo() {
        val task = Task(
            id = 1,
            title = "Test Task Title",
            description = "Test Task Description",
            priority = Priority.HIGH,
            dueDate = Instant.now(),
            isCompleted = false
        )

        composeTestRule.setContent {
            TodoAppTheme {
                TaskCard(
                    task = task,
                    onTaskClick = {},
                    onToggleComplete = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Test Task Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("High").assertIsDisplayed()
    }

    @Test
    fun taskCard_clickCallsCallback() {
        val task = Task(
            id = 2,
            title = "Clickable Task",
            priority = Priority.MEDIUM,
            isCompleted = false
        )
        var clickedId: Long? = null

        composeTestRule.setContent {
            TodoAppTheme {
                TaskCard(
                    task = task,
                    onTaskClick = { id -> clickedId = id },
                    onToggleComplete = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Clickable Task").performClick()

        assert(clickedId == task.id)
    }
}
