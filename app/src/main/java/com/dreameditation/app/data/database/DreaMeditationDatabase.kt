package com.dreameditation.app.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.dreameditation.app.data.dao.AudioTrackDao
import com.dreameditation.app.data.dao.SessionDao
import com.dreameditation.app.data.model.AudioTrack
import com.dreameditation.app.data.model.Session

@Database(
    entities = [
        AudioTrack::class,
        Session::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class DreaMeditationDatabase : RoomDatabase() {

    abstract fun audioTrackDao(): AudioTrackDao
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile
        private var INSTANCE: DreaMeditationDatabase? = null

        fun getDatabase(context: Context): DreaMeditationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DreaMeditationDatabase::class.java,
                    "DreaMeditation_database"
                )
                    .fallbackToDestructiveMigration()
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun clearInstance() {
            synchronized(this) {
                INSTANCE?.close()
                INSTANCE = null
            }
        }
    }
}