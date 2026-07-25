package com.lelestacia.tkmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lelestacia.tkmanagement.data.relation.TunggakanItem
import com.lelestacia.tkmanagement.data.repository.CashRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TunggakanViewModel(private val repository: CashRepository) : ViewModel() {

    private val _list = MutableStateFlow<List<TunggakanItem>>(emptyList())
    val list: StateFlow<List<TunggakanItem>> = _list.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getTunggakanList().collectLatest { _list.value = it }
        }
    }
}
