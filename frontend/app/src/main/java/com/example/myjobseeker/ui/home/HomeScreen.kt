package com.example.myjobseeker.ui.home

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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

@Composable
fun HomeScreen(
    onJobClick: (Job) -> Unit,
    onProfileClick: () -> Unit,
    onApplyClick: (Job) -> Unit,
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

    Scaffold(
        topBar = { HomeHeader(onProfileClick, location) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLightBlue)
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
                    }
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
                        color = TextDark
                    )
                    Text(
                        text = stringResource(id = R.string.see_all),
                        color = BlueNormal,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { }
                    )
                }
            }

            items(getDummyJobs()) { job ->
                JobCard(
                    job = job,
                    onClick = { onJobClick(job) },
                    onApplyClick = { onApplyClick(job) }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun HomeHeader(onProfileClick: () -> Unit, location: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavNavyHeader)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // tv_logo
        Surface(
            color = LogoBlue,
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = "login",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
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
                tint = Color.White
            )
            Text(
                text = location,
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // iv_notification
        Icon(
            painter = painterResource(id = R.drawable.ic_notifications),
            contentDescription = "Notifications",
            tint = Color.White,
            modifier = Modifier
                .padding(end = 12.dp)
                .size(24.dp)
                .clickable { }
        )

        // iv_profile
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFF0B244C)) // bg_circle_dark color or similar
                .clickable(onClick = onProfileClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_person),
                contentDescription = "Profile",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SearchBar(
    text: String,
    onTextChange: (String) -> Unit,
    onMicClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                tint = TextGray,
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
                        color = TextGray
                    )
                }
                BasicTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 14.sp, color = TextDark),
                    singleLine = true
                )
            }

            // iv_mic
            Icon(
                painter = painterResource(id = R.drawable.ic_mic),
                contentDescription = "Voice Search",
                tint = TextDark,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(20.dp)
                    .clickable { onMicClick() }
            )
            
            // iv_filter
            Icon(
                painter = painterResource(id = R.drawable.ic_filter),
                contentDescription = "Filter",
                tint = TextDark,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { }
            )
        }
    }
}

private fun getDummyJobs(): List<Job> = listOf(
    Job(1, "Barista", "Kopi Kenangan", "Khatib Sulaiman", 99, "1.8 km", "Rp. 70.000/hari", "15.00 - 22.00", "Basic Coffee"),
    Job(2, "Admin Toko", "Toko Fotocopy", "Limau Manis", 70, "7.5 km", "Rp. 100.000/mg", "19.00 - 21.00", "Printing, dll"),
    Job(3, "Social Media Designer", "Kopi Kenangan", "Khatib Sulaiman", 75, "1.8 km", "Rp. 50.000/design", "free time", "Design")
)
