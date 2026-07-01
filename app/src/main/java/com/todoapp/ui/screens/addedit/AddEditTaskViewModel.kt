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
            _uiState.value = state.copy(isTitleError = true)
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true)
            try {
                val savedTaskId = if (state.id != null) {
                    val existingTask = taskRepository.getTaskById(state.id)
                    existingTask?.let {
                        val updatedTask = it.copy(
                            title = state.title,
                            description = state.description.ifBlank { null },
                            dueDate = state.dueDate,
                            priority = state.priority
                        )
                        taskRepository.updateTask(updatedTask)
                        reminderManager.cancelReminder(it.id)
                        if (state.dueDate != null) {
                            reminderManager.scheduleReminder(updatedTask)
                        }
                    }
                    state.id
                } else {
                    val newTaskId = taskRepository.insertTask(
                        com.todoapp.domain.model.Task(
                            title = state.title,
                            description = state.description.ifBlank { null },
                            dueDate = state.dueDate,
                            priority = state.priority
                        )
                    )
                    if (state.dueDate != null) {
                        val newTask = taskRepository.getTaskById(newTaskId)
                        newTask?.let { reminderManager.scheduleReminder(it) }
                    }
                    newTaskId
                }
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = state.copy(isLoading = false)
            }
        }
    }
}
