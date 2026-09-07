package com.example.myjobseeker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.myjobseeker.ui.theme.*

@Composable
fun JobCard(job: Job, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Header: Logo, Title, Company Info, Bookmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // company_logo
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(BlueDark)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // job_title and company_info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = job.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "${job.companyName} • ${job.location}",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }

                // btn_bookmark
                Icon(
                    painter = painterResource(id = R.drawable.ic_bookmark),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = BlueNormal
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // tags_layout: tv_match, tv_distance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                // tv_match
                Surface(
                    modifier = Modifier.height(25.dp),
                    color = SuccessGreenLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.match_format, job.matchPercentage),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        color = SuccessGreen
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // tv_distance
                Surface(
                    modifier = Modifier
                        .width(56.dp)
                        .height(25.dp),
                    color = AccentBlueLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = job.distance,
                            fontSize = 10.sp,
                            color = TextGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            // tv_salary
            Text(
                text = job.salary,
                modifier = Modifier.align(Alignment.End),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            // details_layout and btn_apply
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // details_layout: tv_time, tv_skill
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.height(25.dp),
                        color = AccentBlueLight,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = job.timeRange,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            color = TextGray
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        modifier = Modifier.height(25.dp),
                        color = AccentBlueLight,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = job.skill,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            color = TextGray
                        )
                    }
                }

                // btn_apply
                Button(
                    onClick = { /* Handle apply */ },
                    modifier = Modifier.height(38.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BlueNormal),
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.apply),
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
