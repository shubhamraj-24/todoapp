package com.todoapp.data.repository

import com.todoapp.data.database.dao.TaskDao
import com.todoapp.data.database.entity.toDomainModel
import com.todoapp.data.database.entity.toEntity
import com.todoapp.domain.model.Task
import com.todoapp.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun observeAllTasks(): Flow<List<Task>> {
        return taskDao.observeAllTasks().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun observeActiveTasks(): Flow<List<Task>> {
        return taskDao.observeActiveTasks().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun observeCompletedTasks(): Flow<List<Task>> {
        return taskDao.observeCompletedTasks().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getTaskById(taskId: Long): Task? {
        return taskDao.getTaskById(taskId)?.toDomainModel()
    }

    override suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task.toEntity())
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
    }

    override suspend fun getTaskCount(): Int {
        return taskDao.getTaskCount()
    }

    override suspend fun getTasksWithReminders(): List<Task> {
        return taskDao.getTasksWithReminders().map { it.toDomainModel() }
    }
}
