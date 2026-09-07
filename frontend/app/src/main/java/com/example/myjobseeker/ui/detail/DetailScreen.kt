package com.example.myjobseeker.ui.detail

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myjobseeker.R
import com.example.myjobseeker.model.Job
import com.example.myjobseeker.ui.theme.*

@Composable
fun DetailScreen(
    job: Job,
    onBackClick: () -> Unit,
    onApplyClick: () -> Unit,
    location: String,
    buttonText: String = stringResource(id = R.string.apply_job),
    buttonColor: Color = NavNavyHeader
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLightBlue)
    ) {
        // Top Dark Bar (Header)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavNavyHeader)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = LogoBlue,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.logo),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
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
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp).padding(end = 16.dp)
            )
            
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF0B244C), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Main Content Card
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 88.dp) // Space for bottom bar
                ) {
                    // Toolbar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable(onClick = onBackClick),
                            tint = TextDark
                        )
                        Text(
                            text = stringResource(id = R.string.job_detail_title),
                            modifier = Modifier.padding(start = 16.dp).weight(1f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextDark
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_share),
                            contentDescription = "Share",
                            modifier = Modifier.size(24.dp),
                            tint = TextDark
                        )
                    }

                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Image Area
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_background), // Using background as placeholder
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().background(BlueDark),
                                contentScale = ContentScale.Crop
                            )
                            
                            // Verified Tag
                            Surface(
                                modifier = Modifier.padding(12.dp).align(Alignment.TopStart),
                                color = Color.Black.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_verified),
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = SuccessGreen
                                    )
                                    Text(
                                        text = stringResource(id = R.string.verified_employer),
                                        modifier = Modifier.padding(start = 6.dp),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Heart and Bookmark icons
                            Row(
                                modifier = Modifier.padding(12.dp).align(Alignment.TopEnd)
                            ) {
                                Surface(
                                    modifier = Modifier.size(32.dp),
                                    color = Color.White.copy(alpha = 0.9f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_heart_outline),
                                        contentDescription = null,
                                        modifier = Modifier.padding(8.dp),
                                        tint = TextDark
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    modifier = Modifier.size(32.dp),
                                    color = Color.White.copy(alpha = 0.9f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_bookmark_outline),
                                        contentDescription = null,
                                        modifier = Modifier.padding(8.dp),
                                        tint = TextDark
                                    )
                                }
                            }
                        }

                        // Header Info
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.Top) {
                                Box(modifier = Modifier.size(48.dp).background(Color(0xFF5D6D7E)))
                                Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                                    Text(text = job.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
                                    Text(text = job.companyName, color = TextGray, fontSize = 14.sp)
                                }
                                Surface(
                                    color = Color(0xFFEBF2FF),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.shift_night),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        color = BlueNormal,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Distance and Salary Blocks
                            Row(modifier = Modifier.fillMaxWidth()) {
                                InfoBlock(
                                    label = stringResource(id = R.string.distance_label),
                                    value = stringResource(id = R.string.distance_near_format, job.distance),
                                    icon = R.drawable.ic_location,
                                    iconColor = BlueNormal,
                                    bgColor = AccentBlueLight,
                                    iconBgColor = Color(0xFFEBF2FF),
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                InfoBlock(
                                    label = stringResource(id = R.string.net_salary),
                                    value = job.salary,
                                    icon = R.drawable.ic_cash,
                                    iconColor = SuccessGreen,
                                    bgColor = AccentBlueLight,
                                    iconBgColor = Color(0xFFE8F6EF),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Description Section
                            SectionContainer(title = stringResource(id = R.string.job_description)) {
                                Text(
                                    text = stringResource(id = R.string.job_description_placeholder),
                                    color = TextGray,
                                    fontSize = 13.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Skills Section
                            SectionContainer(title = stringResource(id = R.string.main_skills)) {
                                FlowRow(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                                    SkillTag(stringResource(id = R.string.skill_1))
                                    SkillTag(stringResource(id = R.string.skill_2))
                                    SkillTag(stringResource(id = R.string.skill_3))
                                    SkillTag(stringResource(id = R.string.skill_4))
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Match Section
                            SectionContainer(title = stringResource(id = R.string.match_percentage)) {
                                Text(
                                    text = stringResource(id = R.string.match_format, job.matchPercentage),
                                    color = SuccessGreen,
                                    fontSize = 13.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }

                // Bottom Action Bar
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            color = AccentBlueLight,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chat),
                                contentDescription = null,
                                modifier = Modifier.padding(12.dp),
                                tint = TextDark
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = onApplyClick,
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                        ) {
                            Text(
                                text = buttonText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoBlock(label: String, value: String, icon: Int, iconColor: Color, bgColor: Color, iconBgColor: Color, modifier: Modifier) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(32.dp),
                color = iconBgColor,
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.padding(8.dp),
                    tint = iconColor
                )
            }
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(text = label, fontSize = 10.sp, color = TextGray)
                Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
            }
        }
    }
}

@Composable
fun SectionContainer(title: String, content: @Composable () -> Unit) {
    Surface(
        color = AccentBlueLight,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun SkillTag(skill: String) {
    Surface(
        modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
        color = Color.White,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = skill,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            color = BlueNormal,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        content = { content() }
    )
}
