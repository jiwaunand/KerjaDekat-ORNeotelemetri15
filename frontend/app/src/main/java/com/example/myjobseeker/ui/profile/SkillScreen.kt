package com.example.myjobseeker.ui.profile

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myjobseeker.R
import com.example.myjobseeker.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillScreen(
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by authViewModel.currentUser.collectAsState(initial = null)
    var skillList by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(currentUser) {
        currentUser?.let {
            if (it.skills.isNotEmpty()) {
                skillList = it.skills.split(",").filter { s -> s.isNotBlank() }
            } else if (skillList.isEmpty()) {
                skillList = listOf("")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Keahlian", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .size(24.dp)
                            .clickable { onBackClick() }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { skillList = skillList + "" },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Skill")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Tambahkan keahlian Anda agar perusahaan lebih mudah menemukan Anda.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            skillList.forEachIndexed { index, skill ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = skill,
                        onValueChange = { newValue ->
                            val newList = skillList.toMutableList()
                            newList[index] = newValue
                            skillList = newList
                        },
                        label = { Text("Skill ${index + 1}") },
                        modifier = Modifier.weight(1f)
                    )
                    
                    IconButton(
                        onClick = {
                            val newList = skillList.toMutableList()
                            newList.removeAt(index)
                            skillList = newList
                        }
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    currentUser?.let {
                        val skillsString = skillList.filter { s -> s.isNotBlank() }.joinToString(",")
                        val updatedUser = it.copy(skills = skillsString)
                        authViewModel.updateProfile(updatedUser)
                        Toast.makeText(context, "Keahlian berhasil disimpan", Toast.LENGTH_SHORT).show()
                        onBackClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Simpan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
        }
    }
}
