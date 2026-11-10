package com.example.a22mikardex.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Materia::class],
    version = 1,
    exportSchema = false
)
abstract class KardexDatabase : RoomDatabase() {
    abstract fun materiaDao(): MateriaDao

    companion object {
        @Volatile private var INSTANCE: KardexDatabase? = null

        fun getInstance(context: Context): KardexDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    KardexDatabase::class.java,
                    "kardex.db"
                ).build().also { INSTANCE = it }
            }
    }
}
