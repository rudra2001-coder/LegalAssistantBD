package com.rudra.legalassistantbd.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudra.legalassistantbd.core.database.entity.LawSectionEntity
import com.rudra.legalassistantbd.search.BengaliSearchEngine
import com.rudra.legalassistantbd.search.SearchIndexer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchState(
    val query: String = "",
    val results: List<LawSectionEntity> = emptyList(),
    val isSearching: Boolean = false,
    val hasSearched: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchIndexer: SearchIndexer,
    private val bengaliSearchEngine: BengaliSearchEngine
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChanged(query: String) {
        _state.update { it.copy(query = query) }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            if (query.isNotBlank()) {
                _state.update { it.copy(isSearching = true, hasSearched = true) }
                searchIndexer.search(query).collect { results ->
                    _state.update {
                        it.copy(results = results, isSearching = false)
                    }
                }
            } else {
                _state.update { it.copy(results = emptyList(), isSearching = false, hasSearched = false) }
            }
        }
    }

    fun isBengali(text: String): Boolean = bengaliSearchEngine.isBengali(text)
}
