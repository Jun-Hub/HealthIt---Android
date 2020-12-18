package io.jun.healthit.model.repository

import androidx.lifecycle.LiveData
import io.jun.healthit.model.MemoDao
import io.jun.healthit.model.data.Memo

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class MemoRepository(private val memoDao: MemoDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allMemos: LiveData<List<Memo>> = memoDao.getAll()

    suspend fun insert(memo: Memo) = memoDao.insert(memo)

    suspend fun update(memo: Memo) = memoDao.update(memo)

    suspend fun delete(memo: Memo) = memoDao.delete(memo)

    suspend fun getMemoById(id: Int) = memoDao.loadById(id)

    fun isExist(date: String) = memoDao.isExist(date)
}