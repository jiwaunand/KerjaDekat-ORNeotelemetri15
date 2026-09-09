package com.example.myjobseeker.ui.job

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myjobseeker.R
import com.example.myjobseeker.model.Job
import com.example.myjobseeker.ui.detail.DetailHeader
import com.example.myjobseeker.ui.theme.*
import com.example.myjobseeker.viewmodel.JobViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddJobScreen(
    jobViewModel: JobViewModel,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    onProfileClick: () -> Unit,
    location: String
) {
    var title by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var jobLocation by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf("") }
    var timeRange by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Bar
        DetailHeader(onProfileClick, onMenuClick, onBackClick, location)

        // Main Content Card
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tambah Lowongan",
                            modifier = Modifier.padding(start = 16.dp).weight(1f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
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
                                .background(Color.LightGray)
                                .clickable { launcher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (imageUri != null) {
                                AsyncImage(
                                    model = imageUri,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_work), // Using ic_work as placeholder
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = Color.Gray
                                    )
                                    Text(text = "Tambah Foto Lowongan", color = Color.Gray)
                                }
                            }
                        }

                        // Header Info Inputs
                        Column(modifier = Modifier.padding(16.dp)) {
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("Judul Pekerjaan") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = companyName,
                                onValueChange = { companyName = it },
                                label = { Text("Nama Perusahaan") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = jobLocation,
                                onValueChange = { jobLocation = it },
                                label = { Text("Lokasi (Kota)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Distance and Salary Blocks (Inputs)
                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = distance,
                                    onValueChange = { distance = it },
                                    label = { Text("Jarak (ex: 2.0 km)") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = salary,
                                    onValueChange = { salary = it },
                                    label = { Text("Gaji (ex: Rp 1jt)") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = timeRange,
                                onValueChange = { timeRange = it },
                                label = { Text("Waktu Kerja (ex: 08.00 - 17.00)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Description Section
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text("Deskripsi Pekerjaan") },
                                modifier = Modifier.fillMaxWidth().height(120.dp),
                                shape = RoundedCornerShape(8.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Skills Section (Simulated Tag Input)
                            OutlinedTextField(
                                value = skills,
                                onValueChange = { skills = it },
                                label = { Text("Tags / Skill (pisahkan dengan koma)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )

                            FlowRow(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                                skills.split(",").filter { it.isNotBlank() }.forEach { skill ->
                                    SkillTag(skill.trim())
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }

                // Bottom Action Bar
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                if (title.isNotBlank() && companyName.isNotBlank()) {
                                    val newJob = Job(
                                        id = (System.currentTimeMillis() / 1000).toInt(),
                                        title = title,
                                        companyName = companyName,
                                        location = jobLocation,
                                        matchPercentage = 100,
                                        distance = distance,
                                        salary = salary,
                                        timeRange = timeRange,
                                        skill = skills,
                                        imageUri = imageUri?.toString()
                                    )
                                    jobViewModel.addJob(newJob)
                                    onBackClick()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NavNavyHeader)
                        ) {
                            Text(
                                text = "Tambahkan",
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
fun SkillTag(skill: String) {
    Surface(
        modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
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
