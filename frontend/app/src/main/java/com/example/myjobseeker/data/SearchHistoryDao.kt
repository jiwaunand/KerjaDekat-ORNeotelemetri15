package com.example.myjobseeker.data

import androidx.room.*
import com.example.myjobseeker.model.SearchHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history WHERE userId = :userId ORDER BY timestamp DESC")
    fun getSearchHistory(userId: Int): Flow<List<SearchHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(searchHistory: SearchHistory)

    @Query("DELETE FROM search_history WHERE userId = :userId AND `query` = :query")
    suspend fun deleteQuery(userId: Int, query: String)

    @Query("DELETE FROM search_history WHERE userId = :userId")
    suspend fun clearHistory(userId: Int)

    @Delete
    suspend fun delete(searchHistory: SearchHistory)

    @Query("SELECT * FROM search_history WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getSearchHistorySync(userId: Int): List<SearchHistory>

    @Transaction
    suspend fun addSearchQuery(userId: Int, query: String) {
        deleteQuery(userId, query)
        insert(SearchHistory(userId = userId, query = query))
        
        val history = getSearchHistorySync(userId)
        if (history.size > 4) {
            val toDelete = history.subList(4, history.size)
            toDelete.forEach { delete(it) }
        }
    }
}
