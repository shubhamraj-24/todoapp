package com.todoapp.ui.screens.taskdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository,
    private val reminderManager: com.todoapp.util.ReminderManager
) : ViewModel() {

    private val taskId: Long = checkNotNull(savedStateHandle["taskId"])

    private val _uiState = MutableStateFlow<TaskDetailUiState>(TaskDetailUiState.Loading)
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    init {
        loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            try {
                val task = taskRepository.getTaskById(taskId)
                if (task != null) {
                    _uiState.value = TaskDetailUiState.Success(task)
                } else {
                    _uiState.value = TaskDetailUiState.NotFound
                }
            } catch (e: Exception) {
                _uiState.value = TaskDetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun toggleTaskCompletion() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is TaskDetailUiState.Success) {
                val updatedTask = currentState.task.copy(isCompleted = !currentState.task.isCompleted)
                taskRepository.updateTask(updatedTask)
                _uiState.value = TaskDetailUiState.Success(updatedTask)
            }
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is TaskDetailUiState.Success) {
                reminderManager.cancelReminder(currentState.task.id)
                taskRepository.deleteTask(currentState.task)
            }
        }
    }
}
