package com.example.retailease.ui.screens.wholesale.khatabook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retailease.models.SalesmanLedger
import com.example.retailease.repository.LedgerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class KhatabookViewModel @Inject constructor(
    private val ledgerRepository: LedgerRepository
) : ViewModel() {

    private val _ledgerEntries = MutableStateFlow<List<SalesmanLedger>>(emptyList())
    val ledgerEntries: StateFlow<List<SalesmanLedger>> = _ledgerEntries.asStateFlow()

    private val _currentBalance = MutableStateFlow(BigDecimal.ZERO)
    val currentBalance: StateFlow<BigDecimal> = _currentBalance.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadLedgerForSalesman(salesmanId: Int) {
        _isLoading.value = true

        // Launch separate coroutine for balance
        viewModelScope.launch {
            try {
                val balance = ledgerRepository.getCurrentBalance(salesmanId)
                _currentBalance.value = balance
            } catch (e: Exception) {
                _currentBalance.value = BigDecimal.ZERO
            }
        }

        // Launch separate coroutine for ledger entries collection
        viewModelScope.launch {
            try {
                ledgerRepository.getLedgerBySalesman(salesmanId).collect { entries ->
                    _ledgerEntries.value = entries
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _ledgerEntries.value = emptyList()
                _isLoading.value = false
            }
        }
    }

    fun refreshLedger(salesmanId: Int) {
        loadLedgerForSalesman(salesmanId)
    }

    fun insertLedgerEntry(ledger: SalesmanLedger){
        viewModelScope.launch {
            ledgerRepository.insertLedgerEntry(ledger)
        }
    }
}