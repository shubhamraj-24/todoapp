# Todo App - Senior Android Engineer Implementation

A production-quality Todo/Task Manager Android app built with modern Android architecture and best practices.

## Overview

This app implements all the requirements from the fresher onboarding task, with a focus on:
- Clean architecture (MVVM + Repository pattern)
- Kotlin coroutines & Flow for async operations
- Jetpack Compose for UI
- Room for local data persistence
- Hilt for dependency injection
- Compose Navigation for screen navigation
- AlarmManager & BroadcastReceiver for reminders
- AIDL for inter-process communication (IPC)

## Architecture

The app follows a clean, layered architecture:

```
com.todoapp/
├── data/
│   ├── database/          # Room database, DAOs, entities
│   │   ├── dao/
│   │   └── entity/
│   └── repository/        # Repository implementations
├── di/                    # Hilt dependency injection modules
├── domain/
│   ├── model/             # Domain models
│   └── repository/        # Repository interfaces
├── ui/
│   ├── navigation/        # Navigation graph
│   ├── screens/           # Screen Composables + ViewModels
│   ├── components/        # Reusable UI components
│   └── theme/             # Material 3 theme
├── service/               # AIDL service
├── receiver/              # Broadcast receivers
└── util/                  # Utility classes
```

## Key Features

1. **Task Management**: CRUD operations for tasks
2. **Task List**: Filter tasks by All/Active/Completed, sort by Date/Priority
3. **Task Details**: Full view of task with complete/delete options
4. **Reminders**: Scheduled notifications for tasks with due dates
5. **Boot Persistence**: Reminders are rescheduled after device reboot
6. **Deep Links**: Directly open tasks via `taskapp://task/{taskId}`
7. **AIDL Service**: Exposes task count via IPC

## Tech Stack

- **Language**: Kotlin 1.9+
- **Minimum SDK**: API 26 (Android 8.0)
- **Target SDK**: API 35 (Android 15)
- **UI Framework**: Jetpack Compose + Material 3
- **Architecture**: MVVM with Clean Architecture
- **DI**: Hilt 2.51.1
- **Local DB**: Room 2.6.1
- **Navigation**: Compose Navigation 2.8.3
- **Async**: Coroutines + Flow
- **Notifications**: Notification Channels (API 26+)
- **IPC**: AIDL

## Building and Running

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17 or later
- Android SDK with API 35

### Build Instructions

1. Clone or open the project in Android Studio
2. Sync Gradle dependencies
3. Run the app on an emulator or physical device (API 26+)

## Testing Deep Links

Use this adb command to test deep linking:
```bash
adb shell am start -a android.intent.action.VIEW -d "taskapp://task/1"
```
This will open task with ID 1 (if it exists).

## Testing AIDL

The app exposes an AIDL service with action `com.todoapp.ACTION_TASK_SERVICE`.
The service has one method: `int getTaskCount()` which returns the current number of tasks.

You can create a simple test client app to bind to this service and call the method.

## Testing Reminders

1. Create a new task with a due date
2. Wait for the due time - a reminder notification will appear
3. Reboot your device - reminders are automatically rescheduled
4. Complete or delete a task - its reminder is canceled

## Running Unit Tests (7+ tests)

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17+

### Test Classes Implemented
1. `TaskListViewModelTest` - Tests task list filtering, sorting, toggling completion, deletion
2. `AddEditTaskViewModelTest` - Tests task creation/editing, field validation
3. `TaskDetailViewModelTest` - Tests task details view, completion toggle, deletion

### Run Instructions (Android Studio)
1. Open Project in Android Studio
2. In the Project panel, navigate to `app/src/test/java/com/todoapp/`
3. Right-click on any test class (e.g., `TaskListViewModelTest.kt`)
4. Select "Run 'TaskListViewModelTest'"
5. To run all unit tests: Right-click on the `test/java` folder → "Run 'Tests in 'todoapp'"

### Run Instructions (Command Line)
```bash
# From project root
./gradlew testDebugUnitTest
```

## Running UI Tests (2+ tests)

### Test Classes Implemented
1. `PriorityBadgeTest` - Tests priority badge displays correct labels for LOW/MEDIUM/HIGH
2. `TaskCardTest` - Tests task card displays content and responds to clicks

### Run Instructions (Android Studio)
1. Open Project in Android Studio
2. In the Project panel, navigate to `app/src/androidTest/java/com/todoapp/`
3. Right-click on any UI test class (e.g., `PriorityBadgeTest.kt`)
4. Select "Run 'PriorityBadgeTest'" (choose an emulator or physical device)
5. To run all UI tests: Right-click on the `androidTest/java` folder → "Run 'Tests in 'todoapp'"

### Run Instructions (Command Line)
```bash
# From project root
./gradlew connectedDebugAndroidTest
```
**Note**: Requires an emulator or physical device to be connected.

## Future Improvements (Outside Current Scope)

- Sync with a backend API
- Home screen widgets
- Recurring tasks
- Task categories/labels
- Dark/light theme toggle
- Import/export tasks

## License

This is a sample project for learning purposes.
