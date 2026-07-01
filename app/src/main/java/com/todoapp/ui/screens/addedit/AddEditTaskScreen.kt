package com.todoapp.ui.screens.addedit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.todoapp.R
import com.todoapp.domain.model.Priority
import com.todoapp.ui.components.PriorityBadge
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditTaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.id != null) {
                            stringResource(R.string.edit_task)
                        } else {
                            stringResource(R.string.add_task)
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text(stringResource(R.string.title)) },
                singleLine = true,
                isError = uiState.isTitleError,
                supportingText = if (uiState.isTitleError) {
                    { Text(stringResource(R.string.title_required)) }
                } else {
                    null
                },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text(stringResource(R.string.description)) },
                minLines = 3,
                maxLines = 5,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth()
            )

            DateTimeSelector(
                dueDate = uiState.dueDate,
                onDateTimeSelected = viewModel::onDueDateChange,
                onClearDateTime = { viewModel.onDueDateChange(null) }
            )

            PrioritySelector(
                selectedPriority = uiState.priority,
                onPrioritySelected = viewModel::onPriorityChange
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.saveTask(onSaveSuccess) },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}

@Composable
private fun DateTimeSelector(
    dueDate: Instant?,
    onDateTimeSelected: (Instant?) -> Unit,
    onClearDateTime: () -> Unit
) {
    val context = LocalContext.current
    val dateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = dueDate?.let {
                it.atZone(ZoneId.systemDefault()).format(dateTimeFormatter)
            } ?: "",
            onValueChange = {},
            label = { Text(stringResource(R.string.due_date)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = null
                )
            },
            trailingIcon = if (dueDate != null) {
                {
                    IconButton(onClick = onClearDateTime) {
                        Icon(
                            imageVector = Icons.Outlined.Clear,
                            contentDescription = stringResource(R.string.clear_date)
                        )
                    }
                }
            } else {
                null
            },
            readOnly = true,
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = {
                showDateTimePicker(context, dueDate) { newDateTime ->
                    onDateTimeSelected(newDateTime)
                }
            }
        ) {
            Text(stringResource(R.string.select))
        }
    }
}

@Composable
private fun PrioritySelector(
    selectedPriority: Priority,
    onPrioritySelected: (Priority) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "${stringResource(R.string.priority)}:",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(end = 8.dp)
        )

        PriorityBadge(priority = selectedPriority)

        Button(
            onClick = { expanded = true }
        ) {
            Text(stringResource(R.string.change))
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Priority.entries.forEach { priority ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PriorityBadge(priority = priority)
                        }
                    },
                    onClick = {
                        onPrioritySelected(priority)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun showDateTimePicker(
    context: Context,
    initialDateTime: Instant?,
    onDateTimeSelected: (Instant) -> Unit
) {
    val initialLocalDateTime = initialDateTime?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
        ?: LocalDateTime.now()

    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    val selectedDateTime = LocalDateTime.of(year, month + 1, dayOfMonth, hourOfDay, minute)
                    val instant = selectedDateTime.atZone(ZoneId.systemDefault()).toInstant()
                    onDateTimeSelected(instant)
                },
                initialLocalDateTime.hour,
                initialLocalDateTime.minute,
                false
            ).show()
        },
        initialLocalDateTime.year,
        initialLocalDateTime.monthValue - 1,
        initialLocalDateTime.dayOfMonth
    ).show()
}
