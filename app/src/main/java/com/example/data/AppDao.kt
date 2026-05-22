package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Diary Entries
    @Query("SELECT * FROM diary_entries ORDER BY date DESC")
    fun getAllDiaryEntries(): Flow<List<DiaryEntry>>

    @Query("SELECT * FROM diary_entries WHERE date = :date LIMIT 1")
    suspend fun getDiaryEntryForDate(date: String): DiaryEntry?

    @Query("SELECT * FROM diary_entries WHERE date = :date")
    fun getDiaryEntryFlowForDate(date: String): Flow<DiaryEntry?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiaryEntry(entry: DiaryEntry)

    @Delete
    suspend fun deleteDiaryEntry(entry: DiaryEntry)

    // Goals
    @Query("SELECT * FROM goals ORDER BY date DESC, createdAt ASC")
    fun getAllGoals(): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE date = :date ORDER BY createdAt ASC")
    fun getGoalsForDate(date: String): Flow<List<Goal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal)

    @Update
    suspend fun updateGoal(goal: Goal)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteGoalById(id: Int)

    // Habits
    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun getAllHabits(): Flow<List<Habit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteHabitById(id: Int)

    // Habit Completions
    @Query("SELECT * FROM habit_completions")
    fun getAllHabitCompletions(): Flow<List<HabitCompletion>>

    @Query("SELECT * FROM habit_completions WHERE date = :date")
    fun getHabitCompletionsForDate(date: String): Flow<List<HabitCompletion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitCompletion(completion: HabitCompletion)

    @Delete
    suspend fun deleteHabitCompletion(completion: HabitCompletion)

    @Query("SELECT EXISTS(SELECT * FROM habit_completions WHERE habitId = :habitId AND date = :date)")
    suspend fun isHabitCompleted(habitId: Int, date: String): Boolean
}
