package io.jun.healthit.model.data

import androidx.room.*

@Entity
data class Inbody (
    @PrimaryKey(autoGenerate = false) val date: String,
    @ColumnInfo(name = "weight") val weight: Float?,
    @ColumnInfo(name = "skeletalMuscle") val skeletalMuscle: Float?,
    @ColumnInfo(name = "percentFat") val percentFat: Float?,
)