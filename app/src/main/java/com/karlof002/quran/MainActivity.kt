package com.karlof002.quran

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.karlof002.quran.ui.screens.*
import com.karlof002.quran.ui.theme.QuranTheme
import com.karlof002.quran.ui.utils.*
import com.karlof002.quran.ui.components.*
import com.karlof002.quran.ui.viewmodel.RatingViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Setup window for fullscreen support
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val settingsViewModel: com.karlof002.quran.ui.viewmodel.SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            val isDarkMode by settingsViewModel.isDarkMode.observeAsState(false)

            // Update system bars color based on theme
            LaunchedEffect(isDarkMode) {
                val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
                windowInsetsController.isAppearanceLightStatusBars = !isDarkMode
                windowInsetsController.isAppearanceLightNavigationBars = !isDarkMode
            }

            QuranTheme(darkTheme = isDarkMode) {
                QuranApp(
                    settingsViewModel = settingsViewModel,
                    onEnterFullscreen = { enterFullscreen() },
                    onExitFullscreen = { exitFullscreen() }
                )
            }
        }
    }

    private fun enterFullscreen() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun exitFullscreen() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranApp(
    settingsViewModel: com.karlof002.quran.ui.viewmodel.SettingsViewModel,
    onEnterFullscreen: () -> Unit = {},
    onExitFullscreen: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentDestination = navBackStackEntry?.destination

    // Rating system
    val ratingViewModel: RatingViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val showRatingDialog by ratingViewModel.showRatingDialog.observeAsState(false)

    // Determine window size and navigation type
    val windowSize = rememberWindowSizeClass()
    val navigationType = getNavigationType(windowSize)

    // Hide navigation on reader screen
    val showNavigation = currentRoute?.startsWith("reader")?.not() ?: true

    // Handle fullscreen mode based on current route
    LaunchedEffect(currentRoute) {
        if (currentRoute?.startsWith("reader") == true) {
            onEnterFullscreen()
        } else {
            onExitFullscreen()
        }
    }

    val navigateToRoute: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    // Show rating dialog if needed
    if (showRatingDialog) {
        RatingDialog(
            onRate = { ratingViewModel.onUserRated() },
            onDismiss = { ratingViewModel.dismissDialog() },
            onNotNow = { ratingViewModel.onUserDeclined() }
        )
    }

    // Adaptive layout based on navigation type
    when (navigationType) {
        NavigationType.BOTTOM_NAVIGATION -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    if (showNavigation) {
                        AdaptiveNavigationBar(
                            currentDestination = currentDestination,
                            onNavigate = navigateToRoute
                        )
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = "home",
                    modifier = if (showNavigation) Modifier.padding(innerPadding) else Modifier.fillMaxSize()
                ) {
                    composable("home") {
                        HomeScreen(
                            onSurahClick = { surahId, startPage, surahName, verses, juzNumber ->
                                navController.navigate("reader/$startPage/604/$surahName/$verses/$juzNumber")
                            },
                            onJuzClick = { juzId, startPage, endPage ->
                                navController.navigate("reader/$startPage/$endPage/Juz $juzId/0/$juzId")
                            },
                            windowSize = windowSize
                        )
                    }
                    composable("search") {
                        SearchScreen(
                            onSurahClick = { surahId, startPage, surahName, verses, juzNumber ->
                                navController.navigate("reader/$startPage/604/$surahName/$verses/$juzNumber")
                            },
                            windowSize = windowSize
                        )
                    }
                    composable("bookmarks") {
                        BookmarksScreen(
                            onBookmarkClick = { pageNumber, surahName, verses, juzNumber ->
                                navController.navigate("reader/$pageNumber/604/$surahName/$verses/$juzNumber")
                            },
                            windowSize = windowSize
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            viewModel = settingsViewModel,
                            windowSize = windowSize,
                            onNavigateToDonation = {
                                navController.navigate("donation")
                            },
                            onNavigateToAbout = {
                                navController.navigate("about")
                            }
                        )
                    }

                    // About screen
                    composable("about") {
                        AboutScreen(
                            onBackClick = { navController.navigateUp() }
                        )
                    }

                    // Donation screen
                    composable("donation") {
                        DonationScreen(
                            onBackClick = { navController.navigateUp() }
                        )
                    }

                    // Reader screen with arguments
                    composable(
                        route = "reader/{startPage}/{endPage}/{title}/{verses}/{juzNumber}",
                        arguments = listOf(
                            navArgument("startPage") { type = NavType.IntType },
                            navArgument("endPage") { type = NavType.IntType },
                            navArgument("title") { type = NavType.StringType },
                            navArgument("verses") { type = NavType.IntType; defaultValue = 0 },
                            navArgument("juzNumber") { type = NavType.IntType; defaultValue = 0 }
                        )
                    ) { backStackEntry ->
                        val startPage = backStackEntry.arguments?.getInt("startPage") ?: 1
                        val endPage = backStackEntry.arguments?.getInt("endPage") ?: 1
                        val title = backStackEntry.arguments?.getString("title") ?: "Quran"
                        val verses = backStackEntry.arguments?.getInt("verses") ?: 0
                        val juzNumber = backStackEntry.arguments?.getInt("juzNumber") ?: 0

                        QuranReaderScreen(
                            startPage = startPage,
                            endPage = endPage,
                            title = title,
                            verses = verses,
                            juzNumber = juzNumber,
                            onBackClick = { navController.navigateUp() },
                            windowSize = windowSize
                        )
                    }
                }
            }
        }
        NavigationType.NAVIGATION_RAIL -> {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                // Navigation Rail - always visible unless on reader screen
                if (showNavigation) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        header = {
                            // Book icon at the top
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = "Quran Al-Kareem",
                                modifier = Modifier
                                    .padding(vertical = 20.dp)
                                    .size(28.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    ) {
                        Spacer(modifier = Modifier.height(100.dp))

                        navigationItems.forEachIndexed { index, item ->
                            NavigationRailItem(
                                icon = {
                                    Icon(
                                        item.icon,
                                        contentDescription = item.title,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = { Text(item.title) },
                                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                                onClick = { navigateToRoute(item.route) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            )

                            if (index < navigationItems.size - 1) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }

                // Content area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("home") {
                            HomeScreen(
                                onSurahClick = { surahId, startPage, surahName, verses, juzNumber ->
                                    navController.navigate("reader/$startPage/604/$surahName/$verses/$juzNumber")
                                },
                                onJuzClick = { juzId, startPage, endPage ->
                                    navController.navigate("reader/$startPage/$endPage/Juz $juzId/0/$juzId")
                                },
                                windowSize = windowSize
                            )
                        }
                        composable("search") {
                            SearchScreen(
                                onSurahClick = { surahId, startPage, surahName, verses, juzNumber ->
                                    navController.navigate("reader/$startPage/604/$surahName/$verses/$juzNumber")
                                },
                                windowSize = windowSize
                            )
                        }
                        composable("bookmarks") {
                            BookmarksScreen(
                                onBookmarkClick = { pageNumber, surahName, verses, juzNumber ->
                                    navController.navigate("reader/$pageNumber/604/$surahName/$verses/$juzNumber")
                                },
                                windowSize = windowSize
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                viewModel = settingsViewModel,
                                windowSize = windowSize,
                                onNavigateToDonation = {
                                    navController.navigate("donation")
                                },
                                onNavigateToAbout = {
                                    navController.navigate("about")
                                }
                            )
                        }

                        // About screen
                        composable("about") {
                            AboutScreen(
                                onBackClick = { navController.navigateUp() }
                            )
                        }

                        // Donation screen
                        composable("donation") {
                            DonationScreen(
                                onBackClick = { navController.navigateUp() }
                            )
                        }

                        // Reader screen with arguments
                        composable(
                            route = "reader/{startPage}/{endPage}/{title}/{verses}/{juzNumber}",
                            arguments = listOf(
                                navArgument("startPage") { type = NavType.IntType },
                                navArgument("endPage") { type = NavType.IntType },
                                navArgument("title") { type = NavType.StringType },
                                navArgument("verses") { type = NavType.IntType; defaultValue = 0 },
                                navArgument("juzNumber") { type = NavType.IntType; defaultValue = 0 }
                            )
                        ) { backStackEntry ->
                            val startPage = backStackEntry.arguments?.getInt("startPage") ?: 1
                            val endPage = backStackEntry.arguments?.getInt("endPage") ?: 1
                            val title = backStackEntry.arguments?.getString("title") ?: "Quran"
                            val verses = backStackEntry.arguments?.getInt("verses") ?: 0
                            val juzNumber = backStackEntry.arguments?.getInt("juzNumber") ?: 0

                            QuranReaderScreen(
                                startPage = startPage,
                                endPage = endPage,
                                title = title,
                                verses = verses,
                                juzNumber = juzNumber,
                                onBackClick = { navController.navigateUp() },
                                windowSize = windowSize
                            )
                        }
                    }
                }
            }
        }
        NavigationType.PERMANENT_NAVIGATION_DRAWER -> {
            // For large screens with permanent navigation drawer
            PermanentNavigationDrawer(
                drawerContent = {
                    PermanentDrawerSheet(
                        modifier = Modifier.width(240.dp),
                        drawerContainerColor = MaterialTheme.colorScheme.surface
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))

                        // App title/logo
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = "Quran Al-Kareem",
                            modifier = Modifier
                                .padding(16.dp)
                                .size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        navigationItems.forEach { item ->
                            NavigationDrawerItem(
                                icon = {
                                    Icon(
                                        item.icon,
                                        contentDescription = item.title,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = { Text(item.title) },
                                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                                onClick = { navigateToRoute(item.route) },
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            ) {
                // Content area
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("home") {
                            HomeScreen(
                                onSurahClick = { surahId, startPage, surahName, verses, juzNumber ->
                                    navController.navigate("reader/$startPage/604/$surahName/$verses/$juzNumber")
                                },
                                onJuzClick = { juzId, startPage, endPage ->
                                    navController.navigate("reader/$startPage/$endPage/Juz $juzId/0/$juzId")
                                }
                            )
                        }
                        composable("search") {
                            SearchScreen(
                                onSurahClick = { surahId, startPage, surahName, verses, juzNumber ->
                                    navController.navigate("reader/$startPage/604/$surahName/$verses/$juzNumber")
                                }
                            )
                        }
                        composable("bookmarks") {
                            BookmarksScreen(
                                onBookmarkClick = { pageNumber, surahName, verses, juzNumber ->
                                    navController.navigate("reader/$pageNumber/604/$surahName/$verses/$juzNumber")
                                }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                viewModel = settingsViewModel,
                                onNavigateToDonation = {
                                    navController.navigate("donation")
                                }
                            )
                        }

                        // Donation screen
                        composable("donation") {
                            DonationScreen(
                                onBackClick = { navController.navigateUp() }
                            )
                        }

                        // Reader screen with arguments
                        composable(
                            route = "reader/{startPage}/{endPage}/{title}/{verses}/{juzNumber}",
                            arguments = listOf(
                                navArgument("startPage") { type = NavType.IntType },
                                navArgument("endPage") { type = NavType.IntType },
                                navArgument("title") { type = NavType.StringType },
                                navArgument("verses") { type = NavType.IntType; defaultValue = 0 },
                                navArgument("juzNumber") { type = NavType.IntType; defaultValue = 0 }
                            )
                        ) { backStackEntry ->
                            val startPage = backStackEntry.arguments?.getInt("startPage") ?: 1
                            val endPage = backStackEntry.arguments?.getInt("endPage") ?: 1
                            val title = backStackEntry.arguments?.getString("title") ?: "Quran"
                            val verses = backStackEntry.arguments?.getInt("verses") ?: 0
                            val juzNumber = backStackEntry.arguments?.getInt("juzNumber") ?: 0

                            QuranReaderScreen(
                                startPage = startPage,
                                endPage = endPage,
                                title = title,
                                verses = verses,
                                juzNumber = juzNumber,
                                onBackClick = { navController.navigateUp() }
                            )
                        }
                    }
                }
            }
        }
    }
}
