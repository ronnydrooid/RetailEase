package com.example.retailease.ui.screens.wholesale.khatabook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retailease.models.Salesman
import com.example.retailease.repository.SalesmanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SalesmanKhataViewModel @Inject constructor(
    private val salesmanRepository: SalesmanRepository,
) : ViewModel() {
    private val _salesmanList = MutableStateFlow(emptyList<Salesman>())
    val salesmanList = _salesmanList.asStateFlow()

    private val _selectedSalesman = MutableStateFlow<Salesman?>(null)
    val selectedSalesman = _selectedSalesman.asStateFlow()

    init {
        observeSalesman()
    }

    private fun observeSalesman() {
        viewModelScope.launch {
            salesmanRepository.getAllSalesmen().collect {
                _salesmanList.value = it
            }
        }
    }

    fun getSalesmanById(salesmanId: Int) {
        viewModelScope.launch {
            _selectedSalesman.value = salesmanRepository.getSalesmanById(salesmanId)
        }
    }


}