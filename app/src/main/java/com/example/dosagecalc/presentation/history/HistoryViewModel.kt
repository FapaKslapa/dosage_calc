package com.example.dosagecalc.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.dosagecalc.domain.model.HistoryRecord
import com.example.dosagecalc.domain.usecase.ManageHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val records: List<HistoryRecord> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val manageHistoryUseCase: ManageHistoryUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    val historyPaged: Flow<PagingData<HistoryRecord>> = _searchQuery
        .flatMapLatest { query -> manageHistoryUseCase.getAllHistoryPaged(query) }
        .cachedIn(viewModelScope)

    init {
        _uiState.update { it.copy(isLoading = false) }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun deleteRecord(recordId: String) {
        viewModelScope.launch {
            manageHistoryUseCase.deleteHistoryRecord(recordId)
        }
    }

    fun getAllHistory(onResult: (List<HistoryRecord>) -> Unit) {
        viewModelScope.launch {
            manageHistoryUseCase.getAllHistory().collect { list ->
                onResult(list)
            }
        }
    }
}
