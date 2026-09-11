package com.example.myjobseeker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myjobseeker.R
import com.example.myjobseeker.ui.theme.TextDark
import com.example.myjobseeker.ui.theme.TextGray

@Composable
fun NavDrawerContent(
    isDark: Boolean,
    username: String?,
    email: String?,
    onBookmarkClick: () -> Unit,
    onRiwayatClick: () -> Unit,
    onModeClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = username ?: "User",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = email ?: "@user",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(8.dp))

        // Menu Items
        DrawerItem(
            icon = R.drawable.ic_bookmark,
            label = "Halaman Bookmark",
            onClick = onBookmarkClick
        )

        DrawerItem(
            icon = R.drawable.ic_work_history,
            label = "Riwayat Pekerjaan",
            onClick = onRiwayatClick
        )
        
        DrawerItem(
            icon = if (isDark) R.drawable.ic_sun else R.drawable.ic_moon, // placeholder icon, user should have better ones
            label = if (isDark) "Light Mode" else "Dark Mode",
            onClick = onModeClick
        )

        /*DrawerItem(
            icon = R.drawable.ic_settings,
            label = "Pengaturan",
            onClick = onSettingsClick
        )*/

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        DrawerItem(
            icon = R.drawable.ic_logout,
            label = "Log out",
            onClick = onLogoutClick,
            textColor = Color.Red
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun DrawerItem(
    icon: Int? = null,
    imageVector: androidx.compose.ui.graphics.vector.ImageVector? = null,
    label: String,
    onClick: () -> Unit,
    textColor: Color? = null
) {
    val finalTextColor = textColor ?: MaterialTheme.colorScheme.onSurface
    val iconColor = if (textColor == Color.Red) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = iconColor
            )
        } else if (imageVector != null) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = iconColor
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = label,
            fontSize = 16.sp,
            color = finalTextColor,
            fontWeight = FontWeight.Medium
        )
    }
}
