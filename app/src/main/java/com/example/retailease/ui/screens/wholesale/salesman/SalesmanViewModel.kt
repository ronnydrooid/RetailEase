package com.example.retailease.ui.screens.wholesale.salesman

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retailease.datasource.preferences.PreferencesManager
import com.example.retailease.models.Salesman
import com.example.retailease.repository.SalesmanDraftOrderItemRepository
import com.example.retailease.repository.SalesmanRepository
import com.example.retailease.ui.components.PrintManager
import com.example.retailease.ui.screens.wholesale.calculator.calculatorViewModels.CalculatorOrderItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SalesmanViewModel @Inject constructor(
    private val salesmanRepository: SalesmanRepository,
    private val preferencesManager: PreferencesManager,
) : ViewModel() {

    val openCashDrawer = preferencesManager.openCashDrawerFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        false
    )

    private val _salesmanList = MutableStateFlow(emptyList<Salesman>())
    val salesmanList = _salesmanList.asStateFlow()

    private val _selectedSalesman = MutableStateFlow<Salesman?>(null)
    val selectedSalesman = _selectedSalesman.asStateFlow()

    private val _cartDetails = MutableStateFlow<Boolean>(false)
    val cartDetails = _cartDetails.asStateFlow()

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

    fun printOrderByBluetooth(context: Context, printItems: String) {
        viewModelScope.launch {
            try {
                PrintManager(context).printTextViaBluetooth(printItems, openCashDrawer.value).fold(

                    onSuccess = { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    },

                    onFailure = {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }

                )
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getSalesmanById(salesmanId: Int) {
        viewModelScope.launch {
            _selectedSalesman.value = salesmanRepository.getSalesmanById(salesmanId)
        }
    }

    fun dismissCartDetails() {
        _cartDetails.value = false
    }

    fun toggleCartDetails() {
        _cartDetails.value = !_cartDetails.value
    }

    fun clearCart() {

    }


}