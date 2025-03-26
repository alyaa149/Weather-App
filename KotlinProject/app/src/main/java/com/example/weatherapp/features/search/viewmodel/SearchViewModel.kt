package com.example.weatherapp.features.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.models.City
import com.example.weatherapp.data.repo.Repo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch



class SearchViewModel(private val repository: Repo) : ViewModel() {
    private val _searchQuery = MutableSharedFlow<String>(replay = 1)

//    init {
//        viewModelScope.launch {
//            _searchQuery
//                .debounce(500)
//                .distinctUntilChanged()
//                .collectLatest { query ->
//                    if (query.isNotBlank()) {
//                        repository.searchCities(query) // ✅ Directly call repo
//                    }
//                }
//        }
//    }
//
//    fun onSearchQueryChanged(query: String) {
//        viewModelScope.launch {
//            _searchQuery.emit(query) // ✅ Emits user input
//        }
 //   }
}

class SearchViewModelFactory(private val repo: Repo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
