package com.todoapp.domain.model

import java.time.Instant

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val dueDate: Instant? = null,
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val createdAt: Instant = Instant.now()
)
