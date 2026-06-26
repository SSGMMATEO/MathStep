package com.sanchez.mathstep.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sanchez.mathstep.data.local.dao.UserDao
import com.sanchez.mathstep.data.local.entity.User

/**
 * Base de datos Room — Singleton.
 * version = 1: primera versión del esquema.
 * fallbackToDestructiveMigration: si cambias la versión, borra y recrea.
 * Aceptable para prototipo académico.
 */
@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

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
