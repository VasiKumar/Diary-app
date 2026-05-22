package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.ui.viewmodel.DayCategory
import com.example.ui.viewmodel.HeatmapDay
import com.example.ui.viewmodel.MainViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun FlowMapScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val streak by viewModel.currentStreak.collectAsStateWithLifecycle()
    val weeklyRate by viewModel.weeklyCompletionRate.collectAsStateWithLifecycle()
    val habitRate by viewModel.habitSuccessRate.collectAsStateWithLifecycle()
    val heatmapData by viewModel.heatmapData.collectAsStateWithLifecycle()
    val remindersEnabled by viewModel.remindersEnabled.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var selectedHeatmapDay by remember { mutableStateOf<HeatmapDay?>(null) }
    val displayFormat = DateTimeFormatter.ofPattern("MMM d, yyyy")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp, top = 8.dp)
    ) {
        // Core Header
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    text = "Flow Dashboard",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Insights, analytics, and offline triggers",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 1. Core Analytics Cards (Row of 3 Stats)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Streak Card
                Card(
                    modifier = Modifier.weight(1.0f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Whatshot,
                            contentDescription = "Streak",
                            tint = CoralMissed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$streak Days",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Streak",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Weekly Complete Card
                Card(
                    modifier = Modifier.weight(1.0f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Weekly",
                            tint = SageProductive,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${(weeklyRate * 100).toInt()}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Weekly Rate",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Habit Consistency Card
                Card(
                    modifier = Modifier.weight(1.0f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = "Habits",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${(habitRate * 100).toInt()}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Habit Success",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 2. GitHub-Style Heatmap Calendar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Consistency Map",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "140-day Lookback",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (heatmapData.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        // Display Heatmap Calendar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Column representing week labels
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(end = 4.dp, top = 2.dp)
                            ) {
                                val weekdays = listOf("S", "M", "T", "W", "T", "F", "S")
                                weekdays.forEach { day ->
                                    Text(
                                        text = day,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.height(14.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            // The Scrollable Heatmap grid representation
                            // 20 columns * 7 days
                            val scrollState = rememberScrollState()
                            LaunchedEffect(heatmapData) {
                                // Auto-scroll to the end (most recent dates)
                                scrollState.scrollTo(scrollState.maxValue)
                            }

                            Row(
                                modifier = Modifier
                                    .weight(1.0f)
                                    .horizontalScroll(scrollState)
                                    .testTag("heatmap_grid"),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val chunkedWeeks = heatmapData.chunked(7)
                                chunkedWeeks.forEachIndexed { weekIndex, weekDays ->
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        weekDays.forEach { day ->
                                            val cellColor = when (day.category) {
                                                DayCategory.PRODUCTIVE -> {
                                                    if (day.completedCount >= 3) SageProductive else SageProductive.copy(alpha = 0.5f)
                                                }
                                                DayCategory.UNPRODUCTIVE -> CoralMissed
                                                DayCategory.NEUTRAL -> if (MaterialTheme.colorScheme.background == Color(0xFF111418)) NeutralEmpty else MaterialTheme.colorScheme.surfaceVariant
                                            }

                                            val isSelected = selectedHeatmapDay?.date == day.date
                                            val cellBorder = if (isSelected) {
                                                BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                                            } else {
                                                null
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clip(RoundedCornerShape(3.dp))
                                                    .background(cellColor)
                                                    .then(if (cellBorder != null) Modifier.border(cellBorder, RoundedCornerShape(3.dp)) else Modifier)
                                                    .clickable {
                                                        selectedHeatmapDay = day
                                                    }
                                                    .testTag("heatmap_day_${day.date}")
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Selected Heatmap detail bubble or Legend
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Legend
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Less", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(if (MaterialTheme.colorScheme.background == Color(0xFF111418)) NeutralEmpty else MaterialTheme.colorScheme.surfaceVariant))
                                Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(CoralMissed))
                                Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(SageProductive.copy(alpha = 0.5f)))
                                Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(SageProductive))
                                Text("More", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SageProductive))
                                Text("Productive", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = SageProductive)
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(CoralMissed))
                                Text("Missed", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = CoralMissed)
                            }
                        }

                        // Selected day description pop-up drawer
                        AnimatedVisibility(
                            visible = selectedHeatmapDay != null,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            val day = selectedHeatmapDay
                            if (day != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = day.date.format(displayFormat),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            val statsText = when (day.category) {
                                                DayCategory.PRODUCTIVE -> "Extremely Productive! 🔥 Done $day.completedCount things."
                                                DayCategory.UNPRODUCTIVE -> "No checklist items completed. ⚠️ Shift your energy!"
                                                DayCategory.NEUTRAL -> "Rest day. No goals schedules."
                                            }
                                            Text(
                                                text = statsText,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        IconButton(onClick = { selectedHeatmapDay = null }) {
                                            Icon(
                                                imageVector = Icons.Filled.Close,
                                                contentDescription = "Hide bubble",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Custom Canvas-Drawn Bar Chart: Past 7 Days Productivity Rate
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Weekly Completion History",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Success percentage for the past 7 days",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val past7Days = remember(heatmapData) {
                        heatmapData.takeLast(7)
                    }

                    if (past7Days.isNotEmpty()) {
                        CustomWeeklyBarChart(days = past7Days)
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Loading weekly stats...", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }

        // 4. Local Reminders Panel (Control Center)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Offline Reminders",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Keep consistent with daily alarms",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = remindersEnabled,
                            onCheckedChange = { viewModel.toggleReminders(it) },
                            modifier = Modifier.testTag("reminders_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Reminders will alert you locally completely offline every day. Goal alarms schedule at 8:00 PM and entry logs at 9:00 PM.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Immediate Test Triggers:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.triggerTestNotification(
                                    title = "Did you complete today's goals?",
                                    message = "Take 30 seconds to check off your habits and goals checklist now! 🌟"
                                )
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1.0f)
                                .testTag("test_goal_reminder_button")
                        ) {
                            Text("Test Goal notification", fontSize = 10.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.triggerTestNotification(
                                    title = "Don't forget to write today's diary.",
                                    message = "Reflect on how today went, update your mood indicator, and rest easy. 😴"
                                )
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1.0f)
                                .testTag("test_diary_reminder_button")
                        ) {
                            Text("Test Diary notification", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomWeeklyBarChart(days: List<HeatmapDay>) {
    val barColor = MaterialTheme.colorScheme.primary
    val gridLineColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f)
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(horizontal = 8.dp)
            .testTag("weekly_bar_chart")
    ) {
        val totalWidth = size.width
        val canvasHeight = size.height

        val paddingBottom = 20.dp.toPx()
        val chartHeight = canvasHeight - paddingBottom

        val barCount = days.size
        val barWidth = 14.dp.toPx()
        val spacing = (totalWidth - (barWidth * barCount)) / (barCount + 1)

        // Draw horizontal grid lines guides
        val linesCount = 4
        for (i in 0..linesCount) {
            val y = i * (chartHeight / linesCount)
            drawLine(
                color = gridLineColor,
                start = Offset(0f, y),
                end = Offset(totalWidth, y),
                strokeWidth = 1f
            )
        }

        days.forEachIndexed { index, day ->
            val maxCompletionsPossible = 5.0f
            val countFraction = (day.completedCount.toFloat() / maxCompletionsPossible).coerceIn(0.0f, 1.0f)
            
            // Draw bar matching success rate
            val bHeight = chartHeight * countFraction
            val x = spacing + index * (barWidth + spacing)
            val y = chartHeight - bHeight

            val individualBarColor = when {
                day.category == DayCategory.PRODUCTIVE -> SageProductive
                day.category == DayCategory.UNPRODUCTIVE -> CoralMissed
                else -> barColor.copy(alpha = 0.4f)
            }

            // Draw rounded bar
            drawRoundRect(
                color = individualBarColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, bHeight),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )

            // Draw date label at bottom
            val dayLabel = day.date.format(DateTimeFormatter.ofPattern("M/d"))
            // We can also draw simple text or just rely on a standard layout below.
            // Since drawContext does allow drawString, we can position labels nicely using basic Canvas metrics.
            // Alternatively, in Compose, it's easier to position labels in a Row underneath the Canvas.
        }
    }

    // Row of date labels matching the index above!
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEach { day ->
            val dayLabel = day.date.format(DateTimeFormatter.ofPattern("M/d"))
            val labelColor = when (day.category) {
                DayCategory.PRODUCTIVE -> SageProductive
                DayCategory.UNPRODUCTIVE -> CoralMissed
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            Text(
                text = dayLabel,
                fontSize = 10.sp,
                color = labelColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(28.dp)
            )
        }
    }
}
