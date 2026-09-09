package com.example.myjobseeker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myjobseeker.model.Application
import com.example.myjobseeker.model.Bookmark
import com.example.myjobseeker.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    suspend fun login(email: String, password: String): User?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: Int): Flow<User?>

    // Bookmarks
    @Insert
    suspend fun insertBookmark(bookmark: Bookmark)

    @Query("DELETE FROM bookmarks WHERE userId = :userId AND jobId = :jobId")
    suspend fun deleteBookmark(userId: Int, jobId: Int)

    @Query("SELECT jobId FROM bookmarks WHERE userId = :userId")
    fun getBookmarkedJobIds(userId: Int): Flow<List<Int>>

    // Applications
    @Insert
    suspend fun insertApplication(application: Application)

    @Query("UPDATE applications SET status = :status WHERE id = :applicationId")
    suspend fun updateApplicationStatus(applicationId: Int, status: com.example.myjobseeker.model.ApplicationStatus)

    @Query("SELECT * FROM applications WHERE userId = :userId")
    fun getApplicationsByUserId(userId: Int): Flow<List<Application>>

    @Query("DELETE FROM applications WHERE id = :applicationId")
    suspend fun deleteApplication(applicationId: Int)

    // Notifications
    @Insert
    suspend fun insertNotification(notification: com.example.myjobseeker.model.Notification)

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsByUserId(userId: Int): Flow<List<com.example.myjobseeker.model.Notification>>

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markAsRead(notificationId: Int)

    @Query("DELETE FROM notifications WHERE userId = :userId")
    suspend fun clearNotifications(userId: Int)
}
