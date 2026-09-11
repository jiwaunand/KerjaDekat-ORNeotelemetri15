package com.example.myjobseeker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myjobseeker.model.Application
import com.example.myjobseeker.model.ApplicationStatus
import com.example.myjobseeker.model.Bookmark
import com.example.myjobseeker.model.User
import com.example.myjobseeker.model.Notification
import com.example.myjobseeker.model.SearchHistory
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class Converters {
    @TypeConverter
    fun fromStatus(status: ApplicationStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): ApplicationStatus = ApplicationStatus.valueOf(value)
}

@Database(entities = [User::class, Bookmark::class, Application::class, Notification::class, SearchHistory::class], version = 5, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "myjobseeker_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
