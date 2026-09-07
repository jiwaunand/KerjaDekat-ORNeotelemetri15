package com.example.myjobseeker.ui.applications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myjobseeker.R
import com.example.myjobseeker.model.Application
import com.example.myjobseeker.model.ApplicationStatus
import com.example.myjobseeker.ui.theme.*
import com.example.myjobseeker.viewmodel.JobViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ApplicationsScreen(
    viewModel: JobViewModel,
    onProfileClick: () -> Unit,
    onDetailClick: (Application) -> Unit,
    location: String
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(id = R.string.status_processed),
        stringResource(id = R.string.status_accepted),
        stringResource(id = R.string.status_rejected)
    )

    val applications = viewModel.applications.filter {
        when (selectedTab) {
            0 -> it.status == ApplicationStatus.DIPROSES
            1 -> it.status == ApplicationStatus.DITERIMA
            2 -> it.status == ApplicationStatus.DITOLAK
            else -> true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLightBlue)
    ) {
        ApplicationsHeader(onProfileClick, location)
        
        // Tab Selector
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFDDE6F0) // Light blue-gray for tab background
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color.White else Color.Transparent)
                            .clickable { selectedTab = index },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = title,
                                color = if (isSelected) TextDark else TextGray,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                            if (isSelected && index == 0 && applications.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    color = LogoBlue,
                                    shape = CircleShape,
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = applications.size.toString(),
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(applications) { application ->
                ApplicationCard(
                    application = application,
                    onDetailClick = { onDetailClick(application) }
                )
            }
        }
    }
}

@Composable
fun ApplicationsHeader(onProfileClick: () -> Unit, location: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(NavNavyHeader)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = LogoBlue,
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = "Logo",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

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

        Icon(
            painter = painterResource(id = R.drawable.ic_notifications),
            contentDescription = "Notifications",
            tint = Color.White,
            modifier = Modifier
                .padding(end = 12.dp)
                .size(24.dp)
                .clickable { }
        )

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFF0B244C))
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
fun ApplicationCard(application: Application, onDetailClick: () -> Unit) {
    val job = application.job
    val appliedDate = application.appliedAt.toLocalDate()
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)
    
    val timeDisplay = when (appliedDate) {
        today -> stringResource(id = R.string.today)
        yesterday -> stringResource(id = R.string.yesterday)
        else -> appliedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Job Logo
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BlueDark)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = job.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "${job.companyName} • ${job.distance}",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }

                // Status Badge
                Surface(
                    color = when (application.status) {
                        ApplicationStatus.DIPROSES -> ProcessOrangeLight
                        ApplicationStatus.DITERIMA -> SuccessGreenLight
                        ApplicationStatus.DITOLAK -> Color(0xFFFFEBEE)
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = when (application.status) {
                        ApplicationStatus.DIPROSES -> androidx.compose.foundation.BorderStroke(1.dp, ProcessOrange)
                        ApplicationStatus.DITERIMA -> androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen)
                        ApplicationStatus.DITOLAK -> androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
                    }
                ) {
                    Text(
                        text = when (application.status) {
                            ApplicationStatus.DIPROSES -> stringResource(id = R.string.status_processed)
                            ApplicationStatus.DITERIMA -> stringResource(id = R.string.status_accepted)
                            ApplicationStatus.DITOLAK -> stringResource(id = R.string.status_rejected)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (application.status) {
                            ApplicationStatus.DIPROSES -> ProcessOrange
                            ApplicationStatus.DITERIMA -> SuccessGreen
                            ApplicationStatus.DITOLAK -> Color.Red
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                // Salary Info
                Surface(
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    color = Color(0xFFF1F1F1),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = stringResource(id = R.string.salary_offer), fontSize = 10.sp, color = TextGray)
                        Text(text = job.salary, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    }
                }

                // Applied Time
                Surface(
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    color = Color(0xFFF1F1F1),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = stringResource(id = R.string.applied_time), fontSize = 10.sp, color = TextGray)
                        Text(
                            text = timeDisplay,
                            fontSize = 12.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = TextDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_work_history),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = TextGray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(id = R.string.response_time_info),
                        fontSize = 10.sp,
                        color = TextGray
                    )
                }
                
                Text(
                    text = stringResource(id = R.string.see_detail),
                    fontSize = 12.sp,
                    color = BlueNormal,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onDetailClick() }
                )
            }
        }
    }
}
