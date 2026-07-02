# Todo App

[![Kotlin Version](https://img.shields.io/badge/kotlin-1.9-blue.svg)](http://kotlinlang.org/)
[![Platform](https://img.shields.io/badge/platform-Android-green.svg)](https://www.android.com)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg)](https://android-arsenal.com/api?level=26)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A production-quality Todo/Task Manager Android app built with modern Android architecture and best practices.

## Overview

This app implements a robust task management system with a focus on:
- **Clean Architecture**: Separation of concerns between Data, Domain, and UI layers.
- **Modern UI**: Built entirely with Jetpack Compose and Material 3.
- **Reactive Programming**: Using Kotlin Coroutines and StateFlow for unidirectional data flow.
- **Dependency Injection**: Full Hilt integration for modular and testable code.
- **Local Persistence**: Room database for offline-first capability.
- **Inter-Process Communication**: AIDL service for external task queries.
- **System Integration**: AlarmManager for precise reminders and BootReceiver for persistence.

## Architecture

The app follows a clean, layered architecture to ensure scalability and maintainability:

```
com.todoapp/
├── data/
│   ├── database/          # Room database configuration, DAOs, and Entities
│   └── repository/        # Repository implementations (Mapping Data to Domain)
├── di/                    # Hilt Modules for dependency management
├── domain/
│   ├── model/             # Pure Kotlin domain models
│   └── repository/        # Repository interfaces (The boundary)
├── ui/
│   ├── navigation/        # Type-safe Navigation Graph & Deep Link handling
│   ├── screens/           # Screen-level Composables & ViewModels
│   ├── components/        # Reusable UI building blocks
│   └── theme/             # Material 3 Design System (Colors, Typography, Shapes)
├── service/               # AIDL Service implementation for IPC
├── receiver/              # Broadcast Receivers (Alarm & Device Boot)
└── util/                  # Shared utilities (Notification/Reminder management)

# Test Infrastructure (debug source set)
├── HiltTestActivity       # Generic host for isolated UI testing
└── di/TestDatabaseModule  # In-memory database provider for test isolation
```

## Key Features

1.  **Task CRUD**: Complete Create, Read, Update, and Delete lifecycle for tasks.
2.  **Advanced Filtering**: Filter tasks by status (All, Active, Completed).
3.  **Smart Sorting**: Sort tasks by Due Date or Priority levels.
4.  **Reliable Reminders**: Local notifications triggered by AlarmManager.
5.  **Resiliency**: Reminders are automatically restored after a device reboot.
6.  **Deep Linking**: Navigate directly to task details using `taskapp://task/{id}`.
7.  **IPC Access**: External apps can query the total task count via AIDL.

## Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **DI**: [Hilt](https://dagger.dev/hilt/)
- **Database**: [Room](https://developer.android.com/training/data-storage/room)
- **Navigation**: [Compose Navigation](https://developer.android.com/jetpack/compose/navigation)
- **Async**: [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
- **IPC**: [AIDL](https://developer.android.com/guide/components/aidl)

## Building and Running

### Prerequisites
- Android Studio Hedgehog+
- JDK 17
- Android SDK 35

### Steps
1. Clone the repository.
2. Open in Android Studio and perform a **Gradle Sync**.
3. Run on an emulator or physical device (API 26+).

## Testing

### Unit Testing
Comprehensive testing of ViewModels and Repositories using MockK and Coroutines Test.
- **GUI**: Right-click `app/src/test` -> **Run 'Tests in com.todoapp'**.
- **CLI**: `./gradlew testDebugUnitTest`

### UI Testing (Instrumented)
Isolated component testing using Hilt and an in-memory database.
- **GUI**: Right-click `app/src/androidTest` -> **Run 'All Tests'**.
- **CLI**: `./gradlew connectedDebugAndroidTest`

### Deep Link Testing
Test navigation via ADB:
```bash
adb shell am start -a android.intent.action.VIEW -d "taskapp://task/1"
```

## License
MIT License. See [LICENSE](LICENSE) for details.
