package com.todoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.todoapp.domain.model.Priority
import com.todoapp.domain.model.Task
import com.todoapp.domain.repository.TaskRepository
import com.todoapp.ui.screens.addedit.AddEditTaskUiState
import com.todoapp.ui.screens.addedit.AddEditTaskViewModel
import com.todoapp.util.ReminderManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AddEditTaskViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var taskRepository: TaskRepository
    private lateinit var reminderManager: ReminderManager
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: AddEditTaskViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        taskRepository = mockk(relaxed = true)
        reminderManager = mockk(relaxed = true)
        savedStateHandle = SavedStateHandle()
        viewModel = AddEditTaskViewModel(savedStateHandle, taskRepository, reminderManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onTitleChange updates ui state title`() = runTest {
        val newTitle = "New Task Title"
        viewModel.onTitleChange(newTitle)

        assertEquals(newTitle, viewModel.uiState.value.title)
    }

    @Test
    fun `onDescriptionChange updates ui state description`() = runTest {
        val newDescription = "New Description"
        viewModel.onDescriptionChange(newDescription)

        assertEquals(newDescription, viewModel.uiState.value.description)
    }

    @Test
    fun `onPriorityChange updates ui state priority`() = runTest {
        val newPriority = Priority.HIGH
        viewModel.onPriorityChange(newPriority)

        assertEquals(newPriority, viewModel.uiState.value.priority)
    }

    @Test
    fun `onDueDateChange updates ui state due date`() = runTest {
        val newDueDate = Instant.now().plusSeconds(3600)
        viewModel.onDueDateChange(newDueDate)

        assertEquals(newDueDate, viewModel.uiState.value.dueDate)
    }

    @Test
    fun `saveTask with empty title sets isTitleError to true`() = runTest {
        viewModel.onTitleChange("")

        viewModel.saveTask {}

        assertTrue(viewModel.uiState.value.isTitleError)
    }

    @Test
    fun `saveTask with valid title calls repository insert`() = runTest {
        val title = "Test Task"
        coEvery { taskRepository.insertTask(any()) } returns 1L

        viewModel.onTitleChange(title)
        viewModel.saveTask {}

        coVerify(exactly = 1) {
            taskRepository.insertTask(any())
        }
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `saveTask for existing task calls repository update`() = runTest {
        val existingTaskId = 5L
        val existingTask = Task(
            id = existingTaskId,
            title = "Old Title",
            priority = Priority.MEDIUM
        )
        savedStateHandle["taskId"] = existingTaskId
        coEvery { taskRepository.getTaskById(existingTaskId) } returns existingTask

        val editViewModel = AddEditTaskViewModel(savedStateHandle, taskRepository, reminderManager)
        val newTitle = "Updated Title"
        editViewModel.onTitleChange(newTitle)
        editViewModel.saveTask {}

        coVerify(exactly = 1) {
            taskRepository.updateTask(any())
        }
    }
}
