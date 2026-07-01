package com.todoapp.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.todoapp.domain.model.Priority
import com.todoapp.ui.theme.TodoAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PriorityBadgeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun priorityBadge_lowPriority_displaysCorrectText() {
        composeTestRule.setContent {
            TodoAppTheme {
                PriorityBadge(priority = Priority.LOW)
            }
        }

        composeTestRule.onNodeWithText("Low").assertIsDisplayed()
    }

    @Test
    fun priorityBadge_mediumPriority_displaysCorrectText() {
        composeTestRule.setContent {
            TodoAppTheme {
                PriorityBadge(priority = Priority.MEDIUM)
            }
        }

        composeTestRule.onNodeWithText("Medium").assertIsDisplayed()
    }

    @Test
    fun priorityBadge_highPriority_displaysCorrectText() {
        composeTestRule.setContent {
            TodoAppTheme {
                PriorityBadge(priority = Priority.HIGH)
            }
        }

        composeTestRule.onNodeWithText("High").assertIsDisplayed()
    }
}
