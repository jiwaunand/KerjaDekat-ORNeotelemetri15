package com.example.myjobseeker.ui.bookmark

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.myjobseeker.ui.home.getDummyJobs
import com.example.myjobseeker.ui.theme.*
import com.example.myjobseeker.viewmodel.JobViewModel

import com.example.myjobseeker.ui.home.HomeHeader

@Composable
fun BookmarkScreen(
    viewModel: JobViewModel,
    onJobClick: (Job) -> Unit,
    onApplyClick: (Job) -> Unit,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onMenuClick: () -> Unit,
    location: String
) {
    val bookmarkedJobIds by viewModel.bookmarkedJobIds.collectAsState()
    val allJobs = getDummyJobs() // Assuming this is available or moved to a shared place
    val bookmarkedJobs = allJobs.filter { bookmarkedJobIds.contains(it.id) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HomeHeader(onProfileClick, onMenuClick, onNotificationClick, location)

        Text(
            text = "Halaman Bookmark",
            modifier = Modifier.padding(16.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (bookmarkedJobs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "tidak ada bookmark yang tersedia",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(bookmarkedJobs) { job ->
                    JobCard(
                        job = job,
                        onClick = { onJobClick(job) },
                        onApplyClick = { onApplyClick(job) },
                        isBookmarked = true,
                        onBookmarkClick = { viewModel.toggleBookmark(job) }
                    )
                }
            }
        }
    }
}
