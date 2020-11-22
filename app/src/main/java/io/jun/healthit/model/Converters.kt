package io.jun.healthit.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import io.jun.healthit.model.data.Record

class Converters {

    @TypeConverter
    fun toJson(value: List<ByteArray>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toList(value: String): List<ByteArray>? {

        val objects = Gson().fromJson(value, Array<ByteArray>::class.java)
        return objects.toList()
    }

    @TypeConverter
    fun toJsonRecord(value: List<Record>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toRecordList(value: String): List<Record>? {

        val objects = Gson().fromJson(value, Array<Record>::class.java)
        return objects.toList()
    }
}