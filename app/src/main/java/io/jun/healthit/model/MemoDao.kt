package io.jun.healthit.model

import androidx.lifecycle.LiveData
import androidx.room.*
import io.jun.healthit.model.data.Memo

@Dao
interface MemoDao {
    @Query("SELECT * FROM memo")
    fun getAll(): LiveData<List<Memo>>

    @Query("SELECT * FROM memo WHERE id IN (:memoId)")
    suspend fun loadById(memoId: Int): Memo

    @Query("SELECT * FROM memo WHERE date IN (:date)")
    fun isExist(date: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(memo: Memo)

    @Delete
    suspend fun delete(memo: Memo)

    @Update
    suspend fun update(memo: Memo)
}
