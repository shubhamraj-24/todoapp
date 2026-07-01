package com.todoapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.todoapp.domain.model.Priority
import com.todoapp.domain.model.SortOption
import com.todoapp.domain.model.Task
import com.todoapp.domain.model.TaskFilter
import com.todoapp.domain.repository.TaskRepository
import com.todoapp.ui.screens.tasklist.TaskListUiState
import com.todoapp.ui.screens.tasklist.TaskListViewModel
import com.todoapp.util.ReminderManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
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

class TaskListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var taskRepository: TaskRepository
    private lateinit var reminderManager: ReminderManager
    private lateinit var viewModel: TaskListViewModel

    private val testTasks = listOf(
        Task(id = 1, title = "Task 1", priority = Priority.HIGH, isCompleted = false, dueDate = Instant.now()),
        Task(id = 2, title = "Task 2", priority = Priority.MEDIUM, isCompleted = true, dueDate = Instant.now().plusSeconds(3600)),
        Task(id = 3, title = "Task 3", priority = Priority.LOW, isCompleted = false, dueDate = Instant.now().minusSeconds(3600))
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        taskRepository = mockk()
        reminderManager = mockk(relaxed = true)
        every { taskRepository.observeAllTasks() } returns flowOf(testTasks)
        every { taskRepository.observeActiveTasks() } returns flowOf(testTasks.filter { !it.isCompleted })
        every { taskRepository.observeCompletedTasks() } returns flowOf(testTasks.filter { it.isCompleted })
        viewModel = TaskListViewModel(taskRepository, reminderManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel initializes with loading state then success`() = runTest {
        val uiState = viewModel.uiState.value
        assertIs<TaskListUiState.Success>(uiState)
        assertEquals(3, (uiState as TaskListUiState.Success).tasks.size)
    }

    @Test
    fun `setFilter updates ui state with filtered tasks`() = runTest {
        viewModel.setFilter(TaskFilter.ACTIVE)

        val uiState = viewModel.uiState.value
        assertIs<TaskListUiState.Success>(uiState)
        assertEquals(2, uiState.tasks.size)
        assertEquals(TaskFilter.ACTIVE, uiState.filter)
    }

    @Test
    fun `setSortOption updates ui state with sorted tasks`() = runTest {
        viewModel.setSortOption(SortOption.PRIORITY)

        val uiState = viewModel.uiState.value
        assertIs<TaskListUiState.Success>(uiState)
        assertEquals(SortOption.PRIORITY, uiState.sortOption)
        // First task should be HIGH priority
        assertEquals(Priority.HIGH, uiState.tasks.first().priority)
    }

    @Test
    fun `toggleTaskCompletion calls repository update`() = runTest {
        val task = testTasks.first()
        coEvery { taskRepository.updateTask(any()) } returns Unit

        viewModel.toggleTaskCompletion(task)

        coVerify(exactly = 1) {
            taskRepository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    @Test
    fun `deleteTask calls repository delete and reminderManager cancel`() = runTest {
        val task = testTasks.first()
        coEvery { taskRepository.deleteTask(any()) } returns Unit

        viewModel.deleteTask(task)

        coVerify(exactly = 1) {
            reminderManager.cancelReminder(task.id)
        }
        coVerify(exactly = 1) {
            taskRepository.deleteTask(task)
        }
    }
}
