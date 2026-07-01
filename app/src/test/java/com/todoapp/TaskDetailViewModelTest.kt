package com.todoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.todoapp.domain.model.Priority
import com.todoapp.domain.model.Task
import com.todoapp.domain.repository.TaskRepository
import com.todoapp.ui.screens.taskdetail.TaskDetailUiState
import com.todoapp.ui.screens.taskdetail.TaskDetailViewModel
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
import kotlin.test.assertIs

class TaskDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testTaskId = 1L
    private lateinit var taskRepository: TaskRepository
    private lateinit var reminderManager: ReminderManager
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: TaskDetailViewModel

    private val testTask = Task(
        id = testTaskId,
        title = "Test Task",
        description = "Test Description",
        priority = Priority.HIGH,
        dueDate = Instant.now(),
        isCompleted = false
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        taskRepository = mockk()
        reminderManager = mockk(relaxed = true)
        savedStateHandle = SavedStateHandle().apply { set("taskId", testTaskId) }
        coEvery { taskRepository.getTaskById(testTaskId) } returns testTask
        viewModel = TaskDetailViewModel(savedStateHandle, taskRepository, reminderManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel loads task successfully on initialization`() = runTest {
        val uiState = viewModel.uiState.value
        assertIs<TaskDetailUiState.Success>(uiState)
        assertEquals(testTaskId, (uiState as TaskDetailUiState.Success).task.id)
    }

    @Test
    fun `toggleTaskCompletion updates task in repository`() = runTest {
        coEvery { taskRepository.updateTask(any()) } returns Unit

        viewModel.toggleTaskCompletion()

        coVerify(exactly = 1) {
            taskRepository.updateTask(testTask.copy(isCompleted = true))
        }
    }

    @Test
    fun `deleteTask calls repository delete and reminderManager cancel`() = runTest {
        coEvery { taskRepository.deleteTask(any()) } returns Unit

        viewModel.deleteTask()

        coVerify(exactly = 1) {
            reminderManager.cancelReminder(testTaskId)
        }
        coVerify(exactly = 1) {
            taskRepository.deleteTask(testTask)
        }
    }

    @Test
    fun `task not found sets ui state to NotFound`() = runTest {
        val notFoundId = 999L
        savedStateHandle = SavedStateHandle().apply { set("taskId", notFoundId) }
        coEvery { taskRepository.getTaskById(notFoundId) } returns null

        val notFoundViewModel = TaskDetailViewModel(savedStateHandle, taskRepository, reminderManager)

        val uiState = notFoundViewModel.uiState.value
        assertIs<TaskDetailUiState.NotFound>(uiState)
    }
}
