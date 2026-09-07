package com.example.myjobseeker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myjobseeker.model.Job
import com.example.myjobseeker.ui.detail.DetailScreen
import com.example.myjobseeker.ui.home.HomeScreen
import com.example.myjobseeker.ui.profile.ProfileScreen
import com.example.myjobseeker.ui.theme.MyJobSeekerTheme
import com.example.myjobseeker.ui.theme.NavNavyHeader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyJobSeekerTheme {
                val navController = rememberNavController()
                var selectedJob by remember { mutableStateOf<Job?>(null) }
                
                Scaffold(
                    bottomBar = { 
                        if (selectedJob == null) {
                            BottomNavigationBar(navController) 
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(
                                onJobClick = { job ->
                                    selectedJob = job
                                    navController.navigate("detail")
                                },
                                onProfileClick = {
                                    navController.navigate("profile") {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                        composable("search") { Text("Search Screen") }
                        composable("applications") { Text("Applications Screen") }
                        composable("profile") { 
                            ProfileScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("detail") {
                            selectedJob?.let { job ->
                                DetailScreen(
                                    job = job,
                                    onBackClick = {
                                        selectedJob = null
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        NavigationItem("home", "Beranda", R.drawable.ic_home),
        NavigationItem("search", "Cari", R.drawable.ic_search),
        NavigationItem("applications", "Lamaran", R.drawable.ic_message),
        NavigationItem("profile", "Profil", R.drawable.ic_person)
    )
    
    NavigationBar(
        containerColor = NavNavyHeader,
        contentColor = Color.White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.White.copy(alpha = 0.6f),
                    unselectedTextColor = Color.White.copy(alpha = 0.6f),
                    indicatorColor = Color(0xFF5D8BF4)
                )
            )
        }
    }
}

data class NavigationItem(val route: String, val title: String, val icon: Int)
