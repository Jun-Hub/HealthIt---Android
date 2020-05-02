package io.jun.healthit.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MemoDao {
    @Query("SELECT * FROM memo")
    fun getAll(): LiveData<List<Memo>>

    @Query("SELECT * FROM memo WHERE id IN (:memoId)")
    fun loadById(memoId: Int): LiveData<Memo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(memo: Memo)

    @Delete
    suspend fun delete(memo: Memo)

    @Update
    suspend fun update(memo: Memo)
}
