package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entries")
data class DiaryEntry(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val content: String,
    val mood: String = "Neutral", // Peaceful, Productive, Happy, Energetic, Neutral, Reflective, Tired, Sad
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val date: String, // YYYY-MM-DD
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String = "",
    val category: String = "General", // Health, Mind, Fitness, Work, Learning, General
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "habit_completions", primaryKeys = ["habitId", "date"])
data class HabitCompletion(
    val habitId: Int,
    val date: String // YYYY-MM-DD
)
