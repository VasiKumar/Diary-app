package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Habit
import com.example.data.HabitCompletion
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val HabitCategories = listOf("Health" to "🍎", "Mind" to "🧠", "Fitness" to "🏋️", "Work" to "💼", "Learning" to "📚", "General" to "🌟")

@Composable
fun HabitHubScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val habits by viewModel.allHabits.collectAsStateWithLifecycle()
    val completions by viewModel.allHabitCompletions.collectAsStateWithLifecycle()

    var showAddForm by remember { mutableStateOf(false) }
    var habitName by remember { mutableStateOf("") }
    var habitDesc by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("General") }

    val defaultHabits = listOf(
        Triple("Meditate 10 minutes", "Pause, breathe, focus in silence", "Mind"),
        Triple("Drink 3L of water", "Maintain absolute body hydration", "Health"),
        Triple("Read 15 pages", "Expand mindset and critical skills", "Learning"),
        Triple("Daily Workout", "Engage in physical fitness challenge", "Fitness")
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp, top = 8.dp)
    ) {
        // Core Dashboard Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Habit Hub",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${habits.size} routines active",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { showAddForm = !showAddForm },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showAddForm) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("toggle_add_habit_form")
                ) {
                    Icon(
                        imageVector = if (showAddForm) Icons.Filled.Close else Icons.Filled.Add,
                        contentDescription = "New Habit"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (showAddForm) "Cancel" else "Add Habit")
                }
            }
        }

        // Add Habit Expandable Card Form
        item {
            AnimatedVisibility(
                visible = showAddForm,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .testTag("add_habit_card"),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Configure New Habit",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        OutlinedTextField(
                            value = habitName,
                            onValueChange = { habitName = it },
                            label = { Text("What habit do you want to build?") },
                            placeholder = { Text("e.g. Read physical books, Morning cold shower") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("habit_name_input"),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = habitDesc,
                            onValueChange = { habitDesc = it },
                            label = { Text("Habit goal or cue (optional)") },
                            placeholder = { Text("e.g. Read right after waking up with green tea") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("habit_desc_input"),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )

                        // Habit Category Selector
                        Text(
                            text = "Focus Category",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HabitCategories.forEach { (cat, emoji) ->
                                val isSelected = cat == selectedCategory
                                val baseColor = when(cat) {
                                    "Mind" -> Color(0xFFB39DDB)
                                    "Health" -> Color(0xFF81C784)
                                    "Fitness" -> Color(0xFFFFF176)
                                    "Work" -> Color(0xFF64B5F6)
                                    "Learning" -> Color(0xFFFFB74D)
                                    else -> MaterialTheme.colorScheme.primary
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1.0f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) baseColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                        .clickable { selectedCategory = cat }
                                        .padding(vertical = 8.dp)
                                        .testTag("cat_$cat"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(emoji, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = cat,
                                            fontSize = 9.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (habitName.isNotBlank()) {
                                    viewModel.addHabit(habitName.trim(), habitDesc.trim(), selectedCategory)
                                    habitName = ""
                                    habitDesc = ""
                                    selectedCategory = "General"
                                    showAddForm = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_habit_button"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Create Focused Habit")
                        }
                    }
                }
            }
        }

        // Habit Hub Lists
        if (habits.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = "No habits",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Habits Configured Yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Habits are daily systems to compound mental wellness and focus. Tap below to import standard templates to get going instantly!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick Import Template Templates
                        Text(
                            text = "Import Wellness Standard Starters:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            defaultHabits.forEach { (name, desc, category) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                        .clickable {
                                            viewModel.addHabit(name, desc, category)
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1.0f)) {
                                        Text(text = name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                        Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text(
                                        text = "+ Import",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            items(habits, key = { it.id }) { habit ->
                val habitCompletions = completions.filter { it.habitId == habit.id }
                HabitDetailRow(
                    habit = habit,
                    completions = habitCompletions,
                    onDelete = { viewModel.deleteHabit(habit.id) }
                )
            }
        }
    }
}

@Composable
fun HabitDetailRow(
    habit: Habit,
    completions: List<HabitCompletion>,
    onDelete: () -> Unit
) {
    val totalCompletionsCount = completions.size
    
    // Lookback 30 Days calculations
    val last30Days = (0..29).map { LocalDate.now().minusDays(it.toLong()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) }
    val completionsIn30Days = completions.count { it.date in last30Days }
    val percentIn30Days = if (completionsIn30Days > 0) {
        (completionsIn30Days.toFloat() / 30f).coerceIn(0f, 1f)
    } else {
        0.0f
    }

    val emoji = when(habit.category) {
        "Health" -> "🍎"
        "Mind" -> "🧠"
        "Fitness" -> "🏋️"
        "Work" -> "💼"
        "Learning" -> "📚"
        else -> "🌟"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("habit_detail_${habit.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Category emoji, Title, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(emoji, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Success: ${(percentIn30Days * 100).toInt()}% past 30 days",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_habit_${habit.id}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = "Delete Habit",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }
            }

            if (habit.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = habit.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 7-day completion bubbles (horizontal track showing checklist completions)
            Text(
                text = "Last 7 Days Tracker:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Past 7 days (Monday..Sunday, or looking back from today)
                val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val bubbleFormatter = DateTimeFormatter.ofPattern("E")
                (6 downTo 0).forEach { offset ->
                    val date = LocalDate.now().minusDays(offset.toLong())
                    val dateStr = date.format(dateFormatter)
                    val completed = completions.any { it.date == dateStr }
                    val label = date.format(bubbleFormatter).take(1)

                    val bubbleBg = if (completed) SageProductive else MaterialTheme.colorScheme.surfaceVariant
                    val textColor = if (completed) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(bubbleBg),
                            contentAlignment = Alignment.Center
                        ) {
                            if (completed) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Done",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            }
                        }
                        Text(
                            text = if (offset == 0) "Today" else date.format(DateTimeFormatter.ofPattern("M/d")),
                            fontSize = 8.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}
