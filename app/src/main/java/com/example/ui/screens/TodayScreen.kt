package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Goal
import com.example.data.Habit
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TodayScreen(
    viewModel: MainViewModel,
    onNavigateToJournal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val goals by viewModel.currentGoals.collectAsStateWithLifecycle()
    val habits by viewModel.allHabits.collectAsStateWithLifecycle()
    val habitCompletions by viewModel.currentHabitCompletions.collectAsStateWithLifecycle()
    val diaryEntry by viewModel.currentDiaryEntry.collectAsStateWithLifecycle()
    val streak by viewModel.currentStreak.collectAsStateWithLifecycle()

    var newGoalText by remember { mutableStateOf("") }
    val isToday = selectedDate == LocalDate.now()

    val dateDisplayFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d")
    val selectedDateFormatted = selectedDate.format(dateDisplayFormatter)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp, top = 8.dp)
    ) {
        // 1. Date Header Navigator
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.selectDate(selectedDate.minusDays(1)) },
                        modifier = Modifier.testTag("prev_date_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Day",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1.0f)
                    ) {
                        Text(
                            text = if (isToday) "TODAY" else selectedDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy")).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = selectedDateFormatted,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Light, // light tracking-tight feel from spec
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = { viewModel.selectDate(selectedDate.plusDays(1)) },
                        modifier = Modifier.testTag("next_date_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Day",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // 2. Streak & Encouragement Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (streak > 0) Icons.Filled.Whatshot else Icons.Filled.SelfImprovement,
                            contentDescription = "Streak",
                            tint = if (streak > 0) CoralMissed else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        val greeting = when {
                            streak > 0 -> "You're on a $streak-day streak! 🔥"
                            else -> "Start today's flow!"
                        }
                        Text(
                            text = greeting,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (streak > 0) "Keep up the momentum. Consistency builds clarity." else "Add a habit or goal below to start your focus journey.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // 3. Quick Diary Section
        item {
            Text(
                text = "Diary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.setWritingMode(true)
                        onNavigateToJournal()
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = "Diary",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (diaryEntry != null) "Today's Written Page" else "Saved completely offline",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (diaryEntry != null) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit Diary",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (diaryEntry != null) {
                        Text(
                            text = diaryEntry?.content ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tap to open and edit page →",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Capture your mind today. No limits, pure thought.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    viewModel.setWritingMode(true)
                                    onNavigateToJournal()
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "New Page",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "New Page",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Daily Goals checklist section
        item {
            Text(
                text = "Today's Absolute Focus",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 8.dp)
            )
        }

        // Input Box for adding goals
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newGoalText,
                    onValueChange = { newGoalText = it },
                    placeholder = { Text("Add standard or specific goal...") },
                    modifier = Modifier
                        .weight(1.0f)
                        .testTag("new_goal_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (newGoalText.isNotBlank()) {
                            viewModel.addGoal(newGoalText.trim())
                            newGoalText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(12.dp))
                        .testTag("add_goal_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Goal",
                        tint = Color.White
                    )
                }
            }
        }

        // Goals List Header
        if (goals.isNotEmpty()) {
            item {
                Text(
                    text = " Checklist Goals",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
            items(goals, key = { it.id }) { goal ->
                GoalItemRow(
                    goal = goal,
                    onToggle = { viewModel.toggleGoalCompletion(goal) },
                    onDelete = { viewModel.deleteGoal(goal.id) }
                )
            }
        }

        // Habits List Header
        if (habits.isNotEmpty()) {
            item {
                Text(
                    text = " Daily Habits Checklist",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            items(habits, key = { it.id }) { habit ->
                val isCompleted = habitCompletions.any { it.habitId == habit.id }
                HabitItemRow(
                    habit = habit,
                    isCompleted = isCompleted,
                    onToggle = { viewModel.toggleHabit(habit.id) }
                )
            }
        }

        // Checklist Empty State Case
        if (goals.isEmpty() && habits.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.FactCheck,
                            contentDescription = "No items",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "A complete blank canvas!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Add goals above or visit Habit Hub to activate personal routines.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.alpha(0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GoalItemRow(
    goal: Goal,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val leftAccentColor = if (goal.isCompleted) SageProductive else CleanPrimary
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("goal_item_${goal.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left stripe border indicator
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(leftAccentColor)
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = goal.isCompleted,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = SageProductive,
                        checkmarkColor = MaterialTheme.colorScheme.surface,
                        uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("goal_checkbox_${goal.id}")
                )

                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = if (goal.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    fontWeight = if (goal.isCompleted) FontWeight.Normal else FontWeight.Medium,
                    color = if (goal.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1.0f)
                        .padding(horizontal = 8.dp)
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_goal_${goal.id}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HabitItemRow(
    habit: Habit,
    isCompleted: Boolean,
    onToggle: () -> Unit
) {
    val leftAccentColor = if (isCompleted) SageProductive else CleanPrimary
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("habit_item_${habit.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left stripe border indicator
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(leftAccentColor)
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = SageProductive,
                        checkmarkColor = MaterialTheme.colorScheme.surface,
                        uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("habit_checkbox_${habit.id}")
                )

                Column(
                    modifier = Modifier
                        .weight(1.0f)
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                    )
                    if (habit.description.isNotBlank()) {
                        Text(
                            text = habit.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                SuggestionChip(
                    onClick = { },
                    label = { Text(habit.category, fontSize = 10.sp) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        labelColor = MaterialTheme.colorScheme.primary
                    ),
                    border = null,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
        }
    }
}

