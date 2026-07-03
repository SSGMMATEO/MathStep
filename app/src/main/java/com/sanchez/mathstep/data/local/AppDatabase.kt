package com.sanchez.mathstep.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sanchez.mathstep.data.local.dao.HistoryDao
import com.sanchez.mathstep.data.local.dao.UserDao
import com.sanchez.mathstep.data.local.entity.HistoryRecord
import com.sanchez.mathstep.data.local.entity.User

@Database(
    entities = [User::class, HistoryRecord::class],
    version = 2,                          // sube a 2 por la nueva tabla
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mathstep_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}