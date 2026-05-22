package com.example.data

import kotlinx.coroutines.flow.Flow

class MainRepository(private val appDao: AppDao) {

    // Diary Operations
    val allDiaries: Flow<List<DiaryEntry>> = appDao.getAllDiaryEntries()

    fun getDiaryEntryFlowForDate(date: String): Flow<DiaryEntry?> {
        return appDao.getDiaryEntryFlowForDate(date)
    }

    suspend fun insertDiaryEntry(entry: DiaryEntry) {
        appDao.insertDiaryEntry(entry)
    }

    suspend fun deleteDiaryEntry(entry: DiaryEntry) {
        appDao.deleteDiaryEntry(entry)
    }

    // Goal Operations
    val allGoals: Flow<List<Goal>> = appDao.getAllGoals()

    fun getGoalsForDate(date: String): Flow<List<Goal>> {
        return appDao.getGoalsForDate(date)
    }

    suspend fun insertGoal(goal: Goal) {
        appDao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: Goal) {
        appDao.updateGoal(goal)
    }

    suspend fun deleteGoalById(id: Int) {
        appDao.deleteGoalById(id)
    }

    // Habit Operations
    val allHabits: Flow<List<Habit>> = appDao.getAllHabits()

    suspend fun insertHabit(habit: Habit) {
        appDao.insertHabit(habit)
    }

    suspend fun deleteHabitById(id: Int) {
        appDao.deleteHabitById(id)
    }

    // Habit Completion Operations
    val allHabitCompletions: Flow<List<HabitCompletion>> = appDao.getAllHabitCompletions()

    fun getHabitCompletionsForDate(date: String): Flow<List<HabitCompletion>> {
        return appDao.getHabitCompletionsForDate(date)
    }

    suspend fun toggleHabitCompletion(habitId: Int, date: String) {
        if (appDao.isHabitCompleted(habitId, date)) {
            appDao.deleteHabitCompletion(HabitCompletion(habitId, date))
        } else {
            appDao.insertHabitCompletion(HabitCompletion(habitId, date))
        }
    }
}
