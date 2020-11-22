package io.jun.healthit.model

import androidx.lifecycle.LiveData
import androidx.room.*
import io.jun.healthit.model.data.Inbody

@Dao
interface InbodyDao {
    @Query("SELECT * FROM inbody")
    fun getAll(): LiveData<List<Inbody>>

    @Query("SELECT * FROM inbody WHERE date IN (:date)")
    fun loadById(date: String): LiveData<Inbody>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(inbody: Inbody)

    @Delete
    suspend fun delete(inbody: Inbody)

    @Update
    suspend fun update(inbody: Inbody)
}