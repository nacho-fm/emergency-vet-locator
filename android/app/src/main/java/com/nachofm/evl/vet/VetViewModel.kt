package com.nachofm.evl.vet

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class VetViewModel(private val repository: VetRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allWords: LiveData<List<Vet>> = repository.allWords.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(vet: Vet) = viewModelScope.launch {
        repository.insert(vet)
    }
}

class VetViewModelFactory(private val repository: VetRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VetViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
