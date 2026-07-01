package com.todoapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.todoapp.ITaskService
import com.todoapp.domain.repository.TaskRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class TaskAidlService : Service() {

    @Inject
    lateinit var taskRepository: TaskRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val binder = object : ITaskService.Stub() {
        override fun getTaskCount(): Int {
            return runBlocking {
                taskRepository.getTaskCount()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
}
