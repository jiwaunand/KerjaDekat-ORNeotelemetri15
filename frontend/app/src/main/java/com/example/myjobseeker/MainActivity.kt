package com.example.myjobseeker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myjobseeker.model.Application
import com.example.myjobseeker.model.Job
import com.example.myjobseeker.ui.applications.ApplicationsScreen
import com.example.myjobseeker.ui.detail.DetailScreen
import com.example.myjobseeker.ui.home.HomeScreen
import com.example.myjobseeker.ui.profile.ProfileScreen
import com.example.myjobseeker.ui.search.SearchScreen
import com.example.myjobseeker.ui.theme.BackgroundLightBlue
import com.example.myjobseeker.ui.theme.LogoutRed
import com.example.myjobseeker.ui.theme.MyJobSeekerTheme
import com.example.myjobseeker.ui.theme.NavNavyHeader
import com.example.myjobseeker.viewmodel.JobViewModel
import com.example.myjobseeker.viewmodel.SearchViewModel

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.myjobseeker.viewmodel.LocationViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyJobSeekerTheme {
                val context = LocalContext.current
                val navController = rememberNavController()
                val jobViewModel: JobViewModel = viewModel()
                val locationViewModel: LocationViewModel = viewModel()
                val searchViewModel: SearchViewModel = viewModel()
                
                val locationPermissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                        locationViewModel.fetchLocation(context)
                    }
                }

                LaunchedEffect(Unit) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationViewModel.fetchLocation(context)
                    } else {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }

                val currentAddress by locationViewModel.currentLocation
                var selectedJob by remember { mutableStateOf<Job?>(null) }
                var selectedApplication by remember { mutableStateOf<Application?>(null) }
                
                Scaffold(
                    containerColor = BackgroundLightBlue,
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    bottomBar = { 
                        if (selectedJob == null && selectedApplication == null) {
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
                                },
                                onApplyClick = { job ->
                                    jobViewModel.applyForJob(job)
                                    navController.navigate("applications") {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                onSearchClick = { query ->
                                    searchViewModel.onSearchQueryChange(query)
                                    navController.navigate("search") {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                location = currentAddress
                            )
                        }
                        composable("search") { 
                            SearchScreen(
                                viewModel = searchViewModel,
                                onJobClick = { job ->
                                    selectedJob = job
                                    navController.navigate("detail")
                                },
                                onProfileClick = {
                                    navController.navigate("profile")
                                },
                                onApplyClick = { job ->
                                    jobViewModel.applyForJob(job)
                                    navController.navigate("applications")
                                },
                                location = currentAddress
                            )
                        }
                        composable("applications") { 
                            ApplicationsScreen(
                                viewModel = jobViewModel,
                                onProfileClick = {
                                    navController.navigate("profile")
                                },
                                onDetailClick = { application ->
                                    selectedApplication = application
                                    navController.navigate("application_detail")
                                },
                                location = currentAddress
                            )
                        }
                        composable("profile") { 
                            ProfileScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                location = currentAddress
                            )
                        }
                        composable("detail") {
                            selectedJob?.let { job ->
                                DetailScreen(
                                    job = job,
                                    onBackClick = {
                                        selectedJob = null
                                        navController.popBackStack()
                                    },
                                    onApplyClick = {
                                        jobViewModel.applyForJob(job)
                                        navController.navigate("applications") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                        selectedJob = null
                                    },
                                    location = currentAddress
                                )
                            }
                        }
                        composable("application_detail") {
                            selectedApplication?.let { app ->
                                DetailScreen(
                                    job = app.job,
                                    onBackClick = {
                                        selectedApplication = null
                                        navController.popBackStack()
                                    },
                                    onApplyClick = {
                                        jobViewModel.cancelApplication(app.id)
                                        navController.popBackStack()
                                        selectedApplication = null
                                    },
                                    buttonText = stringResource(id = R.string.cancel_application),
                                    buttonColor = LogoutRed,
                                    location = currentAddress
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
        NavigationItem("applications", "Lamaran", R.drawable.ic_work),
        NavigationItem("profile", "Profil", R.drawable.ic_person)
    )
    
    NavigationBar(
        containerColor = NavNavyHeader,
        contentColor = Color.White,
        tonalElevation = 0.dp
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
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF5D8BF4),
                    selectedTextColor = Color(0xFF5D8BF4),
                    unselectedIconColor = Color.White,
                    unselectedTextColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

data class NavigationItem(val route: String, val title: String, val icon: Int)
