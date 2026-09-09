package com.example.myjobseeker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myjobseeker.data.UserPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val userPreferences = UserPreferences(application)
    private val _currentUserId = MutableStateFlow(-1)

    val isDarkTheme: StateFlow<Boolean> = _currentUserId
        .flatMapLatest { userId ->
            if (userId != -1) userPreferences.getTheme(userId)
            else flowOf(false)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setCurrentUser(userId: Int) {
        _currentUserId.value = userId
    }

    fun toggleTheme() {
        val userId = _currentUserId.value
        if (userId == -1) return
        viewModelScope.launch {
            userPreferences.setTheme(userId, !isDarkTheme.value)
        }
    }
}
