package com.example.retailease.ui.screens.wholesale.calculator.calculatorViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retailease.models.DraftOrder
import com.example.retailease.repository.DraftOrderItemRepository
import com.example.retailease.repository.DraftOrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DraftOrderHistoryViewModel @Inject constructor(
    private val draftOrderRepository: DraftOrderRepository,
    private val draftOrderItemRepository: DraftOrderItemRepository,
) : ViewModel() {

    private val _draftOrders = MutableStateFlow(emptyList<DraftOrder>())
    val draftOrders = _draftOrders.asStateFlow()

    init {
        observeDraftOrders()
    }

    private fun observeDraftOrders() {
        viewModelScope.launch {
            draftOrderRepository.getDraftOrders().collect{
                _draftOrders.value = it
            }
        }
    }

    fun deleteDraftOrder(draftOrder: DraftOrder) {
        viewModelScope.launch {
            draftOrderRepository.deleteDraftOrder(draftOrder)
        }
    }
}