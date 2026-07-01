package com.todoapp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.todoapp.domain.model.Priority
import com.todoapp.domain.model.Task
import java.time.Instant

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val dueDate: Long? = null,
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val createdAt: Long = Instant.now().toEpochMilli()
)

fun TaskEntity.toDomainModel(): Task = Task(
    id = id,
    title = title,
    description = description,
    dueDate = dueDate?.let { Instant.ofEpochMilli(it) },
    priority = priority,
    isCompleted = isCompleted,
    createdAt = Instant.ofEpochMilli(createdAt)
)

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    dueDate = dueDate?.toEpochMilli(),
    priority = priority,
    isCompleted = isCompleted,
    createdAt = createdAt.toEpochMilli()
)
