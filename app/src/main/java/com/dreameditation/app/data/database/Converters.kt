package com.dreameditation.app.data.database

import androidx.room.TypeConverter
import com.dreameditation.app.data.model.AudioCategory
import com.dreameditation.app.data.model.SessionType

class Converters {
    @TypeConverter
    fun fromAudioCategory(category: AudioCategory): String {
        return category.name
    }

    @TypeConverter
    fun toAudioCategory(category: String): AudioCategory {
        return AudioCategory.valueOf(category)
    }

    @TypeConverter
    fun fromSessionType(type: SessionType): String {
        return type.name
    }

    @TypeConverter
    fun toSessionType(type: String): SessionType {
        return SessionType.valueOf(type)
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split(",")
    }
}