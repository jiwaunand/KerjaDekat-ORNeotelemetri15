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

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onJobClick: (Job) -> Unit,
    onProfileClick: () -> Unit,
    onApplyClick: (Job) -> Unit,
    location: String
) {
    val searchQuery = viewModel.searchQuery
    val searchHistory = viewModel.searchHistory
    val autocompleteSuggestions = viewModel.autocompleteSuggestions
    val searchResults = viewModel.searchResults

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
            .background(BackgroundLightBlue)
    ) {
        HomeHeader(onProfileClick = onProfileClick, location = location)

        Column(modifier = Modifier.fillMaxSize()) {
            SearchBar(
                text = searchQuery,
                onTextChange = { viewModel.onSearchQueryChange(it) },
                onMicClick = {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Silakan bicara...")
                    }
                    voiceRecognitionLauncher.launch(intent)
                }
            )

            FilterChipsRow()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                if (searchQuery.isEmpty()) {
                    // Show Search History
                    item {
                        Text(
                            text = "Riwayat Pencarian",
                            modifier = Modifier.padding(16.dp),
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }
                    items(searchHistory) { historyItem ->
                        SearchHistoryItem(
                            text = historyItem,
                            onClick = { viewModel.performSearch(historyItem) }
                        )
                    }
                } else if (searchResults.isEmpty() && autocompleteSuggestions.isNotEmpty()) {
                    // Show Autocomplete Suggestions
                    items(autocompleteSuggestions) { suggestion ->
                        SearchHistoryItem(
                            text = suggestion,
                            onClick = { viewModel.performSearch(suggestion) }
                        )
                    }
                } else {
                    // Show Search Results
                    item {
                        Text(
                            text = "${searchResults.size} Lowongan",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextDark
                        )
                    }
                    items(searchResults) { job ->
                        JobCard(
                            job = job,
                            onClick = { onJobClick(job) },
                            onApplyClick = { onApplyClick(job) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChipsRow() {
    val filters = listOf("Terdekat", "Shift Fleksibel", "Gaji Harian")
    var selectedFilter by remember { mutableStateOf("Terdekat") }

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
            items(filters) { filter ->
                val isSelected = filter == selectedFilter
                Surface(
                    modifier = Modifier.clickable { selectedFilter = filter },
                    color = if (isSelected) BlueNormal else Color.White,
                    shape = RoundedCornerShape(16.dp),
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                ) {
                    Text(
                        text = filter,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = if (isSelected) Color.White else TextGray,
                        fontSize = 12.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Surface(
            modifier = Modifier.size(32.dp),
            color = Color.White,
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "More filters",
                tint = TextGray
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
            tint = TextGray
        )
        Text(
            text = text,
            modifier = Modifier.padding(start = 12.dp),
            color = TextDark,
            fontSize = 14.sp
        )
    }
}
