package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainAppScreen()
      }
    }
  }
}

@Composable
fun MainAppScreen() {
  val navController = rememberNavController()
  val viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
  
  var selectedItem by remember { mutableStateOf("today") }

  Scaffold(
    bottomBar = {
      NavigationBar(
        modifier = Modifier
          .testTag("app_navigation_bar")
          .navigationBarsPadding(),
        containerColor = Color(0xFF211F26),
        tonalElevation = 8.dp
      ) {
        NavigationBarItem(
          icon = { Icon(Icons.Filled.SpaceDashboard, contentDescription = "Today") },
          label = { Text("Today") },
          selected = selectedItem == "today",
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color(0xFFD0BCFF),
            selectedTextColor = Color(0xFFD0BCFF),
            unselectedIconColor = Color(0xFFCAC4D0),
            unselectedTextColor = Color(0xFFCAC4D0),
            indicatorColor = Color(0xFF4A4458)
          ),
          onClick = {
            selectedItem = "today"
            navController.navigate("today") {
              popUpTo("today") { saveState = true }
              launchSingleTop = true
              restoreState = true
            }
          },
          modifier = Modifier.testTag("nav_tab_today")
        )
        NavigationBarItem(
          icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Diary") },
          label = { Text("Diary") },
          selected = selectedItem == "journal",
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color(0xFFD0BCFF),
            selectedTextColor = Color(0xFFD0BCFF),
            unselectedIconColor = Color(0xFFCAC4D0),
            unselectedTextColor = Color(0xFFCAC4D0),
            indicatorColor = Color(0xFF4A4458)
          ),
          onClick = {
            selectedItem = "journal"
            navController.navigate("journal") {
              popUpTo("today") { saveState = true }
              launchSingleTop = true
              restoreState = true
            }
          },
          modifier = Modifier.testTag("nav_tab_journal")
        )
        NavigationBarItem(
          icon = { Icon(Icons.Filled.AutoAwesome, contentDescription = "Habit Hub") },
          label = { Text("Habit Hub") },
          selected = selectedItem == "habits",
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color(0xFFD0BCFF),
            selectedTextColor = Color(0xFFD0BCFF),
            unselectedIconColor = Color(0xFFCAC4D0),
            unselectedTextColor = Color(0xFFCAC4D0),
            indicatorColor = Color(0xFF4A4458)
          ),
          onClick = {
            selectedItem = "habits"
            navController.navigate("habits") {
              popUpTo("today") { saveState = true }
              launchSingleTop = true
              restoreState = true
            }
          },
          modifier = Modifier.testTag("nav_tab_habits")
        )
        NavigationBarItem(
          icon = { Icon(Icons.Filled.Analytics, contentDescription = "Flow Map") },
          label = { Text("Flow Map") },
          selected = selectedItem == "flowmap",
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color(0xFFD0BCFF),
            selectedTextColor = Color(0xFFD0BCFF),
            unselectedIconColor = Color(0xFFCAC4D0),
            unselectedTextColor = Color(0xFFCAC4D0),
            indicatorColor = Color(0xFF4A4458)
          ),
          onClick = {
            selectedItem = "flowmap"
            navController.navigate("flowmap") {
              popUpTo("today") { saveState = true }
              launchSingleTop = true
              restoreState = true
            }
          },
          modifier = Modifier.testTag("nav_tab_flowmap")
        )
      }
    },
    contentWindowInsets = WindowInsets.safeDrawing
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = "today",
      modifier = Modifier.padding(innerPadding)
    ) {
      composable("today") {
        TodayScreen(
          viewModel = viewModel,
          onNavigateToJournal = {
            selectedItem = "journal"
            navController.navigate("journal") {
              popUpTo("today") { saveState = true }
              launchSingleTop = true
              restoreState = true
            }
          }
        )
      }
      composable("journal") {
        JournalScreen(viewModel = viewModel)
      }
      composable("habits") {
        HabitHubScreen(viewModel = viewModel)
      }
      composable("flowmap") {
        FlowMapScreen(viewModel = viewModel)
      }
    }
  }
}
