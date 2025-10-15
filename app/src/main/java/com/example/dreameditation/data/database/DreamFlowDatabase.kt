package com.example.dreamaze.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.dreamaze.data.dao.AudioTrackDao
import com.example.dreamaze.data.dao.SessionDao
import com.example.dreamaze.data.model.AudioTrack
import com.example.dreamaze.data.model.Session

@Database(
    entities = [
        AudioTrack::class,
        Session::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DreamFlowDatabase : RoomDatabase() {

    abstract fun audioTrackDao(): AudioTrackDao
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile
        private var INSTANCE: DreamFlowDatabase? = null

        fun getDatabase(context: Context): DreamFlowDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DreamFlowDatabase::class.java,
                    "dreamflow_database"
                )
                    .fallbackToDestructiveMigration() // For development only
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}