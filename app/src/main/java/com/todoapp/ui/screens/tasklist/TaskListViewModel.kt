package com.todoapp.ui.screens.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.domain.model.Priority
import com.todoapp.domain.model.SortOption
import com.todoapp.domain.model.Task
import com.todoapp.domain.model.TaskFilter
import com.todoapp.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val reminderManager: com.todoapp.util.ReminderManager
) : ViewModel() {

    private val _filter = MutableStateFlow(TaskFilter.ALL)
    val filter: StateFlow<TaskFilter> = _filter.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.DATE)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _uiState = MutableStateFlow<TaskListUiState>(TaskListUiState.Loading)
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            try {
                combine(
                    _filter,
                    _sortOption,
                    taskRepository.observeAllTasks(),
                    taskRepository.observeActiveTasks(),
                    taskRepository.observeCompletedTasks()
                ) { filter, sort, all, active, completed ->
                    val tasks = when (filter) {
                        TaskFilter.ALL -> all
                        TaskFilter.ACTIVE -> active
                        TaskFilter.COMPLETED -> completed
                    }
                    val sortedTasks = when (sort) {
                        SortOption.DATE -> tasks.sortedBy { it.dueDate }
                        SortOption.PRIORITY -> tasks.sortedWith(
                            compareByDescending<Task> { it.priority.ordinal }
                                .thenBy { it.dueDate }
                        )
                    }
                    TaskListUiState.Success(
                        tasks = sortedTasks,
                        filter = filter,
                        sortOption = sort
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            } catch (e: Exception) {
                _uiState.value = TaskListUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun setFilter(filter: TaskFilter) {
        _filter.value = filter
    }

    fun setSortOption(sortOption: SortOption) {
        _sortOption.value = sortOption
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            reminderManager.cancelReminder(task.id)
            taskRepository.deleteTask(task)
        }
    }
}
