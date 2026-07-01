package com.todoapp.ui.screens.addedit

import com.todoapp.domain.model.Priority
import java.time.Instant

data class AddEditTaskUiState(
    val id: Long? = null,
    val title: String = "",
    val description: String = "",
    val dueDate: Instant? = null,
    val priority: Priority = Priority.MEDIUM,
    val isTitleError: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
