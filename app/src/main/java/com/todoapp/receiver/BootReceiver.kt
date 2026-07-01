package com.todoapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.todoapp.TodoApplication
import com.todoapp.domain.repository.TaskRepository
import com.todoapp.util.ReminderManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var taskRepository: TaskRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            context?.let {
                val reminderManager = ReminderManager(it)
                scope.launch {
                    val tasks = taskRepository.getTasksWithReminders()
                    tasks.forEach { task ->
                        reminderManager.scheduleReminder(task)
                    }
                }
            }
        }
    }
}
