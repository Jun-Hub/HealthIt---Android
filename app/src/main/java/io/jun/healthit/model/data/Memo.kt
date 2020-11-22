package io.jun.healthit.model.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Memo (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "content") val content: String?,
    @ColumnInfo(name = "record") val record: List<Record>?,
    @ColumnInfo(name = "photo") val photo: List<ByteArray>?,
    @ColumnInfo(name = "date") val date: String?,
    @ColumnInfo(name = "tag") val tag: Int?,
    @ColumnInfo(name = "pin") var pin: Boolean?
)
