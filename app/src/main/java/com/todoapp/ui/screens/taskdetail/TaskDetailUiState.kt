package com.todoapp.ui.screens.taskdetail

import com.todoapp.domain.model.Task

sealed interface TaskDetailUiState {
    data object Loading : TaskDetailUiState
    data class Success(val task: Task) : TaskDetailUiState
    data object NotFound : TaskDetailUiState
    data class Error(val message: String) : TaskDetailUiState
}
