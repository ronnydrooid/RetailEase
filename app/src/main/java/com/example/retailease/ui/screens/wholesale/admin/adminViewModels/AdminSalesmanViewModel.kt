package com.example.retailease.ui.screens.wholesale.admin.adminViewModels

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
class AdminSalesmanViewModel @Inject constructor(private val salesmanRepository: SalesmanRepository) :
    ViewModel() {

    private val _salesmanList = MutableStateFlow(emptyList<Salesman>())
    val salesmanList = _salesmanList.asStateFlow()

    private val _selectedSalesman = MutableStateFlow<Salesman?>(null)
    val selectedSalesman = _selectedSalesman.asStateFlow()

    init {
        observeSalesmanList()
    }

    private fun observeSalesmanList() {
        viewModelScope.launch {
            salesmanRepository.getAllSalesmen().collect { salesmen ->
                _salesmanList.value = salesmen
            }

        }
    }

    fun getSalesmanById(salesmanId: Int) {
        viewModelScope.launch {
            _selectedSalesman.value = salesmanRepository.getSalesmanById(salesmanId)
        }
    }

    fun insertSalesman(salesman: Salesman) {
        viewModelScope.launch {
            salesmanRepository.insertSalesman(salesman)
        }
    }

    fun deleteSalesman(salesman: Salesman) {
        viewModelScope.launch {
            salesmanRepository.deleteSalesman(salesman)
        }
    }
}