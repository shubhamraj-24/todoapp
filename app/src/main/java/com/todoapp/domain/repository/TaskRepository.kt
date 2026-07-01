package com.todoapp.domain.repository

import com.todoapp.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeAllTasks(): Flow<List<Task>>
    fun observeActiveTasks(): Flow<List<Task>>
    fun observeCompletedTasks(): Flow<List<Task>>
    suspend fun getTaskById(taskId: Long): Task?
    suspend fun insertTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun getTaskCount(): Int
    suspend fun getTasksWithReminders(): List<Task>
}
