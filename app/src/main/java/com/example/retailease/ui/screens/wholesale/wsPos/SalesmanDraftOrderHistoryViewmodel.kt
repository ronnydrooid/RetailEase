package com.example.retailease.ui.screens.wholesale.wsPos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retailease.models.SalesmanDraftOrder
import com.example.retailease.repository.SalesmanDraftOrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SalesmanDraftOrderHistoryViewmodel @Inject constructor(private val salesmanDraftOrderRepository: SalesmanDraftOrderRepository) :
    ViewModel() {

    private val _salesmanDraftOrders = MutableStateFlow(emptyList<SalesmanDraftOrder>())
    val salesmanDraftOrders = _salesmanDraftOrders.asStateFlow()

    init {
        observeDraftOrders()
    }

    private fun observeDraftOrders() {
        viewModelScope.launch {
            salesmanDraftOrderRepository.getSalesmanDraftOrders().collect{
                _salesmanDraftOrders.value = it
            }
        }
    }

    fun deleteDraftOrder(salesmanDraftOrder: SalesmanDraftOrder) {
        viewModelScope.launch {
            salesmanDraftOrderRepository.deleteSalesmanDraftOrder(salesmanDraftOrder)
        }
    }
}

