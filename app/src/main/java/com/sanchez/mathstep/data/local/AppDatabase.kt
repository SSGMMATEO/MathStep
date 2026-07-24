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
    version = 3, // sube de 2 a 3 por el nuevo campo "steps" en HistoryRecord
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "mathstep_db")
                    // Aceptable para un prototipo académico: borra y recrea la tabla al
                    // subir de versión. En producción real se escribirían Migration()
                    // explícitas para no perder datos del usuario.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}