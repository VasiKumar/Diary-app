@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.notifier.ReminderHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class DayCategory { PRODUCTIVE, UNPRODUCTIVE, NEUTRAL }
data class HeatmapDay(val date: LocalDate, val category: DayCategory, val completedCount: Int)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = MainRepository(database.appDao())

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    // Observe all diary entries
    val allDiaries: StateFlow<List<DiaryEntry>> = repository.allDiaries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Observe diary entry for selected date
    val currentDiaryEntry: StateFlow<DiaryEntry?> = _selectedDate
        .flatMapLatest { date ->
            repository.getDiaryEntryFlowForDate(date.format(dateFormatter))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Observe all habits
    val allHabits: StateFlow<List<Habit>> = repository.allHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Observe goals for selected date
    val currentGoals: StateFlow<List<Goal>> = _selectedDate
        .flatMapLatest { date ->
            repository.getGoalsForDate(date.format(dateFormatter))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Observe all habit completions
    val allHabitCompletions: StateFlow<List<HabitCompletion>> = repository.allHabitCompletions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter completions for selected date
    val currentHabitCompletions: StateFlow<List<HabitCompletion>> = _selectedDate
        .flatMapLatest { date ->
            repository.getHabitCompletionsForDate(date.format(dateFormatter))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 1. Current productivity streak count
    val currentStreak: StateFlow<Int> = combine(repository.allGoals, repository.allHabitCompletions) { goals, completions ->
        calculateStreak(goals, completions)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // 2. Weekly goal completion percentage
    val weeklyCompletionRate: StateFlow<Float> = combine(repository.allGoals, repository.allHabitCompletions, repository.allHabits) { goals, completions, habits ->
        val last7Days = (0..6).map { LocalDate.now().minusDays(it.toLong()).format(dateFormatter) }.toSet()
        val goalsIn7Days = goals.filter { it.date in last7Days }
        val completedGoalsIn7Days = goalsIn7Days.count { it.isCompleted }
        
        val completionsIn7Days = completions.count { it.date in last7Days }
        
        val totalOpportunities = goalsIn7Days.size + (habits.size * 7)
        val totalCompletions = completedGoalsIn7Days + completionsIn7Days
        
        if (totalOpportunities == 0) 1.0f else totalCompletions.toFloat() / totalOpportunities.toFloat()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1.0f)

    // 3. Habit success rate (completions / active days of tracking in the last 30 days)
    val habitSuccessRate: StateFlow<Float> = combine(repository.allHabitCompletions, repository.allHabits) { completions, habits ->
        if (habits.isEmpty()) return@combine 0.0f
        val last30Days = (0..29).map { LocalDate.now().minusDays(it.toLong()).format(dateFormatter) }
        val activeCompletions = completions.count { it.date in last30Days }
        val maxPossible = habits.size * 30
        (activeCompletions.toFloat() / maxPossible.toFloat()).coerceIn(0.0f, 1.0f)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0f)

    // 4. Heatmap list details for the selected year
    private val _selectedHeatmapYear = MutableStateFlow(LocalDate.now().year)
    val selectedHeatmapYear: StateFlow<Int> = _selectedHeatmapYear.asStateFlow()

    fun setHeatmapYear(year: Int) {
        _selectedHeatmapYear.value = year
    }

    val heatmapData: StateFlow<List<HeatmapDay>> = combine(
        repository.allGoals, 
        repository.allHabitCompletions, 
        repository.allHabits,
        _selectedHeatmapYear
    ) { goals, completions, habits, year ->
        val data = mutableListOf<HeatmapDay>()
        val goalsMap = goals.groupBy { it.date }
        val completionsMap = completions.groupBy { it.date }
        
        val firstDayOfYear = LocalDate.of(year, 1, 1)
        val lastDayOfYear = LocalDate.of(year, 12, 31)
        
        // Find the Sunday on or before Jan 1st. DayOfWeek.value is 1 (Mon) to 7 (Sun)
        val firstDayOfWeekIndex = firstDayOfYear.dayOfWeek.value
        val daysToSubtract = if (firstDayOfWeekIndex == 7) 0 else firstDayOfWeekIndex
        val startDate = firstDayOfYear.minusDays(daysToSubtract.toLong())

        // Find the Saturday on or after Dec 31st
        val lastDayOfWeekIndex = lastDayOfYear.dayOfWeek.value
        val daysToAdd = if (lastDayOfWeekIndex == 7) 6 else 6 - lastDayOfWeekIndex
        val endDate = lastDayOfYear.plusDays(daysToAdd.toLong())

        val totalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1

        for (i in 0 until totalDays) {
            val date = startDate.plusDays(i)
            val dateStr = date.format(dateFormatter)
            
            val dayGoals = goalsMap[dateStr] ?: emptyList()
            val dayCompletions = completionsMap[dateStr] ?: emptyList()
            
            val totalGoals = dayGoals.size
            val completedGoals = dayGoals.count { it.isCompleted }
            val completedHabits = dayCompletions.size
            
            val category = when {
                (completedGoals + completedHabits) > 0 -> DayCategory.PRODUCTIVE
                totalGoals > 0 && completedGoals == 0 -> DayCategory.UNPRODUCTIVE
                else -> DayCategory.NEUTRAL
            }
            
            data.add(HeatmapDay(date, category, completedGoals + completedHabits))
        }
        data
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val past7DaysHeatmapData: StateFlow<List<HeatmapDay>> = combine(
        repository.allGoals, 
        repository.allHabitCompletions, 
        repository.allHabits
    ) { goals, completions, habits ->
        val data = mutableListOf<HeatmapDay>()
        val goalsMap = goals.groupBy { it.date }
        val completionsMap = completions.groupBy { it.date }
        
        val today = LocalDate.now()
        for (i in 6 downTo 0) {
            val date = today.minusDays(i.toLong())
            val dateStr = date.format(dateFormatter)
            
            val dayGoals = goalsMap[dateStr] ?: emptyList()
            val dayCompletions = completionsMap[dateStr] ?: emptyList()
            
            val totalGoals = dayGoals.size
            val completedGoals = dayGoals.count { it.isCompleted }
            val completedHabits = dayCompletions.size
            
            val category = when {
                (completedGoals + completedHabits) > 0 -> DayCategory.PRODUCTIVE
                totalGoals > 0 && completedGoals == 0 -> DayCategory.UNPRODUCTIVE
                else -> DayCategory.NEUTRAL
            }
            
            data.add(HeatmapDay(date, category, completedGoals + completedHabits))
        }
        data
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 5. Offline Reminder Toggles
    private val _remindersEnabled = MutableStateFlow(true)
    val remindersEnabled: StateFlow<Boolean> = _remindersEnabled.asStateFlow()

    private val _isWritingMode = MutableStateFlow(false)
    val isWritingMode: StateFlow<Boolean> = _isWritingMode.asStateFlow()

    private val _pendingDiarySave = MutableStateFlow<String?>(null)

    init {
        ReminderHelper.createNotificationChannel(application)
        if (_remindersEnabled.value) {
            ReminderHelper.scheduleDailyReminders(application)
        }

        @OptIn(kotlinx.coroutines.FlowPreview::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
        viewModelScope.launch {
            _pendingDiarySave
                .debounce(800)
                .filterNotNull()
                .collectLatest { content ->
                    if (_selectedDate.value == LocalDate.now()) {
                        val dateStr = _selectedDate.value.format(dateFormatter)
                        val entry = DiaryEntry(
                            date = dateStr,
                            content = content,
                            mood = "Neutral",
                            lastUpdated = System.currentTimeMillis()
                        )
                        repository.insertDiaryEntry(entry)
                    }
                }
        }
    }

    private fun calculateStreak(goals: List<Goal>, completions: List<HabitCompletion>): Int {
        val completedDates = (goals.filter { it.isCompleted }.map { it.date } + completions.map { it.date }).toSet()
        if (completedDates.isEmpty()) return 0

        var streak = 0
        var checkDate = LocalDate.now()
        
        val todayStr = checkDate.format(dateFormatter)
        val yesterdayStr = checkDate.minusDays(1).format(dateFormatter)
        
        if (completedDates.contains(todayStr)) {
            while (completedDates.contains(checkDate.format(dateFormatter))) {
                streak++
                checkDate = checkDate.minusDays(1)
            }
        } else if (completedDates.contains(yesterdayStr)) {
            checkDate = checkDate.minusDays(1)
            while (completedDates.contains(checkDate.format(dateFormatter))) {
                streak++
                checkDate = checkDate.minusDays(1)
            }
        }
        return streak
    }

    fun toggleReminders(enabled: Boolean) {
        viewModelScope.launch {
            _remindersEnabled.value = enabled
            if (enabled) {
                ReminderHelper.scheduleDailyReminders(getApplication())
            } else {
                ReminderHelper.cancelAllReminders(getApplication())
            }
        }
    }

    fun triggerTestNotification(title: String, message: String) {
        ReminderHelper.showNotification(
            getApplication(),
            id = (System.currentTimeMillis() % 100000).toInt(),
            title = title,
            message = message
        )
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    // Diary Entries
    fun saveDiaryEntry(content: String, mood: String) {
        if (_selectedDate.value == LocalDate.now()) {
            _pendingDiarySave.value = content
        }
    }

    fun saveDiaryEntryImmediately(content: String) {
        if (_selectedDate.value != LocalDate.now()) {
            _pendingDiarySave.value = null // Cancel any pending debounced save
            return
        }
        viewModelScope.launch {
            val dateStr = _selectedDate.value.format(dateFormatter)
            val entry = DiaryEntry(
                date = dateStr,
                content = content,
                mood = "Neutral",
                lastUpdated = System.currentTimeMillis()
            )
            repository.insertDiaryEntry(entry)
            _pendingDiarySave.value = null // Cancel any pending debounced save
        }
    }

    fun setWritingMode(enabled: Boolean) {
        _isWritingMode.value = enabled
    }

    fun deleteDiaryEntry(entry: DiaryEntry) {
        viewModelScope.launch {
            repository.deleteDiaryEntry(entry)
        }
    }

    // Goals Checklist
    fun addGoal(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val dateStr = _selectedDate.value.format(dateFormatter)
            val goal = Goal(
                title = title,
                date = dateStr,
                isCompleted = false
            )
            repository.insertGoal(goal)
        }
    }

    fun toggleGoalCompletion(goal: Goal) {
        viewModelScope.launch {
            val updated = goal.copy(isCompleted = !goal.isCompleted)
            repository.updateGoal(updated)
        }
    }

    fun deleteGoal(goalId: Int) {
        viewModelScope.launch {
            repository.deleteGoalById(goalId)
        }
    }

    // Habits Operations
    fun addHabit(name: String, description: String, category: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val habit = Habit(
                name = name,
                description = description,
                category = category
            )
            repository.insertHabit(habit)
        }
    }

    fun deleteHabit(habitId: Int) {
        viewModelScope.launch {
            repository.deleteHabitById(habitId)
        }
    }

    fun toggleHabit(habitId: Int) {
        viewModelScope.launch {
            val dateStr = _selectedDate.value.format(dateFormatter)
            repository.toggleHabitCompletion(habitId, dateStr)
        }
    }
}
