package com.example.myjobseeker.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.myjobseeker.model.Job

class SearchViewModel : ViewModel() {
    var searchQuery by mutableStateOf("")
        private set

    private val _searchHistory = mutableStateListOf("Barista", "Kasir", "Admin Toko")
    val searchHistory: List<String> = _searchHistory

    private val _autocompleteSuggestions = mutableStateListOf<String>()
    val autocompleteSuggestions: List<String> = _autocompleteSuggestions

    private var allJobs = listOf<Job>()

    fun setAllJobs(jobs: List<Job>) {
        allJobs = jobs
    }

    private val _searchResults = mutableStateListOf<Job>()
    val searchResults: List<Job> = _searchResults

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
        updateAutocomplete(newQuery)
        if (newQuery.contains(":") || newQuery.length > 2) {
            performSearchInternal(newQuery, addToHistory = false)
        } else if (newQuery.isEmpty()) {
            _searchResults.clear()
        }
    }

    private fun updateAutocomplete(query: String) {
        _autocompleteSuggestions.clear()
        if (query.isNotEmpty() && !query.contains(":")) {
            val suggestions = allJobs.map { it.title }
                .distinct()
                .filter { it.contains(query, ignoreCase = true) }
            _autocompleteSuggestions.addAll(suggestions)
        }
    }

    fun performSearch(query: String) {
        searchQuery = query
        performSearchInternal(query, addToHistory = true)
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
                        "category", "skill" -> job.description.contains(value, ignoreCase = true)
                        "company" -> job.companyName.contains(value, ignoreCase = true)
                        "salary" -> {
                            val numericSalary = value.replace(Regex("[^0-9]"), "").toLongOrNull()
                            val jobSalary = job.salary.replace(Regex("[^0-9]"), "").toLongOrNull()
                            if (numericSalary != null && jobSalary != null) {
                                jobSalary >= numericSalary
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
                addToHistory(query)
            }
        }
    }

    private fun addToHistory(query: String) {
        if (query.isNotBlank() && !_searchHistory.contains(query)) {
            _searchHistory.add(0, query)
            if (_searchHistory.size > 10) {
                _searchHistory.removeAt(_searchHistory.size - 1)
            }
        }
    }

    fun clearHistory() {
        _searchHistory.clear()
    }
}
