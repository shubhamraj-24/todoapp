package com.todoapp.ui.screens.tasklist

import com.todoapp.domain.model.SortOption
import com.todoapp.domain.model.Task
import com.todoapp.domain.model.TaskFilter

sealed interface TaskListUiState {
    data object Loading : TaskListUiState
    data class Success(
        val tasks: List<Task>,
        val filter: TaskFilter = TaskFilter.ALL,
        val sortOption: SortOption = SortOption.DATE
    ) : TaskListUiState
    data class Error(val message: String) : TaskListUiState
}
