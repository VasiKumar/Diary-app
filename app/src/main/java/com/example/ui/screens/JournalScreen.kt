package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.DiaryEntry
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Exception-safe helper to parse dates from SQLite
private fun safeParseLocalDate(dateStr: String, formatter: DateTimeFormatter): LocalDate {
    return try {
        LocalDate.parse(dateStr, formatter)
    } catch (e: Throwable) {
        LocalDate.now()
    }
}

// Exception-safe formatter to replace dangerous Instant logic on Android backports/JVM
private fun formatLastUpdated(lastUpdatedInMillis: Long): String {
    if (lastUpdatedInMillis <= 0) return "Just now"
    return try {
        val sdf = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
        sdf.format(java.util.Date(lastUpdatedInMillis))
    } catch (e: Throwable) {
        "Recently"
    }
}

@Composable
fun JournalScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val currentDiary by viewModel.currentDiaryEntry.collectAsStateWithLifecycle()
    val allDiaries by viewModel.allDiaries.collectAsStateWithLifecycle()
    val isWritingMode by viewModel.isWritingMode.collectAsStateWithLifecycle()

    var journalText by remember { mutableStateOf("") }

    // Sync input field state when entering writing mode or changing date, avoiding loops while typing
    LaunchedEffect(selectedDate, isWritingMode) {
        if (isWritingMode) {
            journalText = currentDiary?.content ?: ""
        }
    }

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val displayFormat = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")

    Box(modifier = modifier.fillMaxSize()) {
        // MAIN DIARY FEED
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Header Screen Title & "+ New Page" Action Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Diary",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Preserve your life moments",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                // '+ New Page' Button
                Button(
                    onClick = {
                        viewModel.selectDate(LocalDate.now())
                        viewModel.setWritingMode(true)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.testTag("btn_new_page")
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

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle / Today preview section
            val todayEntry = allDiaries.firstOrNull { it.date == LocalDate.now().format(dateFormatter) }
            val hasTodayEntry = todayEntry != null && todayEntry.content.isNotBlank()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.selectDate(LocalDate.now())
                        viewModel.setWritingMode(true)
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (hasTodayEntry) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (hasTodayEntry) Icons.Filled.EditNote else Icons.Filled.Create,
                            contentDescription = "Today",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "TODAY'S PAGE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (hasTodayEntry) "You have authored a page today. Tap to expand." else "Capture your mind today. Write a new page.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Open",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Historic Reflections Library Folder
            Text(
                text = "My Diary History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
            )

            // Feed List
            val nonBlankDiaries = allDiaries.filter { it.content.isNotBlank() }
            if (nonBlankDiaries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = "Empty Diary",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Your journal is completely pristine.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Press '+ New Page' to write your very first thoughts today. Your future self will thank you.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    items(nonBlankDiaries, key = { it.date }) { entry ->
                        val entryDate = safeParseLocalDate(entry.date, dateFormatter)
                        val isToday = entryDate == LocalDate.now()

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectDate(entryDate)
                                    viewModel.setWritingMode(true)
                                }
                                .testTag("journal_history_item_${entry.date}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isToday) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (isToday) "Today" else entryDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "•  ${entryDate.format(DateTimeFormatter.ofPattern("EEEE"))}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Filled.ChevronRight,
                                        contentDescription = "Edit entry",
                                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = entry.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 22.sp,
                                    fontWeight = FontWeight.Light
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Last updated: " + formatLastUpdated(entry.lastUpdated),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )

                                    // Preview indicator / edit button
                                    Text(
                                        text = "Open Editor →",
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
        }

        // FULLSCREEN WRITING AND PREVIEW CANVAS
        AnimatedVisibility(
            visible = isWritingMode,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Transparent
            ) {
                val radialGradient = androidx.compose.ui.graphics.Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF2B2135), // Immersive, sophisticated deep amethyst center
                        Color(0xFF131118)  // Clean obsidian edge fallback
                    ),
                    radius = 2200f
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(radialGradient)
                        .windowInsetsPadding(WindowInsets.statusBars)
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.saveDiaryEntryImmediately(journalText)
                                viewModel.setWritingMode(false)
                            },
                            modifier = Modifier.testTag("btn_close_editor")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to list",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = selectedDate.format(displayFormat),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.CloudDone,
                                    contentDescription = "Saved offline",
                                    tint = SageProductive,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Auto-saved completely offline",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SageProductive,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.saveDiaryEntryImmediately(journalText)
                                viewModel.setWritingMode(false)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = "Done",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Separation Divider line
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        thickness = 1.dp
                    )

                    // Typing Canvas Area - Single weight block with automatic internal scrolling behavior
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .imePadding() // Shrink content container accurately when IME is up
                            .padding(24.dp)
                    ) {
                        TextField(
                            value = journalText,
                            onValueChange = {
                                journalText = it
                                viewModel.saveDiaryEntry(it, "Neutral")
                            },
                            placeholder = {
                                Text(
                                    text = "Write your thoughts freely here...\n\nHow is your day? Highlight accomplishments, clarify worries, document experiences, or capture inspiring ideas. No limits, pure thought.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                    lineHeight = 28.sp,
                                    fontWeight = FontWeight.Light
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f) // Grow to cover full viewport area and scroll natively!
                                .testTag("journal_input"),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 18.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight.Light,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }
    }
}
