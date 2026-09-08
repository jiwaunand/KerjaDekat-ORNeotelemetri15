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

    private val allJobs = listOf(
        Job(1, "Barista", "Kopi Kenangan", "Khatib Sulaiman", 99, "1.8 km", "Rp. 70.000/hari", "15.00 - 22.00", "Basic Coffee"),
        Job(2, "Admin Toko", "Toko Fotocopy", "Limau Manis", 70, "7.5 km", "Rp. 100.000/mg", "19.00 - 21.00", "Printing, dll"),
        Job(3, "Social Media Designer", "Kopi Kenangan", "Khatib Sulaiman", 75, "1.8 km", "Rp. 50.000/design", "free time", "Design"),
        Job(4, "programer", "PT. mencari cinta sejati", "Padang", 90, "20km","RP. 10.000.000/project","8 jam","coding"),
        Job(5, "programer", "PT. mencari cinta sejati", "bukittinggi", 90, "90km","RP. 10.000.000/project","part time","coding"),
    )

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
                        "category" -> job.skill.contains(value, ignoreCase = true)
                        "company" -> job.companyName.contains(value, ignoreCase = true)
                        "skill" -> job.skill.contains(value, ignoreCase = true)
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
                        "timerange" -> job.timeRange.contains(value, ignoreCase = true)
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
