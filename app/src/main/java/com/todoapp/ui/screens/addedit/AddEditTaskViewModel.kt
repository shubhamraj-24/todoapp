package com.todoapp.ui.screens.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.domain.model.Priority
import com.todoapp.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository,
    private val reminderManager: com.todoapp.util.ReminderManager
) : ViewModel() {

    private val taskId: Long? = savedStateHandle.get<Long>("taskId")?.takeIf { it != -1L }

    private val _uiState = MutableStateFlow(AddEditTaskUiState())
    val uiState: StateFlow<AddEditTaskUiState> = _uiState.asStateFlow()

    init {
        taskId?.let { loadTask(it) }
    }

    private fun loadTask(taskId: Long) {
        viewModelScope.launch {
            val task = taskRepository.getTaskById(taskId)
            task?.let {
                _uiState.value = AddEditTaskUiState(
                    id = it.id,
                    title = it.title,
                    description = it.description ?: "",
                    dueDate = it.dueDate,
                    priority = it.priority
                )
            }
        }
    }

    fun onTitleChange(title: String) {
        _uiState.value = _uiState.value.copy(title = title, isTitleError = false)
    }

    fun onDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun onDueDateChange(dueDate: Instant?) {
        _uiState.value = _uiState.value.copy(dueDate = dueDate)
    }

    fun onPriorityChange(priority: Priority) {
        _uiState.value = _uiState.value.copy(priority = priority)
    }

    fun saveTask(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.value = state.copy(isTitleError = true, errorMessage = null)
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)
            try {
                var operationSucceeded = false
                if (state.id != null) {
                    // Issue1: Check if existingTask exists
                    val existingTask = taskRepository.getTaskById(state.id)
                    if (existingTask != null) {
                        val updatedTask = existingTask.copy(
                            title = state.title,
                            description = state.description.ifBlank { null },
                            dueDate = state.dueDate,
                            priority = state.priority
                        )
                        taskRepository.updateTask(updatedTask)
                        reminderManager.cancelReminder(existingTask.id)
                        if (state.dueDate != null) {
                            reminderManager.scheduleReminder(updatedTask)
                        }
                        operationSucceeded = true
                    } else {
                        _uiState.value = state.copy(isLoading = false, errorMessage = "Task not found")
                    }
                } else {
                    val newTaskId = taskRepository.insertTask(
                        com.todoapp.domain.model.Task(
                            title = state.title,
                            description = state.description.ifBlank { null },
                            dueDate = state.dueDate,
                            priority = state.priority
                        )
                    )
                    // Issue2: Use directly constructed task with new ID instead of re-querying
                    if (state.dueDate != null) {
                        val newTask = com.todoapp.domain.model.Task(
                            id = newTaskId,
                            title = state.title,
                            description = state.description.ifBlank { null },
                            dueDate = state.dueDate,
                            priority = state.priority,
                            isCompleted = false
                        )
                        reminderManager.scheduleReminder(newTask)
                    }
                    operationSucceeded = true
                }
                if (operationSucceeded) {
                    onSuccess()
                }
            } catch (e: Exception) {
                _uiState.value = state.copy(isLoading = false, errorMessage = e.localizedMessage)
            }
        }
    }
}
