package com.example.myjobseeker.viewmodel

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myjobseeker.data.AppDatabase
import com.example.myjobseeker.model.Job
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val searchHistoryDao = AppDatabase.getDatabase(application).searchHistoryDao()
    
    var searchQuery by mutableStateOf("")
        private set

    var isSearchActive by mutableStateOf(false)
        private set

    private val _currentUserId = MutableStateFlow(-1)
    
    val searchHistory: StateFlow<List<String>> = _currentUserId
        .flatMapLatest { userId ->
            if (userId != -1) {
                searchHistoryDao.getSearchHistory(userId).map { historyList ->
                    historyList.map { it.query }
                }
            } else {
                flowOf(emptyList())
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _autocompleteSuggestions = mutableStateListOf<String>()
    val autocompleteSuggestions: List<String> = _autocompleteSuggestions

    private var allJobs = listOf<Job>()

    fun setCurrentUser(userId: Int) {
        _currentUserId.value = userId
    }

    fun setAllJobs(jobs: List<Job>) {
        allJobs = jobs
    }

    private val _searchResults = mutableStateListOf<Job>()
    val searchResults: List<Job> = _searchResults

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
        isSearchActive = false
        updateAutocomplete(newQuery)
        if (newQuery.isEmpty()) {
            _searchResults.clear()
        }
    }

    private fun updateAutocomplete(query: String) {
        _autocompleteSuggestions.clear()
        if (query.isNotEmpty() && !query.contains(":")) {
            // Get matching terms from search history
            val historySuggestions = searchHistory.value.filter { 
                it.contains(query, ignoreCase = true) 
            }

            // Get matching terms from job titles
            val jobSuggestions = allJobs.map { it.title }
                .distinct()
                .filter { it.contains(query, ignoreCase = true) }
            
            // Combine suggestions, prioritizing history, and taking unique items
            val combined = (historySuggestions + jobSuggestions).distinct()
            _autocompleteSuggestions.addAll(combined)
        }
    }

    fun performSearch(query: String) {
        if (query.isNotBlank()) {
            searchQuery = query
            isSearchActive = true
            performSearchInternal(query, addToHistory = true)
        }
    }

    private fun performSearchInternal(query: String, addToHistory: Boolean) {
        _searchResults.clear()
        if (query.isNotEmpty()) {
            val results = if (query.contains(":")) {
                val parts = query.split(":", limit = 2)
                val prefix = parts[0].trim().lowercase()
                val value = parts.getOrNull(1)?.trim() ?: ""
                
                allJobs.filter { job ->
                    when (prefix) {
                        "company" -> job.companyName.contains(value, ignoreCase = true)
                        "salary" -> {
                            val numericSearchValue = value.replace(Regex("[^0-9]"), "")
                            val numericJobSalary = job.salary.replace(Regex("[^0-9]"), "")
                            
                            if (numericSearchValue.isNotEmpty() && numericJobSalary.isNotEmpty()) {
                                numericJobSalary == numericSearchValue
                            } else {
                                job.salary.contains(value, ignoreCase = true)
                            }
                        }
                        "location" -> job.location.contains(value, ignoreCase = true)
                        else -> job.title.contains(query, ignoreCase = true) || job.companyName.contains(query, ignoreCase = true)
                    }
                }
            } else {
                allJobs.filter { 
                    it.title.contains(query, ignoreCase = true) || 
                    it.companyName.contains(query, ignoreCase = true) 
                }
            }
            _searchResults.addAll(results)
            if (addToHistory) {
                addQueryToHistory(query)
            }
        }
    }

    private fun addQueryToHistory(query: String) {
        val userId = _currentUserId.value
        if (userId != -1 && query.isNotBlank()) {
            viewModelScope.launch {
                searchHistoryDao.addSearchQuery(userId, query)
            }
        }
    }

    fun clearHistory() {
        val userId = _currentUserId.value
        if (userId != -1) {
            viewModelScope.launch {
                searchHistoryDao.clearHistory(userId)
            }
        }
    }
}
