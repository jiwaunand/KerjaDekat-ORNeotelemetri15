package com.example.myjobseeker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myjobseeker.data.AppDatabase
import com.example.myjobseeker.data.UserPreferences
import com.example.myjobseeker.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()
    private val userPreferences = UserPreferences(application)

    val isLoggedIn: StateFlow<Boolean> = userPreferences.isLoggedIn.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    val userId: StateFlow<Int> = userPreferences.userId.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        -1
    )

    val currentUser: Flow<User?> = userId.flatMapLatest { id ->
        if (id != -1) userDao.getUserById(id) else flowOf(null)
    }

    fun register(username: String, email: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                onResult(false, "Semua field harus diisi")
                return@launch
            }
            if (!email.contains("@")) {
                onResult(false, "Email tidak valid")
                return@launch
            }
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                onResult(false, "Email sudah terdaftar")
                return@launch
            }
            val newUser = User(username = username, email = email, password = password)
            userDao.insertUser(newUser)
            onResult(true, "Registrasi berhasil")
        }
    }

    fun login(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            if (email.isBlank() || password.isBlank()) {
                onResult(false, "Semua field harus diisi")
                return@launch
            }
            val user = userDao.login(email, password)
            if (user != null) {
                userPreferences.saveLoginSession(user.id)
                onResult(true, "Login berhasil")
            } else {
                onResult(false, "Email atau password salah")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.clearLoginSession()
        }
    }
}
