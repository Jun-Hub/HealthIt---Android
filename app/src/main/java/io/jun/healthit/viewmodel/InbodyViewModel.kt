package io.jun.healthit.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import io.jun.healthit.model.MemoDatabase
import io.jun.healthit.model.data.Inbody
import io.jun.healthit.model.repository.InbodyRepository
import kotlinx.coroutines.launch

class InbodyViewModel(application: Application): AndroidViewModel(application) {

    private val repository: InbodyRepository
    val allInbodys: LiveData<List<Inbody>>

    init {
        val inbodysDao = MemoDatabase.getDatabase(application).inbodyDao()
        repository = InbodyRepository(inbodysDao)
        allInbodys = repository.allInbodys
    }

    fun insert(inbody: Inbody) = viewModelScope.launch { repository.insert(inbody) }

    fun update(inbody: Inbody) = viewModelScope.launch { repository.update(inbody) }

    fun delete(inbody: Inbody) = viewModelScope.launch { repository.delete(inbody) }

}