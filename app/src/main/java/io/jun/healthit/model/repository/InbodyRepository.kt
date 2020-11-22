package io.jun.healthit.model.repository

import androidx.lifecycle.LiveData
import io.jun.healthit.model.InbodyDao
import io.jun.healthit.model.data.Inbody

class InbodyRepository(private val inbodyDao: InbodyDao) {

    val allInbodys: LiveData<List<Inbody>> = inbodyDao.getAll()

    suspend fun insert(inbody: Inbody) = inbodyDao.insert(inbody)

    suspend fun update(inbody: Inbody) = inbodyDao.update(inbody)

    suspend fun delete(inbody: Inbody) = inbodyDao.delete(inbody)
}