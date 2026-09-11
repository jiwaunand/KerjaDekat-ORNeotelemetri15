package com.example.myjobseeker.ui.search

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myjobseeker.R
import com.example.myjobseeker.model.Job
import com.example.myjobseeker.ui.components.JobCard
import com.example.myjobseeker.ui.home.HomeHeader
import com.example.myjobseeker.ui.home.SearchBar
import com.example.myjobseeker.ui.theme.*
import com.example.myjobseeker.viewmodel.SearchViewModel

import com.example.myjobseeker.viewmodel.JobViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    jobViewModel: JobViewModel,
    onJobClick: (Job) -> Unit,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onMenuClick: () -> Unit,
    location: String
) {
    val searchQuery = viewModel.searchQuery
    val searchHistory by viewModel.searchHistory.collectAsState()
    val autocompleteSuggestions = viewModel.autocompleteSuggestions
    val searchResults = viewModel.searchResults
    
    val jobs by jobViewModel.jobs.collectAsState()
    val bookmarkedJobIds by jobViewModel.bookmarkedJobIds.collectAsState()

    LaunchedEffect(jobs) {
        viewModel.setAllJobs(jobs)
    }

    val voiceRecognitionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK && result.data != null) {
            val results = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!results.isNullOrEmpty()) {
                viewModel.performSearch(results[0])
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HomeHeader(
            onProfileClick = onProfileClick,
            onMenuClick = onMenuClick,
            onNotificationClick = onNotificationClick,
            location = location
        )

        SearchBar(
            text = searchQuery,
            onTextChange = { viewModel.onSearchQueryChange(it) },
            onMicClick = {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Silakan bicara...")
                }
                voiceRecognitionLauncher.launch(intent)
            },
            onSearch = {
                viewModel.performSearch(searchQuery)
            }
        )

        FilterChipsRow(
            onFilterSelect = { filterTag ->
                when (filterTag.lowercase()) {
                    "salary" -> {
                        // If user has typed a number, use it as the salary nominal and perform search
                        val nominal = searchQuery.filter { it.isDigit() }
                        if (nominal.isNotEmpty()) {
                            viewModel.performSearch("salary: $nominal")
                        } else {
                            // If search box is empty or doesn't have digits, just set the prefix
                            viewModel.onSearchQueryChange("salary: ")
                        }
                    }
                    "terdekat" -> viewModel.performSearch("location: Padang")
                    "gaji harian" -> viewModel.performSearch("salary: 70000")
                    "location" -> {
                        // If user typed a location name, search for it
                        if (searchQuery.isNotEmpty() && !searchQuery.contains(":")) {
                            viewModel.performSearch("location: $searchQuery")
                        } else {
                            viewModel.onSearchQueryChange("location: ")
                        }
                    }
                    "company" -> {
                        viewModel.onSearchQueryChange("$filterTag: ")
                    }
                    else -> viewModel.performSearch(filterTag)
                }
            }
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            if (!viewModel.isSearchActive) {
                // Show Search History or Autocomplete Suggestions while typing
                if (searchQuery.isEmpty()) {
                    item {
                        Text(
                            text = "Riwayat Pencarian",
                            modifier = Modifier.padding(16.dp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    items(searchHistory) { historyItem ->
                        SearchHistoryItem(
                            text = historyItem,
                            onClick = { viewModel.performSearch(historyItem) }
                        )
                    }
                } else {
                    // Show suggestions while typing
                    items(autocompleteSuggestions) { suggestion ->
                        SearchHistoryItem(
                            text = suggestion,
                            onClick = { viewModel.performSearch(suggestion) }
                        )
                    }
                }
            } else {
                // Show Search Results or "No results" only after Enter/Search performed
                if (searchResults.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tidak ada lowongan yang ditemukan",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "${searchResults.size} Lowongan",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    items(searchResults) { job ->
                        JobCard(
                            job = job,
                            onClick = { onJobClick(job) },
                            isBookmarked = bookmarkedJobIds.contains(job.id),
                            onBookmarkClick = { jobViewModel.toggleBookmark(job) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChipsRow(onFilterSelect: (String) -> Unit) {
    val allFilters = listOf(
        "Location", "Salary", "Company", "Terdekat", "Gaji Harian"
    )
    var isExpanded by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("") }

    val displayFilters = if (isExpanded) allFilters else allFilters.take(3)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(displayFilters) { filter ->
                val isSelected = filter == selectedFilter
                Surface(
                    modifier = Modifier.clickable { 
                        selectedFilter = filter
                        onFilterSelect(filter)
                    },
                    color = if (isSelected) BlueNormal else MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp),
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                ) {
                    Text(
                        text = filter,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Surface(
            modifier = Modifier
                .size(32.dp)
                .clickable { isExpanded = !isExpanded },
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Show less" else "Show more",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SearchHistoryItem(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            modifier = Modifier.padding(start = 12.dp),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp
        )
    }
}
