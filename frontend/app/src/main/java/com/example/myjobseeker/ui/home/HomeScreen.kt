package com.example.myjobseeker.ui.home

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myjobseeker.R
import com.example.myjobseeker.model.Job
import com.example.myjobseeker.ui.components.JobCard
import com.example.myjobseeker.ui.theme.*

import com.example.myjobseeker.viewmodel.JobViewModel

@Composable
fun HomeScreen(
    jobViewModel: JobViewModel,
    currentUserId: Int,
    onJobClick: (Job) -> Unit,
    onProfileClick: () -> Unit,
    onApplyClick: (Job) -> Unit,
    onSearchClick: (String) -> Unit,
    onNotificationClick: () -> Unit,
    onMenuClick: () -> Unit,
    location: String
) {
    var searchText by remember { mutableStateOf("") }

    val voiceRecognitionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK && result.data != null) {
            val results = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!results.isNullOrEmpty()) {
                searchText = results[0]
            }
        }
    }

    val jobs by jobViewModel.jobs.collectAsState()
    val bookmarkedJobIds by jobViewModel.bookmarkedJobIds.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HomeHeader(onProfileClick, onMenuClick, onNotificationClick, location)
        
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            item {
                SearchBar(
                    text = searchText,
                    onTextChange = { searchText = it },
                    onMicClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Silakan bicara...")
                        }
                        voiceRecognitionLauncher.launch(intent)
                    },
                    onClick = { onSearchClick(it) }
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.recommendation_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(id = R.string.see_all),
                        color = BlueNormal,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { }
                    )
                }
            }

            if (jobs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ada lowongan tersedia saat ini",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(jobs) { job ->
                    JobCard(
                        job = job,
                        currentUserId = currentUserId,
                        onClick = { onJobClick(job) },
                        onApplyClick = { onApplyClick(job) },
                        isBookmarked = bookmarkedJobIds.contains(job.id),
                        onBookmarkClick = { jobViewModel.toggleBookmark(job) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun HomeHeader(
    onProfileClick: () -> Unit,
    onMenuClick: () -> Unit,
    onNotificationClick: () -> Unit,
    location: String
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // tv_logo -> Now a clickable logo for drawer
            Surface(
                color = Color.Transparent,
                modifier = Modifier.clickable(onClick = onMenuClick)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.kerja_dekat),
                    contentDescription = "Menu",
                    modifier = Modifier
                        .size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Location Info
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_location),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = LogoBlue
                )
                Text(
                    text = location,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp),
                    fontWeight = FontWeight.Medium
                )
            }

            // iv_notification
            Icon(
                painter = painterResource(id = R.drawable.ic_notifications),
                contentDescription = "Notifications",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(24.dp)
                    .clickable { onNotificationClick() }
            )

            // iv_profile
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(onClick = onProfileClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    text: String,
    onTextChange: (String) -> Unit,
    onMicClick: () -> Unit,
    onClick: ((String) -> Unit)? = null
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = { onClick(text) }) else Modifier),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // iv_search_icon
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            
            // et_search
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (text.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.search_hint),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                BasicTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface),
                    singleLine = true
                )
            }

            // iv_mic
            Icon(
                painter = painterResource(id = R.drawable.ic_mic),
                contentDescription = "Voice Search",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(20.dp)
                    .clickable { onMicClick() }
            )
            
            // iv_filter with Dropdown
            Box {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filter),
                    contentDescription = "Filter",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { showMenu = true }
                )
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Category") },
                        onClick = {
                            if (onClick != null) onClick("category: ") else onTextChange("category: ")
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Company") },
                        onClick = {
                            if (onClick != null) onClick("company: ") else onTextChange("company: ")
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Skill") },
                        onClick = {
                            if (onClick != null) onClick("skill: ") else onTextChange("skill: ")
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Salary") },
                        onClick = {
                            if (onClick != null) onClick("salary: ") else onTextChange("salary: ")
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Location") },
                        onClick = {
                            if (onClick != null) onClick("location: ") else onTextChange("location: ")
                            showMenu = false
                        }
                    )
                }
            }
        }
    }
}

fun getDummyJobs(): List<Job> = listOf(
    Job(1, "Barista", "Kopi Kenangan", "Sebagai barista yang berpengalaman dalam membuat kopi dan meracik minuman.", "Khatib Sulaiman", "Rp. 70.000/hari"),
    Job(2, "Admin Toko", "Toko Fotocopy", "Melakukan administrasi toko dan pelayanan pelanggan.", "Limau Manis", "Rp. 100.000/mg"),
    Job(3, "Social Media Designer", "Kopi Kenangan", "Mendesain konten kreatif untuk media sosial.", "Khatib Sulaiman", "Rp. 50.000/design"),
    Job(4, "programer", "PT. mencari cinta sejati", "Membangun aplikasi web dan mobile.", "Padang", "RP. 10.000.000/project"),
    Job(5, "programer", "PT. mencari cinta sejati", "Membangun sistem backend.", "bukittinggi", "RP. 10.000.000/project"),
)
