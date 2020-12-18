package io.jun.healthit.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import io.jun.healthit.model.data.Memo
import io.jun.healthit.model.MemoDatabase
import io.jun.healthit.model.repository.MemoRepository
import io.jun.healthit.view.fragment.MemoFragment
import kotlinx.coroutines.launch

// Class extends AndroidViewModel and requires application as a parameter.
class MemoViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: MemoRepository
    // LiveData gives us updated words when they change.
    val allMemos: LiveData<List<Memo>>

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val memosDao = MemoDatabase.getDatabase(application).memoDao()
        repository = MemoRepository(memosDao)
        allMemos = repository.allMemos
    }

    fun insert(memo: Memo) = viewModelScope.launch { repository.insert(memo) }

    fun update(memo: Memo) = viewModelScope.launch { repository.update(memo) }

    fun delete(memo: Memo) = viewModelScope.launch { repository.delete(memo) }

    fun getMemoById(id: Int, callback: (Memo) -> Unit) = viewModelScope.launch { callback(repository.getMemoById(id)) }

    fun isExist(date: String) = repository.isExist(date)

    fun isEditMode() = MemoFragment.editOn
}