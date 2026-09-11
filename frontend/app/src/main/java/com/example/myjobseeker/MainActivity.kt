package com.example.myjobseeker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.myjobseeker.model.ApplicationStatus
import com.example.myjobseeker.model.Job
import com.example.myjobseeker.ui.SplashScreen
import com.example.myjobseeker.ui.applications.ApplicationsScreen
import com.example.myjobseeker.ui.bookmark.BookmarkScreen
import com.example.myjobseeker.ui.components.NavDrawerContent
import com.example.myjobseeker.ui.detail.DetailScreen
import com.example.myjobseeker.ui.home.HomeScreen
//import com.example.myjobseeker.ui.home.getDummyJobs
import com.example.myjobseeker.ui.job.AddJobScreen
import com.example.myjobseeker.ui.profile.ProfileScreen
import com.example.myjobseeker.ui.profile.MyProfileScreen
import com.example.myjobseeker.ui.profile.SkillScreen
import com.example.myjobseeker.ui.search.SearchScreen
import com.example.myjobseeker.ui.theme.BackgroundLightBlue
import com.example.myjobseeker.ui.theme.BlueNormal
import com.example.myjobseeker.ui.theme.LogoutRed
import com.example.myjobseeker.ui.theme.MyJobSeekerTheme
import com.example.myjobseeker.ui.theme.NavNavyHeader
import com.example.myjobseeker.ui.theme.TextDark
import com.example.myjobseeker.ui.theme.TextGray
import com.example.myjobseeker.ui.auth.LoginScreen
import com.example.myjobseeker.ui.auth.RegisterScreen
import com.example.myjobseeker.viewmodel.AuthViewModel
import com.example.myjobseeker.viewmodel.JobViewModel
import com.example.myjobseeker.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.myjobseeker.ui.notifications.NotificationsScreen
import com.example.myjobseeker.ui.applications.RiwayatPekerjaanScreen
import com.example.myjobseeker.viewmodel.LocationViewModel
import com.example.myjobseeker.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val authViewModel: AuthViewModel = viewModel()
            val jobViewModel: JobViewModel = viewModel()
            val locationViewModel: LocationViewModel = viewModel()
            val searchViewModel: SearchViewModel = viewModel()

            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            MyJobSeekerTheme(darkTheme = isDarkTheme) {
                val context = LocalContext.current
                val navController = rememberNavController()

                val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
                val currentUser by authViewModel.currentUser.collectAsState(initial = null)
                val currentUserId by authViewModel.userId.collectAsState()

                LaunchedEffect(currentUserId) {
                    jobViewModel.setCurrentUser(currentUserId)
                    themeViewModel.setCurrentUser(currentUserId)
                    searchViewModel.setCurrentUser(currentUserId)
                }
                
                LaunchedEffect(isLoggedIn) {
                    if (!isLoggedIn && navController.currentDestination?.route != "splash") {
                        navController.navigate("login") {
                            popUpTo(0)
                        }
                    }
                }

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
                val bookmarkedJobIds by jobViewModel.bookmarkedJobIds.collectAsState()
                var selectedJob by remember { mutableStateOf<Job?>(null) }
                var selectedApplication by remember { mutableStateOf<Application?>(null) }

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val isAuthRoute = currentRoute == "login" || currentRoute == "register" || currentRoute == "splash"
                val gesturesEnabled = !isAuthRoute && isLoggedIn

                val mainRoutes = listOf("home", "search", "add_job", "applications", "profile")
                val showBottomBar = isLoggedIn && currentRoute in mainRoutes

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = gesturesEnabled,
                    drawerContent = {
                        NavDrawerContent(
                            isDark = isDarkTheme,
                            username = currentUser?.username,
                            email = currentUser?.email,
                            onBookmarkClick = {
                                navController.navigate("bookmarks") {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                scope.launch { drawerState.close() }
                            },
                            onRiwayatClick = {
                                navController.navigate("riwayat_pekerjaan") {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                scope.launch { drawerState.close() }
                            },
                            onModeClick = {
                                themeViewModel.toggleTheme()
                                scope.launch { drawerState.close() }
                            },
                            onSettingsClick = {
                                // Add logic for settings navigation
                                scope.launch { drawerState.close() }
                            },
                            onLogoutClick = {
                                authViewModel.logout()
                                scope.launch { drawerState.close() }
                            }
                        )
                    }
                ) {
                    Scaffold(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        bottomBar = {
                            if (showBottomBar) {
                                BottomNavigationBar(navController)
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "splash",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("splash") {
                                SplashScreen(
                                    onTimeout = {
                                        val destination = if (isLoggedIn) "home" else "login"
                                        navController.navigate(destination) {
                                            popUpTo("splash") { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable("login") {
                                LoginScreen(
                                    viewModel = authViewModel,
                                    onRegisterClick = { navController.navigate("register") },
                                    onLoginSuccess = {
                                        navController.navigate("home") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable("register") {
                                RegisterScreen(
                                    viewModel = authViewModel,
                                    onLoginClick = { navController.popBackStack() }
                                )
                            }
                            composable("home") {
                                HomeScreen(
                                    jobViewModel = jobViewModel,
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
                                    onNotificationClick = {
                                        navController.navigate("notifications")
                                    },
                                    onMenuClick = {
                                        scope.launch { drawerState.open() }
                                    },
                                    location = currentAddress
                                )
                            }
                            composable("search") {
                                SearchScreen(
                                    viewModel = searchViewModel,
                                    jobViewModel = jobViewModel,
                                    onJobClick = { job ->
                                        selectedJob = job
                                        navController.navigate("detail")
                                    },
                                    onProfileClick = {
                                        navController.navigate("profile")
                                    },
                                    onNotificationClick = {
                                        navController.navigate("notifications")
                                    },
                                    onMenuClick = {
                                        scope.launch { drawerState.open() }
                                    },
                                    location = currentAddress
                                )
                            }
                            composable("bookmarks") {
                                BookmarkScreen(
                                    viewModel = jobViewModel,
                                    onJobClick = { job ->
                                        selectedJob = job
                                        navController.navigate("detail")
                                    },
                                    onBackClick = {
                                        navController.popBackStack()
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
                                    onNotificationClick = {
                                        navController.navigate("notifications")
                                    },
                                    onDetailClick = { application ->
                                        selectedApplication = application
                                        navController.navigate("application_detail")
                                    },
                                    onMenuClick = {
                                        scope.launch { drawerState.open() }
                                    },
                                    location = currentAddress
                                )
                            }
                            composable("profile") {
                                ProfileScreen(
                                    jobViewModel = jobViewModel,
                                    onBackClick = {
                                        navController.popBackStack()
                                    },
                                    onNotificationClick = {
                                        navController.navigate("notifications")
                                    },
                                    onMyProfileClick = {
                                        navController.navigate("my_profile")
                                    },
                                    onSkillClick = {
                                        navController.navigate("skills")
                                    },
                                    location = currentAddress,
                                    username = currentUser?.username,
                                    email = currentUser?.email,
                                    onLogoutClick = {
                                        authViewModel.logout()
                                    }
                                )
                            }
                            composable("my_profile") {
                                MyProfileScreen(
                                    authViewModel = authViewModel,
                                    onBackClick = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                            composable("skills") {
                                SkillScreen(
                                    authViewModel = authViewModel,
                                    onBackClick = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                            composable("notifications") {
                                NotificationsScreen(
                                    viewModel = jobViewModel,
                                    onBackClick = {
                                        navController.popBackStack()
                                    },
                                    onMenuClick = {
                                        scope.launch { drawerState.open() }
                                    },
                                    location = currentAddress
                                )
                            }
                            composable("add_job") {
                                AddJobScreen(
                                    jobViewModel = jobViewModel,
                                    onBackClick = {
                                        navController.popBackStack()
                                    },
                                    onMenuClick = {
                                        scope.launch { drawerState.open() }
                                    },
                                    onProfileClick = {
                                        navController.navigate("profile")
                                    },
                                    location = currentAddress
                                )
                            }
                            composable("riwayat_pekerjaan") {
                                RiwayatPekerjaanScreen(
                                    viewModel = jobViewModel,
                                    onBackClick = {
                                        navController.popBackStack()
                                    },
                                    onDetailClick = { application ->
                                        selectedApplication = application
                                        navController.navigate("application_detail")
                                    },
                                    location = currentAddress
                                )
                            }
                        composable("detail") {
                            selectedJob?.let { job ->
                                val isCreator = job.creatorId == currentUserId
                                DetailScreen(
                                    job = job,
                                    onBackClick = {
                                        selectedJob = null
                                        navController.popBackStack()
                                    },
                                    onApplyClick = {
                                        if (isCreator) {
                                            jobViewModel.deleteJob(job.id)
                                            navController.popBackStack()
                                        } else {
                                            jobViewModel.applyForJob(job)
                                            navController.navigate("applications") {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                        selectedJob = null
                                    },
                                    onMenuClick = {
                                        scope.launch { drawerState.open() }
                                    },
                                    onProfileClick = {
                                        navController.navigate("profile")
                                    },
                                    isBookmarked = bookmarkedJobIds.contains(job.id),
                                    onBookmarkClick = { jobViewModel.toggleBookmark(job) },
                                    location = currentAddress,
                                    buttonText = if (isCreator) "Hapus Pekerjaan" else stringResource(id = R.string.apply_job),
                                    buttonColor = if (isCreator) LogoutRed else BlueNormal
                                )
                            }
                        }
                        composable("application_detail") {
                            selectedApplication?.let { app ->
                                jobViewModel.jobs.value.find { it.id == app.jobId }?.let { job ->
                                    val isRejected = app.status == ApplicationStatus.DITOLAK
                                    val isAccepted = app.status == ApplicationStatus.DITERIMA
                                    DetailScreen(
                                        job = job,
                                        onBackClick = {
                                            selectedApplication = null
                                            navController.popBackStack()
                                        },
                                        onApplyClick = {
                                            if (isRejected) {
                                                jobViewModel.deleteApplication(app.id)
                                            } else {
                                                jobViewModel.cancelApplication(app.id)
                                            }
                                            navController.popBackStack()
                                            selectedApplication = null
                                        },
                                        onMenuClick = {
                                            scope.launch { drawerState.open() }
                                        },
                                        onProfileClick = {
                                            navController.navigate("profile")
                                        },
                                        buttonText = if (isRejected) "Hapus" else stringResource(id = R.string.cancel_application),
                                        buttonColor = LogoutRed,
                                        location = currentAddress,
                                        isAccepted = isAccepted
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
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        NavigationItem("home", stringResource(R.string.nav_home), R.drawable.ic_home),
        NavigationItem("search", stringResource(R.string.nav_search), R.drawable.ic_search),
        NavigationItem("add_job", stringResource(R.string.nav_add_job), R.drawable.ic_edit),
        NavigationItem("applications", stringResource(R.string.nav_applications), R.drawable.ic_work),
        NavigationItem("profile", stringResource(R.string.nav_profile), R.drawable.ic_person)
    )
    
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
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
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

data class NavigationItem(val route: String, val title: String, val icon: Int)
