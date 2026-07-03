package com.todoapp.service

import android.content.Intent
import android.os.IBinder
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ServiceTestRule
import com.todoapp.ITaskService
import com.todoapp.domain.model.Priority
import com.todoapp.domain.model.Task
import com.todoapp.domain.repository.TaskRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TaskAidlServiceTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val serviceRule = ServiceTestRule()

    @Inject
    lateinit var taskRepository: TaskRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    @Throws(TimeoutException::class)
    fun testGetTaskCount() = runTest {
        // Prepare data
        val task1 = Task(title = "Task 1", priority = Priority.LOW)
        val task2 = Task(title = "Task 2", priority = Priority.MEDIUM)
        taskRepository.insertTask(task1)
        taskRepository.insertTask(task2)

        // Bind to the service
        val intent = Intent(ApplicationProvider.getApplicationContext(), TaskAidlService::class.java)
        val binder: IBinder = serviceRule.bindService(intent)
        val service = ITaskService.Stub.asInterface(binder)

        // Verify result
        val count = service.getTaskCount()
        assertEquals(2, count)
    }
}
