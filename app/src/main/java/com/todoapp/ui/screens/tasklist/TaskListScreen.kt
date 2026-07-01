package com.todoapp.ui.screens.tasklist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.todoapp.R
import com.todoapp.domain.model.SortOption
import com.todoapp.ui.components.EmptyState
import com.todoapp.ui.components.FilterChips
import com.todoapp.ui.components.LoadingState
import com.todoapp.ui.components.TaskCard
import com.todoapp.ui.theme.TodoAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onAddTaskClick: () -> Unit,
    onTaskClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedFilter by viewModel.filter.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.task_list)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = stringResource(R.string.notification_settings)
                        )
                    }
                    SortMenu(
                        sortOption = sortOption,
                        onSortOptionChange = { viewModel.setSortOption(it) }
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTaskClick) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = stringResource(R.string.add_task)
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            FilterChips(
                selectedFilter = selectedFilter,
                onFilterSelected = { viewModel.setFilter(it) }
            )

            when (uiState) {
                is TaskListUiState.Loading -> LoadingState()
                is TaskListUiState.Error -> {
                    EmptyState(
                        title = "Error",
                        subtitle = (uiState as TaskListUiState.Error).message
                    )
                }
                is TaskListUiState.Success -> {
                    val tasks = (uiState as TaskListUiState.Success).tasks
                    if (tasks.isEmpty()) {
                        EmptyState(
                            title = stringResource(R.string.empty_tasks),
                            subtitle = stringResource(R.string.add_first_task)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(tasks, key = { it.id }) { task ->
                                TaskCard(
                                    task = task,
                                    onTaskClick = onTaskClick,
                                    onToggleComplete = { viewModel.toggleTaskCompletion(it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SortMenu(
    sortOption: SortOption,
    onSortOptionChange: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = stringResource(R.string.sort_options)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SortOption.entries.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            when (option) {
                                SortOption.DATE -> stringResource(R.string.sort_by_date)
                                SortOption.PRIORITY -> stringResource(R.string.sort_by_priority)
                            }
                        )
                    },
                    onClick = {
                        onSortOptionChange(option)
                        expanded = false
                    },
                    trailingIcon = if (sortOption == option) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null
                            )
                        }
                    } else {
                        null
                    }
                )
            }
        }
    }
}
