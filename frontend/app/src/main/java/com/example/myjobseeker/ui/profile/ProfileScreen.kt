package com.example.myjobseeker.ui.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import com.example.myjobseeker.ui.theme.*

@Composable
fun ProfileScreen(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLightBlue)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(NavNavyHeader)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onBackClick),
                tint = Color.White
            )

            Spacer(modifier = Modifier.width(12.dp))

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
            
            Text(
                text = stringResource(id = R.string.nav_profile),
                modifier = Modifier.padding(start = 12.dp).weight(1f),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Icon(
                painter = painterResource(id = R.drawable.ic_notifications),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp).padding(4.dp)
            )
        }

        // Main Content Card
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Section (Avatar + Edit FAB)
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(100.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = Color(0xFFD9D9D9),
                        border = BorderStroke(2.dp, TextDark)
                    ) {
                        // Image placeholder
                    }
                    
                    Surface(
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.BottomEnd),
                        shape = CircleShape,
                        color = NavNavyHeader
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.White,
                            modifier = Modifier.padding(8.dp).size(18.dp)
                        )
                    }
                }

                Text(
                    text = "lorem ipsum",
                    modifier = Modifier.padding(top = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextDark
                )
                
                Text(
                    text = "@loremipsum",
                    color = TextGray,
                    fontSize = 14.sp
                )
                
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_location),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = LogoBlue
                    )
                    Text(
                        text = stringResource(id = R.string.location_placeholder),
                        modifier = Modifier.padding(start = 4.dp),
                        color = TextGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Stats Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    StatBlock(
                        label = stringResource(id = R.string.stat_lamaran),
                        value = "7",
                        modifier = Modifier.weight(1f)
                    )
                    StatBlock(
                        label = stringResource(id = R.string.stat_diprogres),
                        value = "3",
                        modifier = Modifier.weight(1f)
                    )
                    StatBlock(
                        label = stringResource(id = R.string.stat_selesai),
                        value = "1",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Menu Items
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    ProfileMenuItem(icon = R.drawable.ic_person, title = stringResource(id = R.string.menu_profile))
                    ProfileMenuItem(icon = R.drawable.ic_skill, title = stringResource(id = R.string.menu_skill))
                    ProfileMenuItem(icon = R.drawable.ic_work_history, title = stringResource(id = R.string.menu_riwayat))
                    ProfileMenuItem(icon = R.drawable.ic_settings, title = stringResource(id = R.string.menu_pengaturan))
                    ProfileMenuItem(icon = R.drawable.ic_help, title = stringResource(id = R.string.menu_bantuan), isLast = true)
                }

                // Logout Button
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 24.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, CardStatBg),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LogoutRed)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_logout),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = LogoutRed
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.logout),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatBlock(label: String, value: String, modifier: Modifier) {
    Surface(
        modifier = modifier.padding(4.dp),
        color = CardStatBg,
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
            Text(text = label, fontSize = 12.sp, color = TextDark)
        }
    }
}

@Composable
fun ProfileMenuItem(icon: Int, title: String, isLast: Boolean = false) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLast) 24.dp else 8.dp)
            .clickable { },
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF8F9FA) // bg_menu_item color or similar light gray
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = TextDark
            )
            Text(
                text = title,
                modifier = Modifier.padding(start = 12.dp),
                fontWeight = FontWeight.Bold,
                color = TextDark,
                fontSize = 15.sp
            )
        }
    }
}
